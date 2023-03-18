package ol.world.blocks.pressure;

import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;

import ol.content.OlLiquids;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAbleBuild;
import ol.world.consumers.ConsumeLiquidDynamic;

public class PressureLeveler extends MirrorBlock {
    public float liquidConsumption;

    public PressureLeveler(String name) {
        super(name);

        consume(new ConsumeLiquidDynamic(PressureLevelerBuild::getLiquid));
    }

    public class PressureLevelerBuild extends MirrorBlockBuild {
        public LiquidStack[] getLiquid() {
            Building anti = getAntiNearby();
            if (anti instanceof PressureAbleBuild build && build.tier() >= 2) {
                Liquid liquid = build.tier() == 2 ? OlLiquids.angeirum : Liquids.slag;
                return LiquidStack.with(liquid, 0.004f);
            } else {
                return LiquidStack.with(OlLiquids.nothing, 0);
            }
        }

        @Override
        public void updateBoth(Building aa, Building bb) {
            PressureAbleBuild inputBuild = (PressureAbleBuild) aa;
            PressureAbleBuild outputBuild = (PressureAbleBuild) bb;

            float pressure = inputBuild.pressure();
            if (pressure > 1) {
                outputBuild.pressure(pressure);
            }

            LiquidStack[] liquidStack = getLiquid();
            if (liquids != null && liquidStack.length > 0 && liquidStack[0].amount > 0) {
                liquids.remove(liquidStack[0].liquid, pressure * liquidConsumption);
                consume(liquidStack[0]);
            }
        }

        @Override
        public boolean canConsume() {
            var stack = getLiquid()[0];
            if(stack == null) return false;
            if(stack.liquid == OlLiquids.nothing) return super.canConsume();
            return super.canConsume() && liquids.get(stack.liquid) > 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == getLiquid()[0].liquid;
        }

        private void consume(LiquidStack liquidStack) {
            if (liquidConsumption > 0 && liquidStack.amount > 0) {
                liquidStack.amount -= liquidConsumption;
            }
        }
    }
}