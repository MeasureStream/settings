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

        val user = User().apply {
            name="polito"
            surname = "polito"
            email="polito@polito.it"
            userId = "b0be4ea5-17d3-4e63-ad81-510b4532dac8"//"6533c601-0db1-47a6-a150-f402cb142362"//"1d445807-c24e-4513-884d-22451ce9cf67"
            role = "customer"

            muSettings = mutableSetOf()
            cuSettings= mutableSetOf()
        }
        ur.save(user)

        val gateway = Gateway().apply {
            id = 1
            cus = mutableSetOf()
        }

        val controlUnitsSettings = List(10) { i ->
            CuSetting().apply {
                networkId = (i + 1).toLong()
                bandwith = 0
                codingRate = 0
                spreadingFactor = 0
                updateInterval = 0
                gw = gateway
                this.user = user
                mus = mutableSetOf()
            }
        }

        val measurementUnits = List(20) { i ->
            MuSetting().apply {
                networkId = (i + 1).toLong()
                samplingFrequency = 0
                this.user = user
            }
        }

        gateway.cus.addAll(controlUnitsSettings)


        controlUnitsSettings.forEachIndexed { i, it ->
            it.mus.add(measurementUnits[i])
            it.mus.add(measurementUnits[i+10])

            measurementUnits[i].cu = it
            measurementUnits[i+10].cu = it
        }

        user.cuSettings.addAll(controlUnitsSettings)
        user.muSettings.addAll(measurementUnits)


        ur.save(user)
        gr.save(gateway)
        cur.saveAll(controlUnitsSettings)
        mur.saveAll(measurementUnits)
    }
}