package measuremanager.settingsmanager.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import measuremanager.settingsmanager.dtos.MuCreateDTO
import measuremanager.settingsmanager.dtos.MuSettingDTO
import measuremanager.settingsmanager.dtos.toDTO
import measuremanager.settingsmanager.entities.CuSetting
import measuremanager.settingsmanager.entities.Gateway
import measuremanager.settingsmanager.entities.MuSetting
import measuremanager.settingsmanager.entities.User
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
class MuSettingServiceImpl(private val mr:MuSettingRepository, private val ur:UserRepository, private val mq :MqttServiceInterface, private val gr : GatewayRepository, private val cr : CuSettingRepository) :MuSettingService {
    @Transactional
    override fun create(m: MuSettingDTO): MuSettingDTO {
        val user  = getOrCreateCurrentUserId()
        val me = MuSetting().apply {
            samplingFrequency = m.samplingFrequency
        }
        user.muSettings.add(me)
        ur.save(user)
        return mr.save(me).toDTO()
    }
    @Transactional
    override fun create(m: MuCreateDTO): MuSettingDTO {
        val user  = getOrCreateUserId( m.userId)
        val me = mr.findById(m.networkId).getOrDefault( MuSetting().apply { networkId = m.networkId ; this.user = user })

        user.muSettings.add(me)
        ur.save(user)

        return mr.save(me).toDTO()
    }

    override fun read(id: Long): MuSettingDTO {
        val me = mr.findById(id).getOrElse { throw EntityNotFoundException("not found a MuSetting with id : ${id}") }
        if(me.user.userId != getCurrentUserId() && !isAdmin() ) throw  Exception("You can't get an Entity owned by someone else")
        return me.toDTO()
    }

    override fun listAll(): List<MuSettingDTO> {
        if(isAdmin())
            return mr.findAll().map{it.toDTO()}
        val userid = getCurrentUserId()
        return mr.findAllByUser_UserId(userid).map { it.toDTO() }
    }

    override fun sendUpdate(m: MuSettingDTO): MuSettingDTO {
        val userid = getCurrentUserId()
        val me = mr.findById(m.networkId).getOrElse { throw EntityNotFoundException() }
        if(me.user.userId != userid && !isAdmin()) throw  Exception("You can't update an Entity owned by someone else")
        me.apply {
            samplingFrequency = m.samplingFrequency
        }
        mq.sendCommandToMu(me.toDTO(), "update")
        return me.toDTO()
    }

    @Transactional
    override fun update(m: MuSettingDTO): MuSettingDTO {
        //val userid = getCurrentUserId()
        val me = mr.findById(m.networkId).getOrElse { MuSetting().apply { networkId = m.networkId } }
        val gw = gr.findById(m.gateway!!).getOrElse { Gateway().apply { id = m.gateway } }
        val ce = cr.findById(m.cu!!).getOrElse { CuSetting().apply { networkId = m.cu } }

        //if(me.user.userId != userid) throw  Exception("You can't update an Entity owned by someone else")
        me.apply {
            samplingFrequency = m.samplingFrequency
            cu = ce
        }

        ce.mus.add(me)
        gw.cus.add(ce)

        gr.save(gw)
        cr.save(ce)
        val result =   mr.save(me)

        return result.toDTO()
    }

    //m=Id
    override fun start(m: Long): Long {
        val MU = mr.findById(m).getOrElse { throw EntityNotFoundException("not found a MuSetting with id : ${m}") }
        mq.sendCommandToMu(MU.toDTO(), "start")

        return 1
    }

    override fun stop(m: Long): Long {
        val MU = mr.findById(m).getOrElse { throw EntityNotFoundException("not found a MuSetting with id : ${m}") }
        mq.sendCommandToMu(MU.toDTO(), "stop")

        return 1
    }

    override fun delete(id: Long, kafka: Boolean) {

        val me = mr.findById(id).getOrElse { throw EntityNotFoundException() }

        if(!kafka){
            val userid = getCurrentUserId()
            if(me.user.userId != userid && !isAdmin() ) throw  Exception("You can't delete an Entity owned by someone else")
        }

        mr.delete(me)
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