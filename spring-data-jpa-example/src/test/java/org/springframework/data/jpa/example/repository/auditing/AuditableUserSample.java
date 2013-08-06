package org.springframework.data.jpa.example.repository.auditing;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.jpa.domain.support.AuditingBeanFactoryPostProcessor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.example.auditing.AuditableUser;
import org.springframework.data.jpa.example.auditing.AuditorAwareImpl;
import org.springframework.data.jpa.example.repository.InfrastructureConfig;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class AuditableUserSample {

	@Configuration
	@Import(InfrastructureConfig.class)
	@EnableJpaRepositories
	static class Config {

		@Bean
		@Scope("prototype")
		public AuditingEntityListener<AuditableUser> auditingEntityListener() {
			AuditingEntityListener<AuditableUser> auditingEntityListener = new AuditingEntityListener<AuditableUser>();
			AuditingHandler<AuditableUser> auditingHandler = new AuditingHandler<AuditableUser>();
			auditingHandler.setAuditorAware(new AuditorAwareImpl());
			auditingEntityListener.setAuditingHandler(auditingHandler);
			return auditingEntityListener;
		}

		@Bean
		public AuditingBeanFactoryPostProcessor auditing() {
			return new AuditingBeanFactoryPostProcessor();
		}
	}

	@Autowired private CrudRepository<AuditableUser, Long> repository;

	@Autowired private AuditorAwareImpl auditorAware;

	@Autowired private AuditingEntityListener<?> listener;

	// TODO revise bean configuration
	@Ignore
	@Test
	public void auditEntityCreation() throws Exception {

		assertThat(ReflectionTestUtils.getField(listener, "handler"), is(notNullValue()));

		AuditableUser user = new AuditableUser();
		user.setUsername("username");

		auditorAware.setAuditor(user);

		user = repository.save(user);
		user = repository.save(user);

		assertEquals(user, user.getCreatedBy());
		assertEquals(user, user.getLastModifiedBy());
	}
}
