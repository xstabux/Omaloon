package ol.world.blocks.pressure;

import mindustry.gen.Building;

import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureLeveler extends MirrorBlock {
    public PressureLeveler(String name) {
        super(name);
    }

    public class PressureLevelerBuild extends MirrorBlockBuild {
        @Override
        public void updateBoth(Building aa, Building bb) {
            float app = ((PressureAbleBuild) aa).pressure();


            if(app > 1) {



                ((PressureAbleBuild) bb).pressure(app);
            }


        }
    }
}