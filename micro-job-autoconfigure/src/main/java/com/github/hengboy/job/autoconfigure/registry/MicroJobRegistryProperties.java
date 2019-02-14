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

import com.github.hengboy.job.core.enums.MicroJobRegistryAway;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.github.hengboy.job.autoconfigure.registry.MicroJobRegistryProperties.REGISTRY_PROPERTIES_PREFIX;

/**
 * 服务注册中心配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-29 15:03
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Data
@ConfigurationProperties(prefix = REGISTRY_PROPERTIES_PREFIX)
public class MicroJobRegistryProperties {
    /**
     * 注册中心配置前缀
     */
    public final static String REGISTRY_PROPERTIES_PREFIX = "hengboy.job.registry";
    /**
     * 任务注册中心部署的ip地址
     */
    private String ipAddress = "127.0.0.1";
    /**
     * 注册中心监听端口号
     * 默认端口号为：9000
     */
    private int port = 9000;
    /**
     * 心跳同步执行间隔时间，单位：秒
     */
    private int heartDelaySeconds = 5;
    /**
     * 注册中心注册方式
     * 默认内存方式
     */
    private MicroJobRegistryAway away = MicroJobRegistryAway.MEMORY;
    /**
     * zookeeper 相关配置
     */
    private ZookeeperProperties zookeeper = new ZookeeperProperties();

    /**
     * zookeeper属性配置
     */
    @Getter
    @Setter
    class ZookeeperProperties {
        /**
         * zookeeper地址
         */
        private String address = "127.0.0.1:2181";
        /**
         * 会话超时时间
         */
        private int sessionTimeOut = 100000;
        /**
         * 连接超时时间
         */
        private int connectionTimeOut = 100000;
    }
}
