package measuremanager.settingsmanager.services

import measuremanager.settingsmanager.dtos.CuCreateDTO
import measuremanager.settingsmanager.dtos.CuSettingDTO

interface CuSettingService {
    fun create(c : CuCreateDTO) : CuSettingDTO
    fun create(c : CuSettingDTO) : CuSettingDTO
    fun read(id: Long):CuSettingDTO
    fun listAll():List<CuSettingDTO>
    fun update(c:CuSettingDTO):CuSettingDTO
    fun delete(id:Long)
}