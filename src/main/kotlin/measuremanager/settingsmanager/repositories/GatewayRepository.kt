package measuremanager.settingsmanager.repositories

import measuremanager.settingsmanager.entities.Gateway
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GatewayRepository:JpaRepository<Gateway, Long> {
}