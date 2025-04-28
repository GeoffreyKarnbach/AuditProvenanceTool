package project.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityPropertiesConfig {

    @Bean
    @ConfigurationProperties(prefix = "security.auth")
    protected Auth auth() {
        return new Auth();
    }

    @Getter
    @Setter
    public static class Auth {
        private String header;
        private String prefix;
        private String loginUri;
    }
}
