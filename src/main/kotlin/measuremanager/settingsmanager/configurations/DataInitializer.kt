package measuremanager.settingsmanager.configurations


import jakarta.annotation.PostConstruct
import measuremanager.settingsmanager.entities.CuSetting
import measuremanager.settingsmanager.entities.Gateway
import measuremanager.settingsmanager.entities.MuSetting
import measuremanager.settingsmanager.entities.User
import measuremanager.settingsmanager.repositories.CuSettingRepository
import measuremanager.settingsmanager.repositories.GatewayRepository
import measuremanager.settingsmanager.repositories.MuSettingRepository
import measuremanager.settingsmanager.repositories.UserRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component


@Component
class DataInitializer(
    val gr : GatewayRepository,
    val cur: CuSettingRepository,
    val mur: MuSettingRepository,
    val ur: UserRepository
) : InitializingBean{
    @PostConstruct
    fun init(){
        print("initialize fake data")
    }

    override fun afterPropertiesSet() {
        println("Initializing fake data")

        // 1. Crea e salva User
        val user = User().apply {
            name = "polito"
            surname = "polito"
            email = "polito@polito.it"
            userId = "b0be4ea5-17d3-4e63-ad81-510b4532dac8"
            role = "customer"
            muSettings = mutableSetOf()
            cuSettings = mutableSetOf()
        }
        ur.save(user)

        // 2. Crea e salva Gateway
        val gateway = Gateway().apply {
            id = 1
            cus = mutableSetOf()
        }
        val savedGateway = gr.save(gateway)

        // 3. Crea ControlUnits
        val controlUnitsSettings = List(10) { i ->
            CuSetting().apply {
                networkId = (i + 1).toLong()
                bandwidth = 0
                codingRate = 0
                spreadingFactor = 0
                updateInterval = 0
                gw = savedGateway
                this.user = user
                mus = mutableSetOf()
            }
        }

        // 4. Salva CuSettings PRIMA di usarli nei MuSetting
        cur.saveAll(controlUnitsSettings)

        // 5. Crea MuSettings associati a CuSetting salvati
        val measurementUnits = List(20) { i ->
            val relatedCu = controlUnitsSettings[i % 10] // usa 10 CU per 20 MU
            MuSetting().apply {
                networkId = (i + 1).toLong()
                samplingFrequency = 0
                this.user = user
                this.cu = relatedCu
            }.also { mu ->
                relatedCu.mus.add(mu)
            }
        }

        // 6. Salva MuSettings
        mur.saveAll(measurementUnits)

        // 7. Aggiorna relazioni inverse
        savedGateway.cus.addAll(controlUnitsSettings)
        user.cuSettings.addAll(controlUnitsSettings)
        user.muSettings.addAll(measurementUnits)

        // 8. Salvataggi finali
        gr.save(savedGateway)
        ur.save(user)
    }


}