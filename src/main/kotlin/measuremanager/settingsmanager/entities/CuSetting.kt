package measuremanager.settingsmanager.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class CuSetting {
    @Id
    var networkId : Long = 0

    var bandwidth:Long = 0
    var codingRate : Long = 0
    var spreadingFactor : Long = 0
    var updateInterval : Long = 0
    var updateTxPower: Long = 0

    @ManyToOne
    lateinit var user: User

    @ManyToOne
    var gw : Gateway? = null

    @OneToMany
    lateinit var mus: MutableSet<MuSetting>
}