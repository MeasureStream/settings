package measuremanager.settingsmanager.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import measuremanager.settingsmanager.dtos.CuCreateDTO
import measuremanager.settingsmanager.services.CuSettingService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaCuConsumer(private val cs: CuSettingService, private val objectMapper: ObjectMapper)  {

    @KafkaListener(topics = ["cu-creation"], groupId = "measurestream")
    fun consume(message:String){

        try {
            val cucreate = objectMapper.readValue(message, CuCreateDTO::class.java)
            val data = cs.create(cucreate)
            println("Saved data: $data")
        } catch (e: Exception) {
            println("Error parsing message: $message")
            e.printStackTrace()
        }

    }
}