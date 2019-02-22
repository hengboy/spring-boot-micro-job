[![Build Status](https://travis-ci.org/hengboy/spring-boot-micro-job.svg?branch=master)](https://travis-ci.org/hengboy/spring-boot-micro-job)[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://github.com/weibocom/motan/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/com.github.hengboy/spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.hengboy%22%20AND%20a:%22spring-boot-starter%22) ![](https://img.shields.io/badge/JDK-1.8+-green.svg) ![](https://img.shields.io/badge/SpringBoot-1.4+_1.5+_2.0+-green.svg)

`micro-job`是一款`分布式任务调度执行框架`，内部通过各个组件的`Jersey`共享出的`Rest`路径进行数据访问。

详细开发文档 [访问官网](http://job.yuqiyu.com/#/) 

> 名词解释：
>
> `consumer` -> `任务消费节点`
>
> `schedule` -> `任务调度器`
>
> `provider` -> `任务生产者`
>
> `registry` -> `任务注册中心`

## 任务注册中心（registry）

`registry`是任务注册中心，在整个生态圈内担任着各个组件注册节点的任务，任务注册中心实现方式是多样化的，目前包含：`memory`、`zookeeper`、`redis`、`consul`等。

通过`idea、eclipse`工具创建`SpringBoot`项目并添加如下依赖到`pom.xml`文件内。

```
<dependency>
    <groupId>com.github.hengboy</groupId>
	<artifactId>spring-boot-starter-registry-memory</artifactId>
	<version>{lastVersion}</version>
</dependency>
```

在`resources`资源目录下添加`application.yml`配置文件，配置内容如下所示：

```yaml
server:
   port: 9000
hengboy:
  job:
    registry:
      # 任务注册中心节点注册方式
      away: memory
```



## 任务调度器（schedule）

`schedule`是任务调度器，每一个任务的创建都是通过调度器进行分配执行，分配过程中根据消费节点的负载均衡策略配置进行不同消费者节点任务消费。

在生产任务时，也会根据调度器的`负载均衡策略`来进行筛选执行任务调度的`调度器节点`。

通过`idea、eclipse`工具创建`SpringBoot`项目并添加如下依赖到`pom.xml`文件内。

```xml
<dependency>
    <groupId>com.github.hengboy</groupId>
    <artifactId>spring-boot-starter-schedule</artifactId>
    <version>{lastVersion}</version>
</dependency>
```

在`resources`资源目录下添加`application.yml`配置文件，配置内容如下所示：

```yaml
server:
   port: 8081
hengboy:
  job:
    registry:
      # 保持与任务注册中心节点注册方式一致即可
      away: memory
    schedule:
      # 内存方式调度器处理任务队列以及任务日志的存储
      job-store-type: memory  
```



## 任务消费节点（consumer）

`consumer`是任务消费者执行节点，任务由`consumer`进行定义以及上报，当`schedule`调用消费者执行任务请求时，会自动根据`jobKey`来执行对应的任务逻辑方法。

通过`idea、eclipse`工具创建`SpringBoot`项目并添加如下依赖到`pom.xml`文件内。

```xml
<dependency>
	<groupId>com.github.hengboy</groupId>
	<artifactId>spring-boot-starter-consumer</artifactId>
	<version>{lastVersion}</version>
</dependency>
```

在`resources`资源目录下添加`application.yml`配置文件，配置内容如下所示：

```yaml
server:
   port: 8082
hengboy:
  job:
    registry:
      # 保持与任务注册中心节点注册方式一致即可
      away: memory
```

### 任务定义示例

我们来定义一个简单的`Job`，示例如下所示：

```j
@Job(jobExecuteAway = JobExecuteAwayEnum.ONCE)
public class TestJob implements MicroJob {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(TestJob.class);

    @Override
    public JobExecuteResult execute(JobExecuteParam jobExecuteParam) throws JobException {
        logger.info("执行Key：{}，执行参数：{}", jobExecuteParam.getJobKey(), jobExecuteParam.getJsonParam());
        return JobExecuteResult.JOB_EXECUTE_SUCCESS;
    }
}
```

> 在上面定义的`Job`对应的`JobKey`为`testJob`.



## 任务生产节点（provider）

`provider`是任务生产节点，由业务方进行添加依赖并执行`MicroJobProvider.newXxxJob`调用创建任务，如：`创建订单后`执行`发送邮件`通知操作。

通过`idea、eclipse`工具创建`SpringBoot`项目并添加如下依赖到`pom.xml`文件内。

```xml
<dependency>
	<groupId>com.github.hengboy</groupId>
	<artifactId>spring-boot-starter-provider</artifactId>
	<version>{lastVersion}</version>
</dependency>
```

在`resources`资源目录下添加`application.yml`配置文件，配置内容如下所示：

```yaml
server:
  port: 8083
hengboy:
  job:
    registry:
      # 保持与任务注册中心节点注册方式一致即可
      away: memory
```

### 任务执行示例

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderTester {
    /**
     * 注册任务提供者
     */
    @Autowired
    private MicroJobProvider microJobProvider;

    @Test
    public void newJob() {
        // 创建的任务仅执行一次
        microJobProvider.newOnceJob(OnceJobWrapper.Context()
                // 对应consumer内定义任务的jobKey，默认为类名首字母小写
                .jobKey("testJob")
                // 自定义的任务队列key，可以准确定位任务并操作暂停、删除等操作
                .jobQueueKey(UUID.randomUUID().toString())
                // 参数，任意类型参数，consumer消费时会转换为json字符串
                .param(new HashMap() {
                    {
                        put("name", "admin");
                    }
                })
                .wrapper());
    }
}
```



## 测试流程

> 1. 启动任务注册中心
> 2. 启动任务调度中心
> 3. 启动任务消费者节点
> 4. 执行ProviderTester#newJob单元测试方法

## Folders

```
​```
.
├── micro-job-autoconfigure
├── micro-job-dependencies
├── micro-job-samples
│   ├── sample-consumer
│   ├── sample-provider
│   ├── sample-registry-consul
│   ├── sample-registry-memory
│   ├── sample-registry-redis
│   ├── sample-registry-zookeeper
│   ├── sample-schedule
│   ├── pom.xml
│   └── README.md
├── micro-job-starters
│   ├── spring-boot-starter
│   ├── spring-boot-starter-provider
│   ├── spring-boot-starter-registry-consul
│   ├── spring-boot-starter-registry-memory
│   ├── spring-boot-starter-registry-redis
│   ├── spring-boot-starter-registry-zookeeper
│   ├── spring-boot-starter-schedule
│   └── pom.xml
├── .travis.yml
├── LICENSE
├── pom.xml
└── README.md
​```
```

## License

The Apache License