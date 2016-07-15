package itaka.api.mock.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by isao on 2016/07/12.
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "apimock")
public class ApiMockProperties {
    private String spyTargetUrl;
    private String dataFilePath;
}
