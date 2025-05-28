package measuremanager.settingsmanager.mqtt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import measuremanager.settingsmanager.configurations.MqttProperties
import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.dtos.CuSettingDTO
import measuremanager.settingsmanager.dtos.GatewayDTO
import measuremanager.settingsmanager.dtos.MuSettingDTO
import measuremanager.settingsmanager.services.CuSettingService
import measuremanager.settingsmanager.services.GatewayService
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
    private val cs: CuSettingService,
    private val gs: GatewayService
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
                val gateway: GatewayDTO = mapper.readValue(json)
                println("Oggetto deserializzato: $gateway")

                //gw announce
                //creare gw e creare i relativi cu e collegarli oppure se sono già presenti collegarli al gw
                gs.update(gateway)
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
                cmd.cuSettingDTO?.let { cs.update(it) }
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
                cmd.muSettingDTO?.let { ms.update(it) }
            } catch (e: Exception) {
                println("Errore nella deserializzazione del messaggio: ${e.message}")
            }
        }
    }



}