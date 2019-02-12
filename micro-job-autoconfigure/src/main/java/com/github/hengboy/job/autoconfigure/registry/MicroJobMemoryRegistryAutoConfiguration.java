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

package com.github.hengboy.job.autoconfigure.registry;

import com.github.hengboy.job.registry.http.InstanceRegistry;
import com.github.hengboy.job.registry.store.RegistryFactoryBean;
import com.github.hengboy.job.registry.support.memory.MemoryInstanceRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.hengboy.job.autoconfigure.registry.MicroJobRegistryProperties.REGISTRY_PROPERTIES_PREFIX;

/**
 * 内存方式注册中心自动化配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-30 09:55
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Configuration
@ConditionalOnClass({MemoryInstanceRegistry.class, RegistryFactoryBean.class})
@EnableConfigurationProperties(MicroJobRegistryProperties.class)
@ConditionalOnProperty(prefix = REGISTRY_PROPERTIES_PREFIX, name = "away", havingValue = "MEMORY")
@AutoConfigureAfter(MicroJobRegistryAutoConfiguration.class)
public class MicroJobMemoryRegistryAutoConfiguration {
    /**
     * 注册中心属性配置
     */
    private MicroJobRegistryProperties microJobRegistryProperties;

    public MicroJobMemoryRegistryAutoConfiguration(MicroJobRegistryProperties microJobRegistryProperties) {
        this.microJobRegistryProperties = microJobRegistryProperties;
    }

    /**
     * 任务注册中心工程实体类
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public RegistryFactoryBean RegistryFactoryBean() {
        RegistryFactoryBean factoryBean = new RegistryFactoryBean();
        return factoryBean;
    }

    /**
     * 实例注册中心
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public InstanceRegistry instanceRegistry() {
        return new MemoryInstanceRegistry();
    }
}
