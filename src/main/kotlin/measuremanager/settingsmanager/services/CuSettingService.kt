package measuremanager.settingsmanager.services

import measuremanager.settingsmanager.dtos.CuCreateDTO
import measuremanager.settingsmanager.dtos.CuGw
import measuremanager.settingsmanager.dtos.CuSettingDTO

interface CuSettingService {
    fun create(c : CuCreateDTO) : CuSettingDTO
    fun create(c : CuSettingDTO) : CuSettingDTO
    fun read(id: Long):CuSettingDTO
    fun readlist(ids: List<Long> ) : List<CuGw>
    fun listAll():List<CuSettingDTO>
    fun sendUpdate(c : CuSettingDTO): CuSettingDTO
    fun update(c:CuSettingDTO):CuSettingDTO
    fun delete(id:Long, kafka: Boolean)
}