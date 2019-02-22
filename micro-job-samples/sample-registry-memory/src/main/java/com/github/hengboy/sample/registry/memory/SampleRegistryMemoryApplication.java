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

package com.github.hengboy.sample.registry.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * memory registry sample application main class
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-02-22 13:37
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@SpringBootApplication
public class SampleRegistryMemoryApplication {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(SampleRegistryMemoryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SampleRegistryMemoryApplication.class);
        logger.info("「「「「「Micro Job Memory Registry Started」」」」」");
    }
}
