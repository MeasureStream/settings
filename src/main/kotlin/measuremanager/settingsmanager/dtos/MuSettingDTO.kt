package measuremanager.settingsmanager.dtos

import measuremanager.settingsmanager.entities.MuSetting

data class MuSettingDTO (val networkId:Long,val gateway: Long?, val cu : Long? ,val samplingFrequency : Long)

fun MuSetting.toDTO() : MuSettingDTO = MuSettingDTO(networkId, cu?.gw?.id, cu?.networkId,samplingFrequency)