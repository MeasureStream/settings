package measuremanager.settingsmanager.services

import measuremanager.settingsmanager.dtos.MuCreateDTO
import measuremanager.settingsmanager.dtos.MuSettingDTO

interface MuSettingService {
    fun create(m:MuSettingDTO): MuSettingDTO
    fun create(m:MuCreateDTO): MuSettingDTO
    fun read(id:Long) : MuSettingDTO
    fun listAll():List<MuSettingDTO>
    fun update(m:MuSettingDTO) : MuSettingDTO
    fun delete(id:Long)
}