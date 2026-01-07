package com.hyungsuu.common.config;

import java.util.Arrays;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.hyungsuu.common.filter.InitMDCFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FilterConfig implements WebMvcConfigurer {

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean loggingFilter() {

		log.debug("________________________");
		FilterRegistrationBean firstFilter = new FilterRegistrationBean();
		firstFilter.setFilter(new InitMDCFilter());
		firstFilter.setOrder(1);
		firstFilter.setUrlPatterns(Arrays.asList("/api1/*", "/api/*"));
		firstFilter.setName("InitMDCFilter");
		return firstFilter;
	}

}
