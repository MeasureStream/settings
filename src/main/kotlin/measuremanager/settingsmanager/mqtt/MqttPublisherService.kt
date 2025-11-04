package measuremanager.settingsmanager.mqtt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import measuremanager.settingsmanager.configurations.MqttProperties
import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.dtos.CuSettingDTO
import measuremanager.settingsmanager.dtos.MuSettingDTO
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service
import javax.net.ssl.SSLSocketFactory

@Service
class MqttPublisherService(private val props: MqttProperties) : MqttServiceInterface {
    // private val client = MqttClient(props.broker, "spring-backend-publisher")
    private val client = MqttClient(props.broker, props.clientId)

    init {
        // Attiva la riconnessione automatica
        /*
        val options = MqttConnectOptions()
        .apply {
            isCleanSession = false
            userName = props.username
            password = this@MqttPublisherService.props.password.toCharArray()
        }
        */
        val options =
            MqttConnectOptions().apply {
                isCleanSession = true
                userName = props.username
                password = this@MqttPublisherService.props.password.toCharArray()
                socketFactory = SSLSocketFactory.getDefault()
            }

        client.connect(options)


        client.setManualAcks(false) // opzionale
        client.setTimeToWait(1000)
        // connectIfNecessary()
    }

    private fun connectIfNecessary() {
        val options = MqttConnectOptions().apply {
            isCleanSession = true
            userName = props.username
            password = this@MqttPublisherService.props.password.toCharArray()
        }

        if (!client.isConnected) {
            try {
                println("Connessione MQTT a ${props.broker}...")
                client.connect(options)
                println("MQTT client connesso.")
            } catch (e: Exception) {
                println("Errore nella connessione MQTT: ${e.message}")
            }
        }
    }


    override fun sendCommandToGW(c : CommandDTO, type: String) {
        connectIfNecessary()
        if (!client.isConnected) {
            println("Impossibile inviare comando: client MQTT non connesso.")
            return
        }

        val topic = "downlink/gateway"
        val mapper = jacksonObjectMapper()


        val command = mapper.writeValueAsString(c)
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

    override fun sendCommandToCu(cu : CuSettingDTO, type : String) {
        connectIfNecessary()
        if (!client.isConnected) {
            println("Impossibile inviare comando: client MQTT non connesso.")
            return
        }

        val topic = "downlink/cu"
        val mapper = jacksonObjectMapper()
        if(cu.gateway == null) throw Exception("No Route to cu : ${cu.networkId}")

        val c = CommandDTO(
            commandId = 1,
            gateway = cu.gateway,
            cu = cu.networkId,
            mu = -1,
            type = type,
            cuSettingDTO = cu,
            muSettingDTO = null
        )

        val command = mapper.writeValueAsString(c)
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

    override fun sendCommandToMu(mu: MuSettingDTO, type:String) {
        connectIfNecessary()
        if (!client.isConnected) {
            println("Impossibile inviare comando: client MQTT non connesso.")
            return
        }

        val topic = "downlink/mu"
        val mapper = jacksonObjectMapper()
        if(mu.gateway == null || mu.cu == null) throw Exception("No Route to mu : ${mu.networkId}")

        val c = CommandDTO(
            commandId = 1,
            gateway = mu.gateway,
            cu = mu.cu,
            mu = mu.networkId,
            type = type,
            cuSettingDTO = null,
            muSettingDTO = mu
        )

        val command = mapper.writeValueAsString(c)
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
