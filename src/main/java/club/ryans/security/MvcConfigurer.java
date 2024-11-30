package club.ryans.security;

import club.ryans.Application;
import club.ryans.interceptors.CommunityModInterceptor;
import club.ryans.interceptors.RequestLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableScheduling
@EnableJdbcHttpSession(tableName = "session", cleanupCron = "0 0 * * * *",
    maxInactiveIntervalInSeconds = MvcConfigurer.SESSION_TIMEOUT)
public class MvcConfigurer implements WebMvcConfigurer {
    private static final String RESOURCE_DIRECTORY = "src/main/resources";
    public static final int SESSION_TIMEOUT = 3 * 24 * 60 * 60;

    @Autowired
    private CommunityModInterceptor communityModInterceptor;

    @Bean
    public SpringResourceTemplateResolver springTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setOrder(1);

        // While testing, load from source
        if (Application.isTesting()) {
            templateResolver.setPrefix(String.format("file:%s/templates/", RESOURCE_DIRECTORY));
            templateResolver.setCacheable(false);
        } else {
            templateResolver.setPrefix("classpath:templates/");
        }

        return templateResolver;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry resourceHandlerRegistry) {
        ResourceHandlerRegistration registration = resourceHandlerRegistry.addResourceHandler("/**");
        if (Application.isTesting()) {
            registration.addResourceLocations(String.format("file:%s/static/", RESOURCE_DIRECTORY));
            registration.setCachePeriod(0);
        } else {
            registration.addResourceLocations("classpath:/static/");
        }

        resourceHandlerRegistry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/images/");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggingInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(communityModInterceptor).addPathPatterns("/api/public/**");
    }
}
