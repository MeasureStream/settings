package measuremanager.settingsmanager.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.mqtt.MqttService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/API/command")
class CommandController( private val mq: MqttService) {

    @PostMapping("/{muid}","/{muid}")
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
}