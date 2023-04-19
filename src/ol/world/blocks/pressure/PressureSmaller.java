package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.Vars;
import mindustry.annotations.Annotations.Load;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import ol.utils.Angles;
import ol.utils.RegionUtils;
import ol.utils.pressure.PressureAPI;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureSmaller extends MirrorBlock {
    private static TextureRegion plusRegion;
    private static TextureRegion minesRegion;

    public float delta = 30;
    public float pressure = 2f;
    public int tier = -1;

    public PressureSmaller(String name) {
        super(name);

        this.update = true;
        this.noUpdateDisabled = true;
    }

    @Override
    public void load() {
        super.load();

        if(plusRegion == null || minesRegion == null) {
            minesRegion = RegionUtils.getRegion("mines");
            plusRegion = RegionUtils.getRegion("plus");
        }
    }

    public class PressureSmallerBuild extends MirrorBlockBuild {
        @Override
        public void updateBoth(Building aa, Building bb) {
            PressureAbleBuild pab = (PressureAbleBuild) aa;
            PressureAbleBuild pbb = (PressureAbleBuild) bb;

            float tmp = pbb.pressure()-pressure;
            if(PressureAPI.tierAble(pab.tier(), tier) && -pab.pressure() < tmp && !Vars.state.isPaused()
                    && Math.floor(Time.globalTime) % delta == 0 && PressureAPI.tierAble(pab, pbb) &&
                    !       pab.isPressureDamages() && !pbb.isPressureDamages())
            {
                pab.pressure(Math.min(-tmp, 0));
                pbb.pressure(Math.max(pbb.pressure() - 1, 0));
            }
        }
    }
}