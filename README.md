# seata-example说明

## 一、项目介绍

Spring boot 2.2.4.RELEASE + Spring cloud Hoxton.SR1 + Spring cloud alibaba 2.2.0.RELEASE

Seata-server-1.3.0 + OpenFeign + Eureka + MybatisPlus 等



| 应用包名称           | 端口号 | 备注                                                       |
| -------------------- | ------ | ---------------------------------------------------------- |
| eureka               | 10086  | 注册中心                                                   |
| seata-sample         | 8080   | 使用Seata控制分布式事务，单服务集成MybatisPlus多数据源例子 |
| seata-sample-account | 9090   | 账户服务                                                   |
| seata-sample-order   | 9091   | 订单服务                                                   |
| seata-sample-product | 9092   | 商品服务                                                   |
| seata-server-1.3.0   | 8091   | seata TC Server  事务协调者                                |



![](https://cdn.jsdelivr.net/gh/LeeYifeifei/privateImages/images/image-20200923152639975.png)



## 二、Seata简介

### 1.概述

Seata是阿里开源的一款开源的**分布式事务**解决方案，致力于提供高性能和简单易用的分布式事务服务。

官方文档：http://seata.io/zh-cn/docs/overview/what-is-seata.html

GitHub：https://github.com/seata/seata

参考文档：

​		Seata 分布式事务实践和开源详解 | GIAC 实录	https://www.sofastack.tech/blog/seata-distributed-transaction-deep-dive/

​		芋道源码	http://www.iocoder.cn/categories/Seata/

### 2.四种事务模式

Seata 目标打造**一站式**的分布事务的解决方案，最终会提供四种事务模式：

- AT 模式：参见[《Seata AT 模式》](https://seata.io/zh-cn/docs/dev/mode/at-mode.html)文档
- TCC 模式：参见[《Seata TCC 模式》](https://seata.io/zh-cn/docs/dev/mode/tcc-mode.html)文档
- Saga 模式：参见[《SEATA Saga 模式》](https://seata.io/zh-cn/docs/dev/mode/saga-mode.html)文档
- XA 模式：参见[《SEATA XA 模式》](https://seata.io/zh-cn/docs/dev/mode/xa-mode.html)文档

目前使用的**流行度**情况是：AT > TCC > Saga。因此，我们在学习 Seata 的时候，可以花更多精力在 **AT 模式**上，最好搞懂背后的实现原理，毕竟分布式事务涉及到数据的正确性，出问题需要快速排查定位并解决。



3.三种角色

![image-20200923154031343](https://cdn.jsdelivr.net/gh/LeeYifeifei/privateImages/images/image-20200923154031343.png)



- **TC** (Transaction Coordinator) - 事务协调者：维护全局和分支事务的状态，驱动**全局事务**提交或回滚。
- **TM** (Transaction Manager) - 事务管理器：定义**全局事务**的范围，开始全局事务、提交或回滚全局事务。
- **RM** ( Resource Manager ) - 资源管理器：管理**分支事务**处理的资源( Resource )，与 TC 交谈以注册分支事务和报告分支事务的状态，并驱动**分支事务**提交或回滚。

其中，TC 为单独部署的 **Server** 服务端，TM 和 RM 为嵌入到应用中的 **Client** 客户端。



在 Seata 中，一个分布式事务的**生命周期**如下：

![image-20200923154238941](https://cdn.jsdelivr.net/gh/LeeYifeifei/privateImages/images/image-20200923154238941.png)



- TM 请求 TC 开启一个全局事务。TC 会生成一个 **XID** 作为该全局事务的编号。

  > **XID**，会在微服务的调用链路中传播，保证将多个微服务的子事务关联在一起。

- RM 请求 TC 将本地事务注册为全局事务的分支事务，通过全局事务的 **XID** 进行关联。

- TM 请求 TC 告诉 **XID** 对应的全局事务是进行提交还是回滚。

- TC 驱动 RM 们将 **XID** 对应的自己的本地事务进行提交还是回滚。



### 3.部署单机Seata TC Server

因为 TC 需要进行全局事务和分支事务的记录，所以需要对应的**存储**。目前，TC 有两种存储模式( `store.mode` )：

- file 模式：适合**单机**模式，全局事务会话信息在**内存**中读写，并持久化本地文件`root.data`，性能较高。
- db 模式：适合**集群**模式，全局事务会话信息通过 **db** 共享，相对性能差点。



执行 `nohup sh bin/seata-server.sh &` 命令，启动 TC Server 在后台。在 `nohup.out` 文件中，可以看到启动日志

### 4.部署集群Seata TC Server

部署**集群** Seata **TC** Server，实现高可用，生产环境下必备。在集群时，多个 Seata TC Server 通过 **db** 数据库，实现全局事务会话信息的共享。

同时，每个 Seata TC Server 可以注册自己到注册中心上，方便应用从注册中心获得到他们。最终我们部署 集群 TC Server 如下图所示：

![image-20200923155027637](https://cdn.jsdelivr.net/gh/LeeYifeifei/privateImages/images/image-20200923155027637.png)



①初始化数据库

```sql
-- -------------------------------- The script used when storeMode is 'db' --------------------------------
-- the table to store GlobalSession data
CREATE TABLE IF NOT EXISTS `global_table`
(
    `xid`                       VARCHAR(128) NOT NULL,
    `transaction_id`            BIGINT,
    `status`                    TINYINT      NOT NULL,
    `application_id`            VARCHAR(32),
    `transaction_service_group` VARCHAR(32),
    `transaction_name`          VARCHAR(128),
    `timeout`                   INT,
    `begin_time`                BIGINT,
    `application_data`          VARCHAR(2000),
    `gmt_create`                DATETIME,
    `gmt_modified`              DATETIME,
    PRIMARY KEY (`xid`),
    KEY `idx_gmt_modified_status` (`gmt_modified`, `status`),
    KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- the table to store BranchSession data
CREATE TABLE IF NOT EXISTS `branch_table`
(
    `branch_id`         BIGINT       NOT NULL,
    `xid`               VARCHAR(128) NOT NULL,
    `transaction_id`    BIGINT,
    `resource_group_id` VARCHAR(32),
    `resource_id`       VARCHAR(256),
    `branch_type`       VARCHAR(8),
    `status`            TINYINT,
    `client_id`         VARCHAR(64),
    `application_data`  VARCHAR(2000),
    `gmt_create`        DATETIME(6),
    `gmt_modified`      DATETIME(6),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- the table to store lock data
CREATE TABLE IF NOT EXISTS `lock_table`
(
    `row_key`        VARCHAR(128) NOT NULL,
    `xid`            VARCHAR(96),
    `transaction_id` BIGINT,
    `branch_id`      BIGINT       NOT NULL,
    `resource_id`    VARCHAR(256),
    `table_name`     VARCHAR(32),
    `pk`             VARCHAR(36),
    `gmt_create`     DATETIME,
    `gmt_modified`   DATETIME,
    PRIMARY KEY (`row_key`),
    KEY `idx_branch_id` (`branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
```



②修改 `conf/file` 配置文件，修改使用 db 数据库，实现 Seata TC Server 的全局事务会话信息的共享。

设置store.mode为db模式，修改数据库的Url，驱动，账号、密码



④修改 `conf/registry.conf` 配置文件，设置使用 eureka 注册中心。

设置registry.type为eureka，设置eureka地址



启动 TC Server

① 执行 `nohup sh bin/seata-server.sh -p 18091 -n 1 &` 命令，启动**第一个** TC Server 在后台。

- `-p`：Seata TC Server 监听的端口。
- `-n`：Server node。在多个 TC Server 时，需区分各自节点，用于生成不同区间的 transactionId 事务编号，以免冲突。

在 `nohup.out` 文件中，可以看到启动日志

② 执行 `nohup sh bin/seata-server.sh -p 28091 -n 2 &` 命令，启动**第二个** TC Server 在后台。

③ 打开注册中心，我们可以看到有**两个** Seata TC Server 示例



## 三、附件

**其中，每个库中的 `undo_log` 表，是 Seata AT 模式必须创建的表，主要用于分支事务的回滚。**

#### scheam-account.sql

```sql
DROP TABLE IF EXISTS account;
CREATE TABLE account
(
    id               INT(11) NOT NULL AUTO_INCREMENT,
    balance          DOUBLE   DEFAULT NULL,
    last_update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE undo_log
(
    id            BIGINT(20) NOT NULL AUTO_INCREMENT,
    branch_id     BIGINT(20) NOT NULL,
    xid           VARCHAR(100) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info LONGBLOB     NOT NULL,
    log_status    INT(11) NOT NULL,
    log_created   DATETIME     NOT NULL,
    log_modified  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
INSERT INTO account (id, balance)
VALUES (1, 50);
```



#### scheam-order.sql

```sql
DROP TABLE IF EXISTS p_order;
CREATE TABLE p_order
(
    id               INT(11) NOT NULL AUTO_INCREMENT,
    user_id          INT(11) DEFAULT NULL,
    product_id       INT(11) DEFAULT NULL,
    amount           INT(11) DEFAULT NULL,
    total_price      DOUBLE       DEFAULT NULL,
    status           VARCHAR(100) DEFAULT NULL,
    add_time         DATETIME     DEFAULT CURRENT_TIMESTAMP,
    last_update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE undo_log
(
    id            BIGINT(20) NOT NULL AUTO_INCREMENT,
    branch_id     BIGINT(20) NOT NULL,
    xid           VARCHAR(100) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info LONGBLOB     NOT NULL,
    log_status    INT(11) NOT NULL,
    log_created   DATETIME     NOT NULL,
    log_modified  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
```



#### scheam-product.sql

```sql
DROP TABLE IF EXISTS product;
CREATE TABLE product
(
    id               INT(11) NOT NULL AUTO_INCREMENT,
    price            DOUBLE   DEFAULT NULL,
    stock            INT(11) DEFAULT NULL,
    last_update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE undo_log
(
    id            BIGINT(20) NOT NULL AUTO_INCREMENT,
    branch_id     BIGINT(20) NOT NULL,
    xid           VARCHAR(100) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info LONGBLOB     NOT NULL,
    log_status    INT(11) NOT NULL,
    log_created   DATETIME     NOT NULL,
    log_modified  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

INSERT INTO product (id, price, stock)
VALUES (1, 10, 20);
```

