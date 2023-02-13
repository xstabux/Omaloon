package ol.world.blocks.pressure;

import mindustry.content.Liquids;
import mindustry.gen.Building;

import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import ol.content.OlLiquids;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureLeveler extends MirrorBlock {
    public PressureLeveler(String name) {
        super(name);
    }

    public class PressureLevelerBuild extends MirrorBlockBuild {
        public LiquidStack[] getLiquid() {
            LiquidStack[] empty = LiquidStack.with(OlLiquids.nothing, 0);

            if(this.antiNearby() instanceof PressureAbleBuild build) {
                return switch(build.tier()) {
                    case 1, 2, 3, -1 -> LiquidStack.with(
                        switch(build.tier()) {
                            case 1 -> empty;
                            case 2 -> OlLiquids.angeirum;
                            case 3 -> Liquids.slag;
                            case -1 -> empty;

                            default -> throw new IllegalStateException("Unexpected value: " + build.tier());
                        }, 0.4f
                    );
                    default -> empty;
                };
            }

            return empty;
        }

        @Override
        public void updateBoth(Building aa, Building bb) {
            float app = ((PressureAbleBuild) aa).pressure();


            if(app > 1) {



                ((PressureAbleBuild) bb).pressure(app);
            }


        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return true;
        }
    }
}