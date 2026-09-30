package com.deveyk.jobmatch;

import com.deveyk.jobmatch.testsupport.TestContainerConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JobmatchApplication - Baglam Yukleme Testi")
class JobmatchApplicationIT extends TestContainerConfiguration {

	@Test
	@DisplayName("Spring ApplicationContext Testcontainers (Postgres + Keycloak) ile basariyla yuklenir")
	void contextLoads() {
	}

}
