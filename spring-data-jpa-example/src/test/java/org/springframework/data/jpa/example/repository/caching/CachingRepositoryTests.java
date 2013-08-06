/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.example.repository.caching;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.example.domain.User;
import org.springframework.data.jpa.example.repository.InfrastructureConfig;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test to show how to use {@link Cacheable} with a Spring Data repository.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class CachingRepositoryTests {

	/**
	 * Java config to use Spring Data JPA alongside the Spring caching support.
	 * 
	 * @author Oliver Gierke
	 * @author Thomas Darimont
	 */
	@Configuration
	@Import(InfrastructureConfig.class)
	@EnableJpaRepositories
	@EnableCaching
	static class Config {

		@Bean
		public CacheManager cacheManager() {

			Cache cache = new ConcurrentMapCache("byUsername");

			SimpleCacheManager manager = new SimpleCacheManager();
			manager.setCaches(Arrays.asList(cache));

			return manager;
		}
	}

	@Autowired CachingUserRepository repository;
	@Autowired CacheManager cacheManager;

	@Test
	public void cachesValuesReturnedForQueryMethod() {

		User dave = new User();
		dave.setUsername("dmatthews");

		dave = repository.save(dave);

		User result = repository.findByUsername("dmatthews");
		assertThat(result, is(dave));

		// Verify entity cached
		Cache cache = cacheManager.getCache("byUsername");
		ValueWrapper wrapper = cache.get("dmatthews");
		assertThat(wrapper.get(), is((Object) dave));
	}
}
