# 依赖与兼容性

V0.2 新增 Spring AI 1.1.8（通过 BOM 管理）与 Spring Boot Actuator；继续使用 Java 21 编译目标、Boot 3.5.16、现有 MySQL 和前端依赖。模型提供商为 DeepSeek，不需要额外 embedding SDK。兼容依据与运行配置见 [AI_GUIDE.md](AI_GUIDE.md)。

2026-10-27 检查，使用稳定发布版本，npm 安装生成锁文件，不包含 preview / beta / experimental 依赖。

| 层 | 版本 |
| --- | --- |
| Java 编译目标 | 21 |
| Spring Boot | 3.5.16 |
| MyBatis Spring Boot Starter | 3.0.5 |
| springdoc | 2.8.17 |
| MySQL | 8.x；本机测试 8.0.30，Compose 默认 8.4 |
| Vue / Router / Pinia | 3.5.44 / 4.6.3 / 3.0.4 |
| Vite / Vue 插件 | 8.3.5 / 6.0.10 |
| TypeScript / vue-tsc | 5.9.3 / 3.3.13 |
| Tailwind | 4.3.3，使用官方 Vite 插件 |
| Axios | 1.20.0 |
| Vitest / Playwright | 5.0.5 / 1.64.1 |

Flyway、MySQL JDBC 驱动、JUnit 等由 Spring Boot BOM 管理，避免手动错配。Vite 项目要求 Node >=22.12；本机使用 Node 24.15。后端本机运行于 Java 24，编译 release 21；CI 另设 Java 21。CI 配置已提供，不把尚未执行的远程结果算作本机验证。

官方依据：

- [Spring Boot 3.5 系统要求](https://docs.spring.io/spring-boot/3.5/system-requirements.html)：支持 Java 21。
- [MyBatis Starter 兼容矩阵](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)：3.0 适配 Spring Boot 3.2–3.5。
- [springdoc v2](https://springdoc.org/v2/)：Spring Boot 3.5 对应 2.8.x 等兼容系列。
- [Vite 环境要求](https://vite.dev/guide/)：Node 20.19+、22.12+；本项目统一使用 22.12 以上。

npm 安装审计结果为零已知漏洞。此为安装时结果，不能代替持续依赖维护。
