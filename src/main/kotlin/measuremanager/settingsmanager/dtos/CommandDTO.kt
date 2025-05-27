package measuremanager.settingsmanager.dtos

data class CommandDTO(val commandId : Long, val gateway : Long, val cu : Long, val mu: Long, val type: String, val cuSettingDTO: CuSettingDTO?, val muSettingDTO: MuSettingDTO?  )
