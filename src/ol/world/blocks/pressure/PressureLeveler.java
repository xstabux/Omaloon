package ol.world.blocks.pressure;

import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import ol.content.OlLiquids;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;

public class PressureLeveler extends MirrorBlock {
    public float liquidConsumption;
    public float consumption;

    public PressureLeveler(String name) {
        super(name);
    }

    public class PressureLevelerBuild extends MirrorBlockBuild {
        public LiquidStack[] getLiquid() {
            LiquidStack[] empty = LiquidStack.with(OlLiquids.nothing, 0);

            if (this.antiNearby() instanceof PressureAbleBuild build) {
                return switch (build.tier()) {
                    case 2, 3 -> LiquidStack.with(build.tier() == 2 ?
                            OlLiquids.angeirum : Liquids.slag, 0.4f);
                    default -> empty;
                };
            }

            return empty;
        }

        @Override
        public void updateBoth(Building input, Building output) {
            // Get pressure from the input side
            float pressure = ((PressureAbleBuild) input).pressure();

            // If pressure is above 1, transfer pressure to the output side
            if (pressure > 1) {
                ((PressureAbleBuild) output).pressure(pressure);
            }

            // Decrease liquid amount when pressure changes
            if (input.liquids != null) {
                input.liquids.remove(getLiquid()[0].liquid, pressure * liquidConsumption);
                this.consume();
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == getLiquid()[0].liquid;
        }

        // Decreases the amount of the liquid that is being consumed by the PressureLeveler
        public void consume() {
            if (consumption > 0 && getLiquid()[0].amount > 0) {
                getLiquid()[0].amount -= consumption;
            }
        }
    }
}
