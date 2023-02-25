package ol.core

import zelaux.arclib.settings.*

object SettingsManager {
    @JvmField
    val uiRechallenged = BooleanSettingKey("ui-rechallenged") { false }

    @JvmField
    val show = BooleanSettingKey("mod.ol.show") { false }

    @JvmField
    val check = BooleanSettingKey("mod.ol.check") { false }

    @JvmField
    var clarrows = BooleanSettingKey("mod.ol.clarrows") { false }

    init {
        SettingKey.allKeys.each { it.setDefault() }
    }
}