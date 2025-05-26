package measuremanager.settingsmanager.repositories

import measuremanager.settingsmanager.entities.CuSetting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CuSettingRepository: JpaRepository<CuSetting, Long> {

    fun findAllByUser_UserId(userId : String ) : List<CuSetting>
}