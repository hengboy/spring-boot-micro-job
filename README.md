![](http://job.yuqiyu.com/svgs/logo.svg)

[![Build Status](https://travis-ci.org/hengboy/spring-boot-micro-job.svg?branch=master)](https://travis-ci.org/hengboy/spring-boot-micro-job)[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://github.com/weibocom/motan/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/com.github.hengboy/spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.hengboy%22%20AND%20a:%22spring-boot-starter%22) ![](https://img.shields.io/badge/JDK-1.8+-green.svg) ![](https://img.shields.io/badge/SpringBoot-1.4+_1.5+_2.0+-green.svg) [![](http://job.yuqiyu.com/svgs/chinese.svg)](README_zh.md)

`Micro-job` is a distributed task scheduling and execution framework, which accesses data internally through the Rest path shared by `Jersey` of each component.

Detailed development documentation [Visit official website](http://job.yuqiyu.com/#/) 

> Noun interpretation：
>
> `consumer` -> `Task Consumption Node`
>
> `schedule` -> `Task Scheduler`
>
> `provider` -> `Task Producer`
>
> `registry` -> `Task Registry`

## Registry

` Regisry `serves as the task of registering nodes of each component in the whole ecosystem. The way to implement the task registry is diversified. At present, it includes `memory', `zookeeper', `redis', `consul', etc.

Create the `SpringBoot'project through idea and eclipse tools and add the following dependencies to the `pom.xml' file.

```
<dependency>
    <groupId>com.github.hengboy</groupId>
	<artifactId>spring-boot-starter-registry-memory</artifactId>
	<version>{lastVersion}</version>
</dependency>
```

Add the `application.yml'configuration file to the `resources' resource directory as follows:

```yaml
server:
   port: 9000
hengboy:
  job:
    registry:
      # ask Registry Node Registration
      away: memory
```



## Schedule

Each task is created through a dispatcher to allocate and execute. In the process of allocation, different tasks are consumed by different consumer nodes according to the load balancing strategy configuration of the consumer nodes.
In production tasks, the `scheduler nodes'that perform task scheduling are also filtered according to the `load balancing strategy' of the scheduler.
Create `SpringBoot'project through `idea' and `eclipse'tools and add the following dependencies to the `pom.xml' file.

```xml
<dependency>
    <groupId>com.github.hengboy</groupId>
    <artifactId>spring-boot-starter-schedule</artifactId>
    <version>{lastVersion}</version>
</dependency>
```

Add the `application.yml'configuration file to the `resources' resource directory as follows:

```yaml
server:
   port: 8081
hengboy:
  job:
    registry:
      # Maintain consistency with task registry node registration
      away: memory
    schedule:
      # Memory Scheduler handles task queues and storage of task logs
      job-store-type: memory  
```



## Consumer

Tasks are defined and reported by `consumer'. When `schedule'invokes a consumer to execute a task request, the corresponding task logic method is automatically executed according to `jobKey'.
Create `SpringBoot'project through `idea' and `eclipse'tools and add the following dependencies to the `pom.xml' file.

```xml
<dependency>
	<groupId>com.github.hengboy</groupId>
	<artifactId>spring-boot-starter-consumer</artifactId>
	<version>{lastVersion}</version>
</dependency>
```

Add the `application.yml'configuration file to the `resources' resource directory as follows:

```yaml
server:
   port: 8082
hengboy:
  job:
    registry:
      # Maintain consistency with task registry node registration
      away: memory
```

### Example Of JOB Definition

Let's define a simple `Job', as follows:

```j
@Job(jobExecuteAway = JobExecuteAwayEnum.ONCE)
public class TestJob implements MicroJob {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(TestJob.class);

    @Override
    public JobExecuteResult execute(JobExecuteParam jobExecuteParam) throws JobException {
        logger.info("Key：{}，Param：{}", jobExecuteParam.getJobKey(), jobExecuteParam.getJsonParam());
        return JobExecuteResult.JOB_EXECUTE_SUCCESS;
    }
}
```

> The `Job', as defined above, corresponds to `JobKey', which is `testJob'.



## Provider

The business side adds dependencies and performs `MicroJobProvider. newXxxJob'call creation tasks, such as `Send mail' notification operation after creating an order'.
Create `SpringBoot'project through `idea' and `eclipse'tools and add the following dependencies to the `pom.xml' file.

```xml
<dependency>
	<groupId>com.github.hengboy</groupId>
	<artifactId>spring-boot-starter-provider</artifactId>
	<version>{lastVersion}</version>
</dependency>
```

Add the `application.yml'configuration file to the `resources' resource directory as follows:

```yaml
server:
  port: 8083
hengboy:
  job:
    registry:
      # Maintain consistency with task registry node registration
      away: memory
```

### JOB Execution Example

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderTester {
    /**
     * Registered Task Provider
     */
    @Autowired
    private MicroJobProvider microJobProvider;

    @Test
    public void newJob() {
        // Created tasks are executed only once
        microJobProvider.newOnceJob(OnceJobWrapper.Context()
                // JobKey, which corresponds to tasks defined in consumer, defaults to lowercase class names
                .jobKey("testJob")
                // Customized task queue key, can accurately locate tasks and operate pause, delete and other operations
                .jobQueueKey(UUID.randomUUID().toString())
                // Parameters, parameters of any type, when consumer consumes, are converted to JSON strings
                .param(new HashMap() {
                    {
                        put("name", "admin");
                    }
                })
                .wrapper());
    }
}
```



## Test flow

> 1. Start Task Registry
> 2. Start Task Scheduling Center
> 3. Start Task Consumer Node
> 4. Execute the Provider Tester # newJob unit test method

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
