package org.naho.config.static_resource;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.StaticResourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {
    private final StaticResourceProperties staticResourceProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("files/**")
                .addResourceLocations(staticResourceProperties.getLocalRoot());
    }
}
