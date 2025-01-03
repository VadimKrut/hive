package com.pathcreator.hive.configuration;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Set;

@OpenAPIDefinition(
        info = @Info(
                title = "REST api, base service for hive",
                version = "1.0",
                description = "REST api, base service for hive"
        )
)
@Component
@ApplicationPath("/v1")
public class JerseyConfig extends ResourceConfig {

    @Value(value = "${swagger.url.hive}")
    private String base;

    @Value(value = "${swagger.url.outside}")
    private String outsideBase;

    public JerseyConfig() throws ClassNotFoundException {
        this.registerEndpoints();
    }

    private void registerEndpoints() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.pathcreator.hive");
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            this.register(clazz);
        }
    }

    @PostConstruct
    private void init() {
        SwaggerConfiguration config = new SwaggerConfiguration();
        OpenAPI openAPI = new OpenAPI();
        openAPI.addServersItem(new Server().url(base).description("Base URL for all requests"));
        openAPI.addServersItem(new Server().url(outsideBase).description("outside base URL for all requests"));
        config.openAPI(openAPI);
        register(new OpenApiResource().openApiConfiguration(config));
    }
}