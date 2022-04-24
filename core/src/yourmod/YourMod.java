package yourmod;

import mindustry.ctype.*;
import mma.*;
import mma.annotations.*;

import static yourmod.YourModVars.*;

/** If you have no sprites, music and sounds in your mod, remove the annotation after this line */
@ModAnnotations.ModAssetsAnnotation
public class YourMod extends MMAMod{
    public YourMod(){
        super();

        YourModVars.load();
    }

    @Override
    protected void modContent(Content content){
        super.modContent(content);
        /*
        * if you use Load annotation in any class that extends MappableContent,
        * ZelauxModCore will generate YOUR_PREFIXContentRegions class and here you can apply that on instances of those classes
        *
        if(content instanceof MappableContent){
            YOUR_PREFIXContentRegions.loadRegions((MappableContent)content);
        }
        */
    }

    public void init(){
        if(!loaded) return;
        super.init();
        //if you do not need ModListener just remove line after this comment
        if(neededInit) listener.init();
    }

    /** There is you can load extra things like ModMusic or ModSounds etc. */
    public void loadContent(){
        super.loadContent();
    }
}
