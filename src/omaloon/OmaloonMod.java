package omaloon;

import arc.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.mod.*;
import omaloon.content.*;
import omaloon.core.*;
import omaloon.gen.*;
import omaloon.graphics.*;
import omaloon.ui.dialogs.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class OmaloonMod extends Mod{

    public OmaloonMod(){
        super();
        Events.on(EventType.ClientLoadEvent.class, ignored -> {
           OlSettings.load();
            app.post(() -> {
                if(!settings.getBool("@setting.omaloon.show-disclaimer")){
                    new OlDisclaimerDialog().show();
                }
            });
        });

        Events.on(EventType.FileTreeInitEvent.class, e ->
                Core.app.post(OlShaders::init)
        );

        Events.on(EventType.DisposeEvent.class, e ->
                OlShaders.dispose()
        );

        Log.info("Loaded OmaloonMod constructor.");
    }

    @Override
    public void init(){
        super.init();

        if(headless) return;

        if(settings.getBool("@setting.omaloon.check-updates")){
            OlUpdateCheckDialog.check();
        }

        if(settings.getBool("@setting.omaloon.check-crashes")) {
            new OlCrashReportDialog().load();
        }
    }

    @Override
    public void loadContent(){
        Log.info("Loading some Omaloon content.");
        EntityRegistry.register();
        OlSounds.load();
        OlItems.load();
        OlLiquids.load();
        OlStatusEffects.load();
        OlUnitTypes.load();
        OlBlocks.load();
        OlWeathers.load();
        OlPlanets.load();
    }
}
