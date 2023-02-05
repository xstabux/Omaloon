package ol.world.blocks.pressure;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;

import ol.graphics.OlGraphics;
import ol.utils.RegionUtils.BlockRegionFinder;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureLeveler extends MirrorBlock {
    public BlockRegionFinder finder = new BlockRegionFinder(this);
    public TextureRegion[] regions = new TextureRegion[4];

    public PressureLeveler(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();

        this.regions = OlGraphics.getRegions(this.finder.getRegion("-sprites"), 4, 1, 32);
    }

    public class PressureLevelerBuild extends MirrorBlockBuild {
        @Override
        public void updateBoth(Building aa, Building bb) {
            ((PressureAbleBuild) bb).pressure(((PressureAbleBuild) aa).pressure());
        }

        @Override
        public void draw() {
            Draw.rect(regions[this.rotation], this.x, this.y);
            this.drawTeamTop();
        }
    }
}