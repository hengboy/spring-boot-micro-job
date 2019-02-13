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

import com.github.hengboy.job.schedule.ScheduleFactoryBean;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.*;
import org.springframework.boot.autoconfigure.transaction.PlatformTransactionManagerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-31 11:34
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class, PlatformTransactionManagerCustomizer.class, ScheduleFactoryBean.class})
@EnableConfigurationProperties({QuartzProperties.class, MicroJobScheduleProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class MicroJobQuartzAutoConfiguration {
    private final QuartzProperties properties;
    private final MicroJobScheduleProperties microJobScheduleProperties;
    private final ObjectProvider<SchedulerFactoryBeanCustomizer> customizers;
    private final JobDetail[] jobDetails;
    private final Map<String, Calendar> calendars;
    private final Trigger[] triggers;
    private final ApplicationContext applicationContext;

    public MicroJobQuartzAutoConfiguration(QuartzProperties properties, MicroJobScheduleProperties microJobScheduleProperties, ObjectProvider<SchedulerFactoryBeanCustomizer> customizers, ObjectProvider<JobDetail[]> jobDetails, ObjectProvider<Map<String, Calendar>> calendars, ObjectProvider<Trigger[]> triggers, ApplicationContext applicationContext) {
        this.properties = properties;
        this.microJobScheduleProperties = microJobScheduleProperties;
        this.customizers = customizers;
        this.jobDetails = (JobDetail[]) jobDetails.getIfAvailable();
        this.calendars = (Map) calendars.getIfAvailable();
        this.triggers = (Trigger[]) triggers.getIfAvailable();
        this.applicationContext = applicationContext;
        // 初始化job属性配置信息
        initJobProperties(this.properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerFactoryBean quartzScheduler() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(this.applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        if (this.properties.getSchedulerName() != null) {
            schedulerFactoryBean.setSchedulerName(this.properties.getSchedulerName());
        }

        schedulerFactoryBean.setAutoStartup(this.properties.isAutoStartup());
        schedulerFactoryBean.setStartupDelay((int) this.properties.getStartupDelay().getSeconds());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(this.properties.isWaitForJobsToCompleteOnShutdown());
        schedulerFactoryBean.setOverwriteExistingJobs(this.properties.isOverwriteExistingJobs());
        if (!this.properties.getProperties().isEmpty()) {
            schedulerFactoryBean.setQuartzProperties(this.asProperties(this.properties.getProperties()));
        }

        if (this.jobDetails != null && this.jobDetails.length > 0) {
            schedulerFactoryBean.setJobDetails(this.jobDetails);
        }

        if (this.calendars != null && !this.calendars.isEmpty()) {
            schedulerFactoryBean.setCalendars(this.calendars);
        }

        if (this.triggers != null && this.triggers.length > 0) {
            schedulerFactoryBean.setTriggers(this.triggers);
        }

        this.customize(schedulerFactoryBean);
        return schedulerFactoryBean;
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    private void customize(SchedulerFactoryBean schedulerFactoryBean) {
        this.customizers.orderedStream().forEach((customizer) -> {
            customizer.customize(schedulerFactoryBean);
        });
    }

    @Configuration
    @ConditionalOnSingleCandidate(DataSource.class)
    protected static class JdbcStoreTypeConfiguration {
        protected JdbcStoreTypeConfiguration() {
        }

        @Bean
        @Order(0)
        public SchedulerFactoryBeanCustomizer jobDataSourceCustomizer(QuartzProperties properties, DataSource dataSource, @QuartzDataSource ObjectProvider<DataSource> quartzDataSource, ObjectProvider<PlatformTransactionManager> transactionManager) {
            return (schedulerFactoryBean) -> {
                if (properties.getJobStoreType() == JobStoreType.JDBC) {
                    DataSource dataSourceToUse = this.getDataSource(dataSource, quartzDataSource);
                    schedulerFactoryBean.setDataSource(dataSourceToUse);
                    PlatformTransactionManager txManager = (PlatformTransactionManager) transactionManager.getIfUnique();
                    if (txManager != null) {
                        schedulerFactoryBean.setTransactionManager(txManager);
                    }
                }

            };
        }

        private DataSource getDataSource(DataSource dataSource, ObjectProvider<DataSource> quartzDataSource) {
            DataSource dataSourceIfAvailable = (DataSource) quartzDataSource.getIfAvailable();
            return dataSourceIfAvailable != null ? dataSourceIfAvailable : dataSource;
        }

        @Bean
        @ConditionalOnMissingBean
        public QuartzDataSourceInitializer quartzDataSourceInitializer(DataSource dataSource, @QuartzDataSource ObjectProvider<DataSource> quartzDataSource, ResourceLoader resourceLoader, QuartzProperties properties) {
            DataSource dataSourceToUse = this.getDataSource(dataSource, quartzDataSource);
            return new QuartzDataSourceInitializer(dataSourceToUse, resourceLoader, properties);
        }

        @Bean
        public static MicroJobQuartzAutoConfiguration.JdbcStoreTypeConfiguration.DataSourceInitializerSchedulerDependencyPostProcessor jobDataSourceInitializerSchedulerDependencyPostProcessor() {
            return new MicroJobQuartzAutoConfiguration.JdbcStoreTypeConfiguration.DataSourceInitializerSchedulerDependencyPostProcessor();
        }

        private static class DataSourceInitializerSchedulerDependencyPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
            DataSourceInitializerSchedulerDependencyPostProcessor() {
                super(Scheduler.class, SchedulerFactoryBean.class, new String[]{"quartzDataSourceInitializer"});
            }
        }
    }

    /**
     * 初始化任务属性配置
     *
     * @param quartzProperties quartz属性配置
     */
    private void initJobProperties(QuartzProperties quartzProperties) {
        // 设置任务存储方式为数据库方式
        quartzProperties.setJobStoreType(microJobScheduleProperties.getQuartz().getJobStoreType());

        // 设置schema初始化模式
        quartzProperties.getJdbc().setInitializeSchema(microJobScheduleProperties.getQuartz().getJdbc().getInitializeSchema());

        // 设置属性配置
        quartzProperties.getProperties().putAll(microJobScheduleProperties.getQuartz().getProperties());
    }
}
