package com.spoony.spoony_server.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.beans.factory.config.BeanDefinition;

@TestConfiguration
public class AutoMockPortsConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final String BASE_PACKAGE = "com.spoony.spoony_server.application.port.out";

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		var beanFactory = applicationContext.getBeanFactory();
		var scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));

		for (BeanDefinition bd : scanner.findCandidateComponents(BASE_PACKAGE)) {
			try {
				Class<?> clazz = Class.forName(bd.getBeanClassName());
				if (clazz.isInterface() && !beanFactory.containsBeanDefinition(clazz.getName())) {
					Object mock = Mockito.mock(clazz);
					beanFactory.registerSingleton(clazz.getName(), mock);
					System.out.println("✅ AutoMock registered: " + clazz.getSimpleName());
				}
			} catch (Exception e) {
				System.err.println("⚠️ AutoMock failed for: " + bd.getBeanClassName() + " (" + e.getMessage() + ")");
			}
		}
	}
}
