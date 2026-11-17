# ddd-archetype-scaffold-jdk8 使用指南

一套基于 DDD（领域驱动设计）分层的 Spring Boot 工程脚手架，JDK 8 + Spring Boot 2.7.12。
本文面向 Java 新手，照着一步步做即可生成并启动自己的项目。

---

## 一、这个脚手架是什么

它是一个"项目模板"。正常新建一个 DDD 项目需要手工建 7 个模块、配几十个依赖，用本脚手架执行一条命令即可自动生成完整骨架，你只需专心写业务代码。

| 模块 | 作用 | 里面放什么 |
|------|------|-----------|
| `xxx-type` | 通用类型 | 响应体、分页对象、基础异常、通用工具类（`type.utils` 包） |
| `xxx-application` | 应用层 | 用例编排（组合领域服务） |
| `xxx-domain` | 领域层 | 实体、领域服务、端口接口 |
| `xxx-infrastructure` | 基础设施层 | 端口实现、数据库访问、Spring 工具 |
| `xxx-trigger` | 触发层 | Controller（HTTP 接口入口） |
| `xxx-openapi` | 开放接口 | 对外暴露的 API 定义 |
| `xxx-start` | 启动模块 | 启动类、配置文件，打可运行 jar |

> `xxx` 是你生成项目时起的 `artifactId`，生成后模块名会自动替换。

---

## 二、环境要求（先检查，再往下做）

| 必备 | 版本 | 检查命令（终端里执行） | 期望输出示例 |
|------|------|----------------------|-------------|
| JDK | 1.8 | `java -version` | `1.8.0_xxx` |
| Maven | 3.5+ | `mvn -v` | `Apache Maven 3.8.x` |
| IDE | IntelliJ IDEA（推荐） | — | — |
| Nacos | 2.x（服务端） | 浏览器打开 Nacos 控制台能访问 | 登录页正常显示 |

注意：
- JDK 必须是 **8**，用 11/17 会编译报错（依赖了 JDK 8 专有的 JVM 参数和库）。
- 本脚手架**强依赖 Nacos**（注册中心 + 配置中心），没有 Nacos 服务端无法启动。公司有就找运维要地址，自己搭见 [Nacos 快速开始](https://nacos.io/docs/latest/#/quickstart)。

---

## 三、快速开始（推荐方式：archetype 生成新项目）

### 第 1 步：安装脚手架到本地仓库

在本工程根目录（`ddd-archetype-scaffold-jdk8/`）依次执行 2 条命令：

```bash
# 1. 生成脚手架（看到 BUILD SUCCESS 即成功）
mvn archetype:create-from-project

# 2. 进入生成的脚手架目录并安装到本地仓库
cd target/generated-sources/archetype
mvn clean install
```

安装成功后，本地仓库（`~/.m2/repository/cn/zachqie/scaffold/`）会出现
`ddd-archetype-scaffold-jdk8-archetype-1.0.0.jar`，之后就能反复用它生成项目。

### 第 2 步：用脚手架生成你的新项目

先想好三个名字（下面用示例值，替换成你自己的）：

| 参数 | 含义 | 示例值 |
|------|------|--------|
| `-DgroupId` | 组织坐标，一般是公司域名倒写 | `cn.mycompany` |
| `-DartifactId` | 项目名（小写，单词间用 `-`） | `order-service` |
| `-Dpackage` | Java 包名 | `cn.mycompany.order` |

在一个**空目录**下执行（一条命令，不用换行也可以）：

```bash
mvn archetype:generate -B \
  -DarchetypeGroupId=cn.zachqie.scaffold \
  -DarchetypeArtifactId=ddd-archetype-scaffold-jdk8-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=cn.mycompany \
  -DartifactId=order-service \
  -Dversion=1.0.0 \
  -Dpackage=cn.mycompany.order
```

执行完当前目录会出现 `order-service/`，里面 7 个模块名已自动变为
`order-service-type`、`order-service-start` 等，包名也全部替换完毕，**不需要再手工改名**。

### 第 3 步：编译验证生成结果

```bash
cd order-service
mvn clean package -DskipTests
```

看到最后一行 `BUILD SUCCESS` 就说明骨架没问题。如果报错，回到第 1 步重新安装脚手架再试。

### 第 4 步：用 IDEA 打开项目

1. IDEA → `File` → `Open` → 选择 `order-service/pom.xml` → `Open as Project`
2. 等右下角 Maven 依赖下载完（第一次可能几分钟）
3. 找到启动类：`order-service-start` 模块 → `src/main/java` → `cn.mycompany.order` → `Application`，类名上有绿色三角 ▶

### 第 5 步：修改配置（连上你自己的 Nacos）

启动类**现在直接点运行会连不上 Nacos 而启动失败**，先改配置：

1. 打开 `order-service-start/src/main/resources/bootstrap-dev.yml`
2. 把 `server-addr` 两处都改成你的 Nacos 地址（IP:端口）：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 你的Nacos地址:8848   # 服务注册
        namespace: 你的namespace           # 没有就删掉这行
        group: 你的group                   # 没有就删掉这行
      config:
        server-addr: 你的Nacos地址:8848   # 配置中心（和上面保持一致）
        namespace: 你的namespace
        group: 你的group
        file-extension: yml
        shared-configs:
          - data-id: application.yml
            group: 你的group
            refresh: true
```

3. 在 Nacos 控制台新建配置（`配置管理` → `配置列表` → `+`），Data ID 填 `application-dev.yml`，Group 与上面一致，格式选 `YAML`，内容可参考本仓库 `docs/nacos/application-dev.yml`（含数据库连接、端口 10001 等），至少把数据库 `url/username/password` 改成你自己的。

> `docs/nacos/` 下有现成的 `application.yml` 和 `application-dev.yml` 样例，直接复制到 Nacos 里改数据库四项（url、用户名、密码、驱动）即可用。

### 第 6 步：启动并验证

1. IDEA 里点 `Application` 类的 ▶ 启动，控制台无 ERROR、看到 Tomcat started 即成功
2. 浏览器访问健康检查：

```
http://localhost:10001/ddd-jdk8/health
```

> 端口 `10001` 和路径前缀 `/ddd-jdk8` 来自 Nacos 里 `application-dev.yml` 的 `server.port` 和 `server.servlet.context-path`。返回 `Hello HealthController!` 即一切正常。

---

## 四、方式二：不生成新项目，直接用本工程

如果不需要新项目名，也可以直接在本工程上开发：

```bash
git clone <你的仓库地址>
cd ddd-archetype-scaffold-jdk8
mvn clean package -DskipTests
```

然后用 IDEA 打开（同第 4 步）、改 Nacos 配置（同第 5 步）、启动验证（同第 6 步）。

---

## 五、发布脚手架到远程仓库（团队共享，可选）

上面的方式只装到你自己的电脑。想让同事直接用，需把 archetype 发布到公司私服。

1. 在 `target/generated-sources/archetype/pom.xml` 中加入（仓库地址问运维要）：

```xml
<distributionManagement>
    <repository>
        <id>company_repo</id>
        <url>http://私服地址:8081/repository/company_repo/</url>
    </repository>
</distributionManagement>
```

2. 发布：

```bash
cd target/generated-sources/archetype
mvn clean deploy
```

3. 同事使用时在 `mvn archetype:generate` 命令里加 `-DarchetypeCatalog=remote`，其余参数同第 2 步。

---

## 六、常见问题（FAQ）

| 现象 | 原因 | 解决 |
|------|------|------|
| 编译报错含 `invalid target release` | JDK 不是 8 | IDEA：`File → Project Structure` 把 SDK 切回 1.8 |
| 启动卡在 `nacos` 相关日志后失败 | Nacos 地址不通 | 回到第 5 步检查 `server-addr`，浏览器确认 Nacos 能打开 |
| 启动报数据库连接错误 | 数据源配置不对 | Nacos 配置里检查数据库四项；本脚手架默认 MySQL 8 驱动 |
| `mvn archetype:generate` 找不到 archetype | 脚手架没装到本地 | 回到第 1 步，确认两次 `BUILD SUCCESS` |
| 命令行 mvn 正常但 IDEA 下载依赖慢 | IDEA 使用了内置 Maven | IDEA 设置里 Maven home 指向本机 Maven，`settings.xml` 配国内镜像 |
| 生成项目后想换包名 | — | 删掉重新生成最快（`rm -rf` 后回到第 2 步） |

---

## 七、附录

- 工程坐标：`cn.zachqie.scaffold:ddd-archetype-scaffold-jdk8:1.0.0`
- 生成出的 archetype 坐标：`cn.zachqie.scaffold:ddd-archetype-scaffold-jdk8-archetype:1.0.0`
- `docs/nacos/`：Nacos 配置样例（application.yml、application-dev.yml）
- `docs/bin/`：启动/停止/重启脚本（start.sh、stop.sh、restart.sh），部署 Linux 服务器时放到 jar 同级目录使用
- `docs/sql/`：预留的数据库脚本目录，建表语句放这里
