package measuremanager.settingsmanager.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.mqtt.MqttPublisherService
import measuremanager.settingsmanager.mqtt.MqttService
import measuremanager.settingsmanager.services.MuSettingService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/command")
class CommandController(
    private val mq: MqttPublisherService,
    private  val ms: MuSettingService
) {

    @GetMapping("/start/{muid}","/start/{muid}/")
    fun start(@PathVariable muid : Long):Long{

        return ms.start(muid)


    }

    @GetMapping("/stop/{muid}","/stop/{muid}/")
    fun stop(@PathVariable muid : Long): Long{

        return ms.stop(muid)
    }
}