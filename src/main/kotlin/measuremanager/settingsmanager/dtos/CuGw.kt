package measuremanager.settingsmanager.dtos

import measuremanager.settingsmanager.entities.CuSetting

data class CuGw(val cuNetworkId : Long, val gw: Long?)

fun CuSetting.toCuGw() : CuGw = CuGw(  networkId, gw?.id)
