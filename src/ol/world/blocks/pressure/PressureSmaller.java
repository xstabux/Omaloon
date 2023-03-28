package ol.world.blocks.pressure;

import arc.util.Time;
import mindustry.gen.Building;
import ol.utils.pressure.PressureAPI;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureSmaller extends MirrorBlock {
    public float delta = 30;
    public float pressure = 2f;

    public PressureSmaller(String name) {
        super(name);

        this.update = true;
        this.noUpdateDisabled = true;
    }

    public class PressureSmallerBuild extends MirrorBlockBuild {
        @Override
        public void updateBoth(PressureAbleBuild pab, PressureAbleBuild pbb) {
            if(Math.floor(Time.globalTime) % 30 == 0 &&
                    PressureAPI.tierAble(pab.tier(), pbb.tier()))
            {
                pab.pressure(-pbb.pressure());
                pbb.pressure(pbb.pressure() - 1);
            }
        }
    }
}