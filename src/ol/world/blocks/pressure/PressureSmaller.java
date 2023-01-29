package ol.world.blocks.pressure;

import mindustry.gen.Building;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureSmaller extends MirrorBlock {
    public float revers = 2f;
    public float pressure = 2f;

    public PressureSmaller(String name) {
        super(name);
    }

    public class PressureSmallerBuild extends MirrorBlockBuild {
        @Override
        public void updateNearby(Building building) {
            PressureAbleBuild build = (PressureAbleBuild) building;
            build.pressure(build.pressure() - pressure);
        }

        @Override
        public void updateAntiNearby(Building building) {
            PressureAbleBuild build = (PressureAbleBuild) building;
            build.pressure(build.pressure() - pressure/4);
        }
    }
}