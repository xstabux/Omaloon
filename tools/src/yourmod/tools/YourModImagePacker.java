package yourmod.tools;

import mindustry.ctype.*;
import mma.tools.*;
import yourmod.*;

public class YourModImagePacker extends ModImagePacker {

    public YourModImagePacker() {
    }

    @Override
    protected void start() throws Exception {
        YourModVars.create();

        super.start();
    }

    @Override
    protected void preCreatingContent() {
        super.preCreatingContent();

//        JLEntityMapping.init();
    }

    @Override
    protected void runGenerators() {
        new YourModGenerators();
    }

    @Override
    protected void checkContent(Content content) {
        super.checkContent(content);
        /*
        * if you use Load annotation in any class that extends MappableContent,
        * ZelauxModCore will generate YOUR_PREFIXContentRegions class and here you can apply that on instances of those classes
        *
        if(content instanceof MappableContent){
            YOUR_PREFIXContentRegions.loadRegions((MappableContent)content);
        }
        */
    }

    public static void main(String[] args) throws Exception {
        new YourModImagePacker();
    }

}
