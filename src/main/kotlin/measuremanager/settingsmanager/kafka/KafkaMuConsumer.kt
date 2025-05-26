package measuremanager.settingsmanager.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import measuremanager.settingsmanager.dtos.MuCreateDTO
import measuremanager.settingsmanager.services.MuSettingService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaMuConsumer(private val ms: MuSettingService, private val objectMapper: ObjectMapper) {

    @KafkaListener(topics = ["mu-creation"], groupId = "measurestream")
    fun consume(message: String) {

        try {
            val mucreate = objectMapper.readValue(message, MuCreateDTO::class.java)
            val data = ms.create(mucreate)

            println("Saved data: $data")
        } catch (e: Exception) {
            println("Error parsing message: $message")
            e.printStackTrace()
        }

    }
}