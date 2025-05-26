package measuremanager.settingsmanager.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class MuSetting {
    @Id
    var networkId : Long = 0

    var samplingFrequency : Long = 0

    @ManyToOne
    lateinit var user: User
}