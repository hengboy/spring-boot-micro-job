/*
 *    Copyright [2019] [恒宇少年 - 于起宇]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.hengboy.job.autoconfigure.schedule;

import com.github.hengboy.job.autoconfigure.registry.MicroJobRegistryProperties;
import com.github.hengboy.job.core.http.MicroJobRestTemplate;
import com.github.hengboy.job.schedule.ScheduleFactoryBean;
import com.github.hengboy.job.schedule.store.DefaultJobStore;
import com.github.hengboy.job.schedule.store.JobStore;
import com.github.hengboy.job.schedule.store.customizer.JobStoreCustomizer;
import org.quartz.Scheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 分布式调度器自动化配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-28 10:37
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Configuration
@EnableConfigurationProperties({MicroJobScheduleProperties.class, MicroJobRegistryProperties.class, ServerProperties.class})
@ConditionalOnClass({Scheduler.class, ScheduleFactoryBean.class})
public class MicroJobScheduleAutoConfiguration {
    /**
     * 调度器属性配置
     */
    private MicroJobScheduleProperties microJobScheduleProperties;
    /**
     * 任务注册中心属性配置
     */
    private MicroJobRegistryProperties microJobRegistryProperties;
    /**
     * server属性配置
     */
    private ServerProperties serverProperties;
    /**
     * 自定义jobStore配置
     */
    private final ObjectProvider<JobStoreCustomizer> customizers;

    /**
     * 构造函数初始化注入对象信息
     *
     * @param microJobScheduleProperties 调度配置文件内容
     * @param customizers                自定义jobStore配置实现
     */
    public MicroJobScheduleAutoConfiguration(MicroJobScheduleProperties microJobScheduleProperties, MicroJobRegistryProperties microJobRegistryProperties, ServerProperties serverProperties, ObjectProvider<JobStoreCustomizer> customizers) {
        this.microJobScheduleProperties = microJobScheduleProperties;
        this.microJobRegistryProperties = microJobRegistryProperties;
        this.serverProperties = serverProperties;
        this.customizers = customizers;
    }

    /**
     * 数据源自定义配置
     *
     * @param dataSource 数据源配置
     * @return
     */
    @Bean
    @ConditionalOnBean(DataSource.class)
    JobStoreCustomizer dataSourceJobStoreCustomizer(DataSource dataSource) {
        return jobStore -> {
            DefaultJobStore defaultJobStore = (DefaultJobStore) jobStore;
            defaultJobStore.setDataSource(dataSource);
        };
    }

    /**
     * 实例化任务数据源对象
     * 配置使用数据库方式
     *
     * @return
     */
    @Bean
    JobStore jobStore() {
        // 默认任务数据源
        DefaultJobStore defaultJobStore = new DefaultJobStore();

        // 数据库方式任务数据源
        if (JobStoreType.JDBC.toString().equals(microJobScheduleProperties.getJobStoreType().toString())) {
            defaultJobStore.setDelegateClassName("com.github.hengboy.job.schedule.store.delegate.JdbcSqlDelegate");
        }

        // 如果存在自定义配置类
        this.customize(defaultJobStore);

        return defaultJobStore;
    }

    /**
     * 创建任务调度工厂对象
     *
     * @return
     */
    @Bean
    ScheduleFactoryBean microJobScheduleFactoryBean() {
        ScheduleFactoryBean factoryBean = new ScheduleFactoryBean();
        factoryBean.setHeartDelaySeconds(microJobScheduleProperties.getHeartDelaySeconds());

        // 负载均衡权重
        factoryBean.setLoadBalanceWeight(microJobScheduleProperties.getLoadBalanceWeight());
        factoryBean.setMaxRetryTimes(microJobScheduleProperties.getMaxRetryTimes());

        // 设置任务注册中心配置信息
        factoryBean.setRegistryIpAddress(microJobRegistryProperties.getIpAddress());
        factoryBean.setRegistryPort(microJobRegistryProperties.getPort());

        // 设置端口号
        factoryBean.setPort(serverProperties.getPort());

        return factoryBean;
    }


    /**
     * 任务数据源的自定义配置
     *
     * @param jobStore 任务数据源
     */
    private void customize(JobStore jobStore) {
        this.customizers.orderedStream().forEach((customizer) -> customizer.customize(jobStore));
    }

    /**
     * 实例化restTemplate
     * 用于消费者、提供者、调度器、注册中心ws请求交互
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public MicroJobRestTemplate restTemplate() {
        return new MicroJobRestTemplate();
    }
}
