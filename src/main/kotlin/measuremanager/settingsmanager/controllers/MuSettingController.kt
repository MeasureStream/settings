package measuremanager.settingsmanager.controllers

import measuremanager.settingsmanager.dtos.MuSettingDTO
import measuremanager.settingsmanager.services.MuSettingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/API/mu-setting")
class MuSettingController(private val ms: MuSettingService) {
    @PostMapping("","/")
    fun create(@RequestBody m : MuSettingDTO):MuSettingDTO{
        return ms.create(m)
    }
    @GetMapping("/{musettingid}","/{musettingid}/")
    fun read(@PathVariable musettingid:Long): MuSettingDTO{
        return ms.read(musettingid)
    }
    @GetMapping("","/")
    fun listAll():List<MuSettingDTO>{
        return ms.listAll()
    }
    @PutMapping("","/")
    fun update(@RequestBody m:MuSettingDTO):MuSettingDTO{
        return ms.update(m)
    }
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{musettingid}","/{musettingid}/")
    fun delete(@PathVariable musettingid:Long){
        ms.delete(musettingid)
    }
}