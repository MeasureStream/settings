package measuremanager.settingsmanager.mqtt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import measuremanager.settingsmanager.configurations.MqttProperties
import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.dtos.CuSettingDTO
import measuremanager.settingsmanager.dtos.MuSettingDTO
import measuremanager.settingsmanager.services.CuSettingService
import measuremanager.settingsmanager.services.MuSettingService
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service

@Service
class MqttService(
    private val props: MqttProperties,
    private val ms : MuSettingService,
    private val cs: CuSettingService
   // @Qualifier("conversionService") private val conversionService: ConversionService
) {


    private val client = MqttClient(props.broker, props.clientId)

    init {
        val options = MqttConnectOptions().apply {
            isCleanSession = true
            userName = props.username
            password = this@MqttService.props.password.toCharArray()
        }

        client.connect(options)

        // Sottoscrizioni
        client.subscribe("uplink/gateway") { topic, message ->

            val json = message.payload.decodeToString()
            println("Ricevuto [$topic]: $json")

            try {
                val mapper = jacksonObjectMapper()
                val cmd: CommandDTO = mapper.readValue(json)
                println("Oggetto deserializzato: $cmd")

                //gw announce
                //creare gw e creare i relativi cu e collegarli oppure se sono già presenti collegarli al gw
            } catch (e: Exception) {
                println("Errore nella deserializzazione del messaggio: ${e.message}")
            }
        }
        client.subscribe("uplink/cu") { topic, message ->
            val json = message.payload.decodeToString()
            println("Ricevuto [$topic]: $json")

            try {
                val mapper = jacksonObjectMapper()
                val cmd: CommandDTO = mapper.readValue(json)
                println("Oggetto deserializzato: $cmd")

                //cu announce popolare cu o crearla e creare mu o collegarlo
            } catch (e: Exception) {
                println("Errore nella deserializzazione del messaggio: ${e.message}")
            }
        }
        client.subscribe("uplink/mu") { topic, message ->
            val json = message.payload.decodeToString()
            println("Ricevuto [$topic]: $json")

            try {
                val mapper = jacksonObjectMapper()
                val cmd: CommandDTO = mapper.readValue(json)
                println("Oggetto deserializzato: $cmd")

                //mu announce creare mu o popolare
            } catch (e: Exception) {
                println("Errore nella deserializzazione del messaggio: ${e.message}")
            }
        }
    }

    fun sendCommandToGW( c : CommandDTO , type: String) {
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

    fun sendCommandToCu(cu : CuSettingDTO, type : String) {
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

    fun sendCommandToMu(mu:MuSettingDTO, type:String) {
        val topic = "downlink/mu"
        val mapper = jacksonObjectMapper()
        if(mu.gateway == null || mu.cu == null) throw Exception("No Route to cu : ${mu.networkId}")

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