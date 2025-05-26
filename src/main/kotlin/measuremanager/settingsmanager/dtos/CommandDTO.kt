package measuremanager.settingsmanager.dtos

data class CommandDTO(val commandId : Long, val gateway : Int, val cu : Int, val mu: Int, val type: String, val cuSettingDTO: CuSettingDTO?, val muSettingDTO: MuSettingDTO?  )
