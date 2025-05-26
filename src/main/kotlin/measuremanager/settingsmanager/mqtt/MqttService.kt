package measuremanager.settingsmanager.mqtt

import measuremanager.settingsmanager.configurations.MqttProperties
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service

@Service
class MqttService(
    private val props: MqttProperties,
    @Qualifier("conversionService") private val conversionService: ConversionService
) {


    private val client = MqttClient(props.broker, props.clientId)

    init {
        val options = MqttConnectOptions().apply {
            isCleanSession = true
            userName = props.username
            password = this@MqttService.props.password.toCharArray()
        }

        client.connect(options)

        // Sottoscrizione esempio
        client.subscribe("uplink/#") { topic, message ->
            println("Ricevuto [$topic]: ${message.payload.decodeToString()}")
        }
    }

    fun sendCommandToNode( command: String) {
        val topic = "downlink/"
        val message = MqttMessage(command.toByteArray()).apply {
            // qos = 0 fire and forget
            // qos = 1 at least once
            // qos = 2 exactly once
            qos = 1
            isRetained = true
        }
        client.publish(topic, message)
        println("Inviato comando a $topic: $command")
    }

}