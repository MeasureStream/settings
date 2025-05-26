package measuremanager.settingsmanager.services

import jakarta.persistence.EntityNotFoundException
import measuremanager.settingsmanager.dtos.CuCreateDTO
import measuremanager.settingsmanager.dtos.CuSettingDTO
import measuremanager.settingsmanager.dtos.toDTO
import measuremanager.settingsmanager.entities.CuSetting
import measuremanager.settingsmanager.entities.User
import measuremanager.settingsmanager.repositories.CuSettingRepository
import measuremanager.settingsmanager.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Service
class CuSettingServiceImpl(private val cr: CuSettingRepository, private val ur :  UserRepository):CuSettingService {
    override fun create(c: CuSettingDTO): CuSettingDTO {
        val user  = getOrCreateCurrentUserId()
        val ce = CuSetting().apply {
            networkId = c.networkId
            bandwith = c.bandwith
            codingRate = c.codingRate
            spreadingFactor = c.spreadingFactor
            updateInterval = c.updateInterval
        }

        user.cuSettings.add(ce)
        ur.save(user)

        return cr.save(ce).toDTO()
    }

    override fun create(c : CuCreateDTO) : CuSettingDTO {
        val user  = getOrCreateUserId( c.userId)

        val ce = CuSetting().apply {
            networkId = c.networkId
        }

        user.cuSettings.add(ce)
        ur.save(user)

        return cr.save(ce).toDTO()
    }

    override fun read(id: Long): CuSettingDTO {
        val ce = cr.findById(id).getOrNull()
        if (ce != null && ce.user.userId != getCurrentUserId()) throw  Exception("You can't get an Entity owned by someone else")
        if(ce == null ) throw EntityNotFoundException()

        return ce.toDTO()
    }

    override fun listAll(): List<CuSettingDTO> {
        val userid = getCurrentUserId()
        return cr.findAllByUser_UserId(userid).map { it.toDTO() }
    }

    override fun update(c: CuSettingDTO): CuSettingDTO {
        val userid = getCurrentUserId()
        val ce = cr.findById(c.networkId).getOrElse { throw EntityNotFoundException() }
        if (ce.user.userId != userid) throw  Exception("You can't update an Entity owned by someone else")

        ce.apply {
            bandwith = c.bandwith
            codingRate = c.codingRate
            spreadingFactor = c.spreadingFactor
            updateInterval = c.updateInterval
        }

        return cr.save(ce).toDTO()

    }

    override fun delete(id: Long) {
        val userid = getCurrentUserId()
        val ce = cr.findById(id).getOrElse { throw EntityNotFoundException() }
        if (ce.user.userId != userid) throw  Exception("You can't delete an Entity owned by someone else")
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
}