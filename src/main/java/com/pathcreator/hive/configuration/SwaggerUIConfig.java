package com.pathcreator.hive.configuration;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.utils.Constants;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerUIConfig {

    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
            SwaggerUiConfigProperties swaggerUiConfig,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerUiConfigParameters swaggerUiConfigParameters,
            ObjectMapperProvider objectMapperProvider,
            SwaggerWelcomeCommon swaggerWelcomeCommon) {
        return new SwaggerIndexPageTransformer(swaggerUiConfig, swaggerUiOAuthProperties, swaggerUiConfigParameters, swaggerWelcomeCommon, objectMapperProvider) {
            @Override
            protected String overwriteSwaggerDefaultUrl(String html) {
                return html.replace(Constants.SWAGGER_UI_DEFAULT_URL, swaggerUiConfigParameters.getUrl())
                        .replaceAll("\"configUrl.*swagger-config\",", "");
            }
        };
    }
}