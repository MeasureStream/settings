package measuremanager.settingsmanager.dtos

import measuremanager.settingsmanager.entities.Gateway

data class GatewayDTO(val id : Long, val cus : Set<Long>) //val mus: Set<Long>)

fun Gateway.toDTO() : GatewayDTO = GatewayDTO(id, cus.map { it.networkId }.toSet() )//cus.map { it.mus.map { m -> m.networkId } }.flatten().toSet() )
