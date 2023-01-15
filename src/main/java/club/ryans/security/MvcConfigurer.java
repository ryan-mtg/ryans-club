package club.ryans.security;

import club.ryans.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {
    private static final String RESOURCE_DIRECTORY = "src/main/resources";

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
}
