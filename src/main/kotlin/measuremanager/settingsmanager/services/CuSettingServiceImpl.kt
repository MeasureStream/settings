package measuremanager.settingsmanager.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import measuremanager.settingsmanager.dtos.CuCreateDTO
import measuremanager.settingsmanager.dtos.CuSettingDTO
import measuremanager.settingsmanager.dtos.toDTO
import measuremanager.settingsmanager.entities.CuSetting
import measuremanager.settingsmanager.entities.Gateway
import measuremanager.settingsmanager.entities.User
import measuremanager.settingsmanager.mqtt.MqttService
import measuremanager.settingsmanager.mqtt.MqttServiceInterface
import measuremanager.settingsmanager.repositories.CuSettingRepository
import measuremanager.settingsmanager.repositories.GatewayRepository
import measuremanager.settingsmanager.repositories.MuSettingRepository
import measuremanager.settingsmanager.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Service
class   CuSettingServiceImpl(private val cr: CuSettingRepository, private val ur :  UserRepository, private val mq: MqttServiceInterface, private val gr:GatewayRepository, private val mr: MuSettingRepository):CuSettingService {
    override fun create(c: CuSettingDTO): CuSettingDTO {
        val user  = getOrCreateCurrentUserId()
        val ce = CuSetting().apply {
            networkId = c.networkId
            bandwidth = c.bandwidth
            codingRate = c.codingRate
            spreadingFactor = c.spreadingFactor
            updateInterval = c.updateInterval
            mus = mutableSetOf()
        }

        user.cuSettings.add(ce)
        ur.save(user)
        val result = cr.save(ce).toDTO()
        // creation ->  refresh
        mq.sendCommandToCu(result, "read")
        return result
    }

    @Transactional
    override fun create(c : CuCreateDTO) : CuSettingDTO {
        val user  = getOrCreateUserId( c.userId)
        val ce = cr.findById(c.networkId)
            .getOrDefault(CuSetting().apply { networkId = c.networkId; this.user = user })
        ce.apply {
            mus = mutableSetOf()
        }

        user.cuSettings.add(ce)
        ur.save(user)

        val result = cr.save(ce).toDTO()
        // creation ->  refresh
        //mq.sendCommandToCu(result, "read")
        return result
    }

    override fun read(id: Long): CuSettingDTO {
        val ce = cr.findById(id).getOrNull()
        if (ce != null && ce.user.userId != getCurrentUserId() && !isAdmin() ) throw  Exception("You can't get an Entity owned by someone else")
        if(ce == null ) throw EntityNotFoundException()


        return ce.toDTO()
    }

    override fun listAll(): List<CuSettingDTO> {
        if(isAdmin())
            return cr.findAll().map{it.toDTO()}
        val userid = getCurrentUserId()
        return cr.findAllByUser_UserId(userid).map { it.toDTO() }
    }

    override fun sendUpdate(c : CuSettingDTO): CuSettingDTO{

        val userid = getCurrentUserId()
        val ce = cr.findById(c.networkId).getOrElse { throw EntityNotFoundException() }
        if (ce.user.userId != userid && !isAdmin() ) throw  Exception("You can't update an Entity owned by someone else")
        ce.apply {
            bandwidth = c.bandwidth
            codingRate = c.codingRate
            spreadingFactor = c.spreadingFactor
            updateInterval = c.updateInterval
        }
        val result  = ce.toDTO()
        mq.sendCommandToCu(result, "update")
        return result
    }

    override fun update(c: CuSettingDTO): CuSettingDTO {
        // questa funzione deve essere richiamata solo da mqtt
        //val userid = getCurrentUserId()
        //val ce = cr.findById(c.networkId).getOrElse { throw EntityNotFoundException() }
        //if (ce.user.userId != userid) throw  Exception("You can't update an Entity owned by someone else")
        val gw = gr.findById(c.gateway!!).getOrElse { Gateway().apply { id = c.gateway } }
        val ce = cr.findById(c.networkId).getOrElse { CuSetting().apply { networkId = c.networkId } } // forse da rifare la create per fare l'assegnazione ad un user TODO()
        val mus = mr.findAllById(c.mus)
        ce.apply {
            bandwidth = c.bandwidth
            codingRate = c.codingRate
            spreadingFactor = c.spreadingFactor
            updateInterval = c.updateInterval
            this.gw = gw

        }
        mus.map { it.cu = ce }
        ce.mus.addAll(mus)
        gw.cus.add(ce)

        gr.save(gw)
        val result = cr.save(ce)
        mr.saveAll(mus)


        return result.toDTO()

    }

    override fun delete(id: Long) {
        val userid = getCurrentUserId()
        val ce = cr.findById(id).getOrElse { throw EntityNotFoundException() }
        if (ce.user.userId != userid && !isAdmin()) throw  Exception("You can't delete an Entity owned by someone else")
        cr.delete(ce)

    }

    fun getCurrentUserId(): String {
        val auth = SecurityContextHolder.getContext().authentication
        val jwt = auth.principal as Jwt
        return jwt.subject  // oppure jwt.getClaim<String>("preferred_username")
    }

    fun getCurrentUserInfo(): Map<String, String?> {
        val auth = SecurityContextHolder.getContext().authentication
        val jwt = auth.principal as Jwt
        return mapOf(
            "userId" to jwt.subject,
            "email" to jwt.getClaim<String>("email"),
            "givenName" to jwt.getClaim<String>("given_name"),
            "familyName" to jwt.getClaim<String>("family_name"),
            "preferredUsername" to jwt.getClaim<String>("preferred_username")
        )
    }

    fun getOrCreateCurrentUserId(): User {
        val userId = getCurrentUserId()
        val user = ur.findById(userId).getOrNull()
        if( user != null)
            return user
        val info = getCurrentUserInfo()
        val newUser = User().apply {
            this.userId = userId
            name = info["givenName"] ?: ""
            surname = info["familyName"] ?: ""
            email = info["email"] ?: ""
            cuSettings = mutableSetOf()
            muSettings = mutableSetOf()
        }

        return ur.save(newUser)
    }

    fun getOrCreateUserId( userId : String): User {
        //val userId = getCurrentUserId()
        val user = ur.findById(userId).getOrNull()
        if( user != null)
            return user

        val newUser = User().apply {
            this.userId = userId
            name =  ""
            surname =  ""
            email =  ""
            cuSettings = mutableSetOf()
            muSettings = mutableSetOf()
        }

        return ur.save(newUser)
    }

    fun isAdmin() : Boolean{
        val auth = SecurityContextHolder.getContext().authentication
        val isAdmin = auth.authorities.any { it.authority == "ROLE_app-admin" }
        return isAdmin
    }
}