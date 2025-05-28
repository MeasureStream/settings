package measuremanager.settingsmanager.mqtt

import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.dtos.CuSettingDTO
import measuremanager.settingsmanager.dtos.MuSettingDTO

interface MqttServiceInterface {
    fun sendCommandToGW(c : CommandDTO, type: String)
    fun sendCommandToCu(cu : CuSettingDTO, type : String)
    fun sendCommandToMu(mu: MuSettingDTO, type:String)
}