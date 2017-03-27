package com.pokebattler.fight.jaxrs;

import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BlogProxyConfiguration implements EnvironmentAware {
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CachingProxyServlet(),
				propertyResolver.getProperty("servlet_url"));
		servletRegistrationBean.addInitParameter("targetUri", propertyResolver.getProperty("target_url"));
		servletRegistrationBean.addInitParameter(CachingProxyServlet.P_CACHE_CONTROL, propertyResolver.getProperty("cache_control"));
		servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG,
				propertyResolver.getProperty("logging_enabled", "false"));
		return servletRegistrationBean;
	}

	private RelaxedPropertyResolver propertyResolver;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, "proxy.blog.");
	}
}
