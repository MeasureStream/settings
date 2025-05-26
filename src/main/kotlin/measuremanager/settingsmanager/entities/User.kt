package measuremanager.settingsmanager.entities

import jakarta.persistence.*

@Entity
@Table(name = "app_user")  // nome alternativo, non riservato
class User {
    @Id
    lateinit var userId : String

    lateinit var name:String

    lateinit var surname:String

    lateinit var email:String

    lateinit var role:String

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    lateinit var muSettings : MutableSet<MuSetting>

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    lateinit var cuSettings : MutableSet<CuSetting>


}