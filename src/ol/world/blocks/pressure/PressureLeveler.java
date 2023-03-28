package ol.world.blocks.pressure;

import arc.struct.Seq;
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
            Building[] a = getAntiNearby();
            var valid = new Seq<Building>();
            boolean consumesLiquid = false;
            for(var anti : a) {
                if(anti instanceof PressureAbleBuild build && build.tier() >= 2) {
                    valid.add(anti);
                    consumesLiquid = true;
                }
            }
            if(consumesLiquid) {
                LiquidStack[] stacks = new LiquidStack[valid.size];
                final int[] i = {0};
                valid.forEach((b) -> {
                    Liquid liquid = ((PressureAbleBuild) b).tier() == 2 ? OlLiquids.angeirum : Liquids.slag;
                    stacks[i[0]++] = LiquidStack.with(liquid, 0.04f)[0];
                });
                return stacks;
            }
            return LiquidStack.with(OlLiquids.nothing, 0);
        }

        @Override
        public void updateBoth(PressureAbleBuild[] aa, PressureAbleBuild[] bb) {
            //PressureAbleBuild inputBuild = (PressureAbleBuild) aa;
            //PressureAbleBuild outputBuild = (PressureAbleBuild) bb;

            float inputPressure = 0;
            for(PressureAbleBuild inputBuild : aa) {
                if(inputBuild != null) {
                    inputPressure += inputBuild.pressure();
                }
            }

            boolean tmp = false;
            for(PressureAbleBuild outputBuild : bb) {
                if(outputBuild != null && inputPressure > outputBuild.pressure()) {
                    outputBuild.pressure(inputPressure);
                    tmp = true;
                }

                //inputPressure < outputBuild.pressure()
                //this is condition is bug.
            }

            LiquidStack[] liquidStack = getLiquid();
            if(liquids != null && tmp) {
                for(var stack : liquidStack) {
                    if (stack.amount > 0) {
                        consume(stack);
                        liquids.remove(stack.liquid, stack.amount);
                    }
                }
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
                liquidStack.amount = 0;
            }
        }
    }
}