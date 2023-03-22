package ol.world.blocks.pressure;

import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;

import ol.content.*;
import ol.world.blocks.pressure.meta.*;
import ol.world.consumers.*;

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

            float inputPressure = inputBuild.pressure();
            float outputPressure = outputBuild.pressure();
            float pressureDifference = Math.abs(inputPressure - outputPressure);

            if (inputPressure > outputPressure) {
                outputBuild.pressure(inputPressure);
            } else if (outputPressure > inputPressure) {
                inputBuild.pressure(outputPressure);
            }

            LiquidStack[] liquidStack = getLiquid();
            if (liquids != null && liquidStack.length > 0 && liquidStack[0].amount > 0 && pressureDifference > 0) {
                consume(liquidStack[0], pressureDifference);
                liquids.remove(liquidStack[0].liquid, pressureDifference * liquidConsumption);
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

        private void consume(LiquidStack liquidStack, float pressureDifference) {
            if (liquidConsumption > 0 && liquidStack.amount > 0) {
                float consumption = pressureDifference * liquidConsumption;
                liquidStack.amount -= consumption;
            }
        }
    }
}