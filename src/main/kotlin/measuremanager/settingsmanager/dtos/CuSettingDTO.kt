package measuremanager.settingsmanager.dtos

import measuremanager.settingsmanager.entities.CuSetting

data class CuSettingDTO(val networkId:Long, val bandwith:Long, val codingRate : Long, val spreadingFactor : Long, val updateInterval : Long)

fun CuSetting.toDTO() : CuSettingDTO = CuSettingDTO(networkId,bandwith,codingRate,spreadingFactor, updateInterval)