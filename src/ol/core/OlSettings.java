package ol.core;

import ol.*;

import static mindustry.Vars.ui;

public class OlSettings{
    public static void init(){
        boolean tmp = SettingsManager.uiRechallenged.get();

        SettingsManager.uiRechallenged.set(false);
        SettingsManager.uiRechallenged.set(tmp);
    }

    public static void loadCategory(){
        //add omaloon settings
        ui.settings.addCategory("@mod.ol.omaloon-settings", OlVars.fullName("settings-icon"), table -> {
            //checks
            table.checkPref(SettingsManager.show.key, SettingsManager.show.def());
            table.checkPref(SettingsManager.check.key, SettingsManager.check.def());
            table.checkPref(SettingsManager.clarrows.key, SettingsManager.clarrows.def());
            table.checkPref(SettingsManager.checkCrashes.key, SettingsManager.checkCrashes.def());
        });
    }
}
