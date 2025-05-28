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
            cus = mutableSetOf()
        }
        val savedGateway = gr.save(gateway)

        // 3. Crea MeasurementUnits
        val measurementUnits = List(20) { i ->
            MuSetting().apply {
                networkId = (i + 1).toLong()
                samplingFrequency = 0
                this.user = user
            }
        }

        // 4. Crea ControlUnits associandoli a Gateway, User e MuSetting
        val controlUnitsSettings = List(10) { i ->
            CuSetting().apply {
                networkId = (i + 1).toLong()
                bandwith = 0
                codingRate = 0
                spreadingFactor = 0
                updateInterval = 0
                gw = savedGateway
                this.user = user
                mus = mutableSetOf()
            }.also { cu ->
                cu.mus.add(measurementUnits[i])
                cu.mus.add(measurementUnits[i + 10])
                measurementUnits[i].cu = cu
                measurementUnits[i + 10].cu = cu
            }
        }

        // 5. Aggiorna relazioni inverse
        savedGateway.cus.addAll(controlUnitsSettings)
        user.cuSettings.addAll(controlUnitsSettings)
        user.muSettings.addAll(measurementUnits)

        // 6. Salvataggio finale (ordine importante!)
        gr.save(savedGateway) // per aggiornare la relazione con CU
        ur.save(user)         // per aggiornare relazioni bidirezionali
        cur.saveAll(controlUnitsSettings)
        mur.saveAll(measurementUnits)
    }

}