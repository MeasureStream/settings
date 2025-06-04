package measuremanager.settingsmanager.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Gateway {
    @Id
    var id: Long = 0

    @OneToMany(mappedBy = "gw", cascade = [CascadeType.ALL], orphanRemoval = true)
    lateinit var cus : MutableSet<CuSetting>

}