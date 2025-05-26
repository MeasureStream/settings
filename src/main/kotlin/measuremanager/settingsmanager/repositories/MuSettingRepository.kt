package measuremanager.settingsmanager.repositories

import measuremanager.settingsmanager.entities.MuSetting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MuSettingRepository:JpaRepository<MuSetting, Long> {
    fun findAllByUser_UserId(userId : String ) : List<MuSetting>
}