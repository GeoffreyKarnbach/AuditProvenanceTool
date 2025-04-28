package project.backend.config.properties;

import project.backend.config.SecurityPropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This configuration class offers all necessary security properties in an immutable manner.
 */
@Component
@RequiredArgsConstructor
public class SecurityProperties {

    private final SecurityPropertiesConfig.Auth auth;

    public String getAuthHeader() {
        return auth.getHeader();
    }

    public String getAuthTokenPrefix() {
        return auth.getPrefix();
    }

    public String getLoginUri() {
        return auth.getLoginUri();
    }

}
