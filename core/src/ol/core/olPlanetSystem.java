/*package ol.core;

import arc.Events;
import mindustry.*;
import mindustry.game.EventType;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mma.*;
import ol.content.*;

import static mma.ModVars.modInfo;

public class olPlanetSystem{
    static Planet prevStar;
    static Planet olStar;

    public static void setup(){
        olStar = olPlanets.amsha;
        ModListener.updaters.add(olPlanetSystem::checkChanges);

    }

    private static void updatePlanet(Planet planet){
        planet.position.setZero();
        planet.addParentOffset(planet.position);
        if(planet.parent != null){
            planet.position.add(planet.parent.position);
        }
        for(Planet child : planet.children){
            updatePlanet(child);
        }
    }

    private static void checkChanges(){
        updatePlanet(olStar);
        PlanetParams state = Vars.ui.planet.state;
        if(state.solarSystem == olStar){
            if(state.planet.minfo.mod != modInfo){
                state.solarSystem = state.planet.solarSystem;
                updateDialog();
            }
        }else{
            if(state.planet.minfo.mod == modInfo){
                prevStar = state.solarSystem;
                state.solarSystem = olStar;
                updateDialog();
            }
        }

    }

    private static void updateDialog(){

        PlanetDialog dialog = Vars.ui.planet;
        if(dialog.isShown()){

            dialog.show(dialog.getScene(), null);
        }
    }
}
*/