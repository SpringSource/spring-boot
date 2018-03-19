/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure.metrics.orm.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.sql.DataSource;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.hibernate.SessionFactory;
import org.junit.Test;

import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HibernateMetricsAutoConfiguration}.
 *
 * @author Rui Figueira
 */
public class HibernateMetricsAutoConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.with(MetricsRun.simple())
			.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class,
					HibernateMetricsAutoConfiguration.class))
			.withUserConfiguration(BaseConfiguration.class).withPropertyValues(
					"spring.jpa.properties.hibernate.generate_statistics:true");

	@Test
	public void autoConfiguredEntityManagerFactoryWithStatsIsInstrumented() {
		this.contextRunner.run((context) -> {
			context.getBean(EntityManagerFactory.class).unwrap(SessionFactory.class);
			MeterRegistry registry = context.getBean(MeterRegistry.class);
			registry.get("hibernate.statements")
					.tags("entityManagerFactory", "entityManagerFactory").meter();
		});
	}

	@Test
	public void autoConfiguredEntityManagerFactoryWithoutStatsIsNotInstrumented() {
		this.contextRunner
				.withPropertyValues(
						"spring.jpa.properties.hibernate.generate_statistics:false")
				.run((context) -> {
					context.getBean(EntityManagerFactory.class)
							.unwrap(SessionFactory.class);
					MeterRegistry registry = context.getBean(MeterRegistry.class);
					assertThat(registry.find("hibernate.statements").meter()).isNull();
				});
	}

	@Test
	public void entityManagerFactoryInstrumentationCanBeDisabled() {
		this.contextRunner.withPropertyValues("management.metrics.enable.hibernate=false")
				.run((context) -> {
					context.getBean(DataSource.class).getConnection().getMetaData();
					MeterRegistry registry = context.getBean(MeterRegistry.class);
					assertThat(registry.find("hibernate.statements").meter()).isNull();
				});
	}

	@Test
	public void allEntityManagerFactoriesCanBeInstrumented() {
		this.contextRunner
				.withUserConfiguration(TwoEntityManagerFactoriesConfiguration.class)
				.run((context) -> {
					context.getBean("firstEntityManagerFactory",
							EntityManagerFactory.class).getMetamodel();
					context.getBean("secondOne", EntityManagerFactory.class)
							.getMetamodel();
					MeterRegistry registry = context.getBean(MeterRegistry.class);
					registry.get("hibernate.statements")
							.tags("entityManagerFactory", "first").meter();
					registry.get("hibernate.statements")
							.tags("entityManagerFactory", "secondOne").meter();
				});
	}

	@Configuration
	static class BaseConfiguration {

		@Bean
		public SimpleMeterRegistry simpleMeterRegistry() {
			return new SimpleMeterRegistry();
		}

	}

	@Entity
	static class MyEntity {

		@Id
		@GeneratedValue
		private Long id;
	}

	@Configuration
	static class TwoEntityManagerFactoriesConfiguration {

		private static final Class<?>[] PACKAGE_CLASSES = new Class<?>[] {
				MyEntity.class };

		@Primary
		@Bean
		public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(
				DataSource ds) {
			return createSessionFactory(ds);
		}

		@Bean
		public LocalContainerEntityManagerFactoryBean secondOne(DataSource ds) {
			return createSessionFactory(ds);
		}

		private LocalContainerEntityManagerFactoryBean createSessionFactory(
				DataSource ds) {
			Map<String, String> jpaProperties = new HashMap<>();
			jpaProperties.put("hibernate.generate_statistics", "true");
			EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
					new HibernateJpaVendorAdapter(), jpaProperties, null);
			return builder.dataSource(ds).packages(PACKAGE_CLASSES).build();
		}
	}

}
