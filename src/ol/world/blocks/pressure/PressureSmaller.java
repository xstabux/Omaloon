package ol.world.blocks.pressure;

import mindustry.gen.Building;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureSmaller extends MirrorBlock {
    public float revers = 2f;
    public float pressure = 2f;

    public PressureSmaller(String name) {
        super(name);

        this.update = true;
        this.noUpdateDisabled = true;
    }

    public class PressureSmallerBuild extends MirrorBlockBuild {
        @Override
        public void updateBoth(Building aa, Building bb) {
            PressureAbleBuild pab = (PressureAbleBuild) aa;
            PressureAbleBuild pbb = (PressureAbleBuild) bb;

            pab.pressure(-pbb.pressure());
        }
    }
}