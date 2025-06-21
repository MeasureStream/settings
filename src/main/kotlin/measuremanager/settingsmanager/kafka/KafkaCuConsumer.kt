package measuremanager.settingsmanager.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import measuremanager.settingsmanager.dtos.EventCU
import measuremanager.settingsmanager.services.CuSettingService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaCuConsumer(private val cs: CuSettingService, private val objectMapper: ObjectMapper)  {

    @KafkaListener(topics = ["cu-creation"], groupId = "measurestream")
    fun consume(message:String){

        try {
            //val cucreate = objectMapper.readValue(message, CuCreateDTO::class.java)
            //val data = cs.create(cucreate)
            val event = objectMapper.readValue(message, EventCU::class.java)
            when (event.eventType){
                "CREATE" -> {
                    val data = cs.create(event.cu)
                    println("Saved data: $data")
                }
                "DELETE" -> {
                    cs.delete(event.cu.networkId, true)
                    println("deleted cu: $event.cu")
                }
                else -> {throw Exception("Unrecognized event : $event")}
            }


        } catch (e: Exception) {
            println("Error parsing message: $message")
            e.printStackTrace()
        }

    }
}