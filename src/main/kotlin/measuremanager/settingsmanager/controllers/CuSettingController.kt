package measuremanager.settingsmanager.controllers

import measuremanager.settingsmanager.dtos.CuSettingDTO
import measuremanager.settingsmanager.dtos.MuSettingDTO
import measuremanager.settingsmanager.services.CuSettingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/cu-setting")
class CuSettingController (private val cs : CuSettingService){
    @PostMapping("","/")
    fun create(@RequestBody c : CuSettingDTO ): CuSettingDTO {
        return cs.create(c)
    }
    @GetMapping("/{cusettingid}","/{cusettingid}/")
    fun read(@PathVariable cusettingid:Long): CuSettingDTO {
        return cs.read(cusettingid)
    }
    @GetMapping("","/")
    fun listAll():List<CuSettingDTO>{
        return cs.listAll()
    }
    @PutMapping("","/")
    fun update(@RequestBody c : CuSettingDTO ): CuSettingDTO {
        return cs.update(c)
    }
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{cusettingid}","/{cusettingid}/")
    fun delete(@PathVariable cusettingid:Long){
        cs.delete(cusettingid)
    }
}