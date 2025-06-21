package measuremanager.settingsmanager.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import measuremanager.settingsmanager.dtos.EventMU
import measuremanager.settingsmanager.services.MuSettingService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaMuConsumer(private val ms: MuSettingService, private val objectMapper: ObjectMapper) {

    @KafkaListener(topics = ["mus"], groupId = "measurestream")
    fun consume(message: String) {

        try {
            val event = objectMapper.readValue(message, EventMU::class.java)
            when (event.eventType){
                "CREATE" -> {
                    val data = ms.create(event.mu)
                    println("Saved mu: $data")
                }
                "DELETE" -> {
                    ms.delete(event.mu.networkId, true)
                    println("deleted mu: ${event.mu}")
                }
                else -> {throw Exception("Unrecognized event : $event")}
            }

        } catch (e: Exception) {
            println("Error parsing message: $message")
            e.printStackTrace()
        }

    }
}