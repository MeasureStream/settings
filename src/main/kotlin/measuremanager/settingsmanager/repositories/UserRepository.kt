package measuremanager.settingsmanager.repositories

import measuremanager.settingsmanager.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository:JpaRepository<User,String> {
}