package measuremanager.settingsmanager.dtos

import measuremanager.settingsmanager.entities.MuSetting

data class MuSettingDTO (val networkId:Long, val samplingFrequency : Long)

fun MuSetting.toDTO() : MuSettingDTO = MuSettingDTO(networkId,samplingFrequency)