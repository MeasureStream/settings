package measuremanager.settingsmanager.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "mqtt")
class MqttProperties {
    lateinit var broker: String
    lateinit var username: String
    lateinit var password: String
    lateinit var clientId: String
}