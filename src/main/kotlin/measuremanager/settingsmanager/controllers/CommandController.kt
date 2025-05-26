package measuremanager.settingsmanager.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.mqtt.MqttService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/command")
class CommandController( private val mq: MqttService) {

    @GetMapping("/start/{muid}","/start/{muid}/")
    fun start(@PathVariable muid : Long){
        val c = CommandDTO(
            commandId = 1 ,
            gateway = 1,
            cu = 1  ,
            mu = 1,
            type = "start",
            cuSettingDTO = null,
            muSettingDTO = null,
        )
        val mapper = jacksonObjectMapper()

        val jsonString = mapper.writeValueAsString(c)
        mq.sendCommandToNode(jsonString)

    }

    @GetMapping("/stop/{muid}","/stop/{muid}")
    fun stop(@PathVariable muid : Long){
        val c = CommandDTO(
            commandId = 1 ,
            gateway = 1,
            cu = 1  ,
            mu = 1,
            type = "stop",
            cuSettingDTO = null,
            muSettingDTO = null,
        )
        val mapper = jacksonObjectMapper()

        val jsonString = mapper.writeValueAsString(c)
        mq.sendCommandToNode(jsonString)

    }
}