package measuremanager.settingsmanager.services

import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.dtos.GatewayDTO


interface GatewayService {
    fun create(c : CommandDTO) : CommandDTO
    fun read(id: Long): GatewayDTO
    fun sendRead(id:Long): CommandDTO
    fun listAll():List<GatewayDTO>
    fun sendUpdate(c : CommandDTO): CommandDTO
    fun update(c: GatewayDTO): GatewayDTO
    fun delete(id:Long)
}