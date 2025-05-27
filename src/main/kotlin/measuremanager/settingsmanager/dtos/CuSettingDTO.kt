package measuremanager.settingsmanager.dtos

import measuremanager.settingsmanager.entities.CuSetting

data class CuSettingDTO(val networkId:Long, val gateway:Long?,val bandwith:Long, val codingRate : Long, val spreadingFactor : Long, val updateInterval : Long, val mus : Set<Long>)

fun CuSetting.toDTO() : CuSettingDTO = CuSettingDTO(networkId, gw?.id,bandwith,codingRate,spreadingFactor, updateInterval, mus.map { it.networkId }.toSet())