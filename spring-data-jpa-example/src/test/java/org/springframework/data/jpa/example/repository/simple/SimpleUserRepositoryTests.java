package org.springframework.data.jpa.example.repository.simple;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.example.domain.User;
import org.springframework.data.jpa.example.repository.InfrastructureConfig;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Intergration test showing the basic usage of {@link SimpleUserRepository}.
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class SimpleUserRepositoryTests {

	@Configuration
	@Import(InfrastructureConfig.class)
	@EnableJpaRepositories
	static class Config {}

	@Autowired SimpleUserRepository repository;
	User user;

	@Before
	public void setUp() {
		user = new User();
		user.setUsername("foobar");
		user.setFirstname("firstname");
		user.setLastname("lastname");
	}

	@Test
	public void findSavedUserById() {

		user = repository.save(user);

		assertEquals(user, repository.findOne(user.getId()));
	}

	@Test
	public void findSavedUserByLastname() throws Exception {

		user = repository.save(user);

		List<User> users = repository.findByLastname("lastname");

		assertNotNull(users);
		assertTrue(users.contains(user));
	}

	@Test
	public void findByFirstnameOrLastname() throws Exception {

		user = repository.save(user);

		List<User> users = repository.findByFirstnameOrLastname("lastname");

		assertTrue(users.contains(user));
	}
}
