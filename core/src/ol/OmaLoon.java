package ol;

import mindustry.Vars;
import mindustry.ctype.*;
import mma.*;
import mma.annotations.*;
import ol.content.*;

import static mindustry.Vars.headless;
import static ol.olVars.*;

@ModAnnotations.ModAssetsAnnotation
public class OmaLoon extends MMAMod{
    public OmaLoon(){
        super();
        olVars.load();
    }

    @Override
    public void init(){
        super.init();
        //if (!Vars.headless) {
            //olPlanetSystem.setup();
        //}
    }

    @Override
    protected void modContent(Content content){
        super.modContent(content);
    }

    public void loadContent(){
        new olItems().load();
        new olLiquids().load();
        new olBlocks().load();
        new olPlanets().load();
    }
}
