package measuremanager.settingsmanager.services

import jakarta.persistence.EntityNotFoundException
import measuremanager.settingsmanager.dtos.CommandDTO
import measuremanager.settingsmanager.dtos.GatewayDTO
import measuremanager.settingsmanager.dtos.toDTO
import measuremanager.settingsmanager.entities.CuSetting
import measuremanager.settingsmanager.entities.Gateway
import measuremanager.settingsmanager.mqtt.MqttService
import measuremanager.settingsmanager.mqtt.MqttServiceInterface
import measuremanager.settingsmanager.repositories.CuSettingRepository
import measuremanager.settingsmanager.repositories.GatewayRepository
import measuremanager.settingsmanager.repositories.MuSettingRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrElse

@Service
class GatewayServiceImpl(private val gr:GatewayRepository, private val cr: CuSettingRepository, private val mr: MuSettingRepository, private val mq : MqttServiceInterface) : GatewayService {
    override fun create(c: CommandDTO): CommandDTO {
        // praticamente non viene mai chiamato
        throw Exception("This operation is not allowed yet")

    }

    override fun read(id: Long) : GatewayDTO {
        val gw = gr.findById(id).getOrElse { throw EntityNotFoundException() }
        return gw.toDTO()
    }

    override fun sendRead(id: Long): CommandDTO {
        val gw = gr.findById(id).getOrElse { throw EntityNotFoundException() }
        val c = CommandDTO(
            commandId = 1,
            gateway = id,
            cu = -1,
            mu=-1,
            type="read",
            cuSettingDTO = null,
            muSettingDTO = null
        )
        mq.sendCommandToGW(c, c.type)
        return c
    }

    override fun listAll(): List<GatewayDTO> {
        //TODO(permettere questa opzione solo all'admin)
        return gr.findAll().map { it.toDTO() }
    }

    override fun sendUpdate(c: CommandDTO): CommandDTO {
        val gw = gr.findById(c.gateway).getOrElse { throw EntityNotFoundException() }
        if (c.type != "update") throw Exception("Operation not allowed")

        mq.sendCommandToGW(c, c.type)
        return c
    }

    override fun update(g : GatewayDTO ): GatewayDTO {
        // risponde ad un annunce di gateway aggiorna tutta la lista di cus se gw non esiste lo crea , uno dei pochi modi di creare un gw
        val gw = gr.findById(g.id)
            .getOrDefault(Gateway().apply { id = g.id; cus= mutableSetOf() })

        val cus = mutableSetOf<CuSetting>()

        for(cId in g.cus) {
           cus.add(cr.findById(cId)
               .getOrDefault(CuSetting().apply { networkId = cId; mus= mutableSetOf(); this.gw = gw }))
        }
        gw.cus.clear()
        gw.cus.addAll(cus)



        return gr.save(gw).toDTO()

    }

    override fun delete(id: Long) {
        gr.deleteById(id)
    }
}