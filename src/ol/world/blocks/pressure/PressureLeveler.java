package ol.world.blocks.pressure;

import arc.struct.ObjectMap;
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
                ObjectMap<Liquid, Seq<LiquidStack>> map = new ObjectMap<>();
                for(var stack : stacks) {
                    if(map.containsKey(stack.liquid)) {
                        map.get(stack.liquid).add(stack);
                    } else {
                        map.put(stack.liquid, Seq.with(stack));
                    }
                }
                var keys = map.keys().toSeq();
                LiquidStack[] len = new LiquidStack[keys.size];
                int j = 0;
                for(var key : keys) {
                    len[j++] = new LiquidStack(key, map.get(key).sumf(s -> s.amount));
                }
                return len;
            }
            return LiquidStack.empty;
        }

        @Override
        public void updateBoth(Building aa, Building bb) {
            PressureAbleBuild inputBuild = (PressureAbleBuild) aa;
            PressureAbleBuild outputBuild = (PressureAbleBuild) bb;

            float inputPressure = inputBuild.pressure();
            float outputPressure = outputBuild.pressure();

            if (inputPressure > outputPressure) {
                outputBuild.pressure(inputPressure);
            } else if (outputPressure > inputPressure) {
                //will be created bug
                inputBuild.pressure(outputPressure);
            }

            var l = getLiquid();
            if(outputPressure < inputPressure && l.length > 0) {
                float pressureDifference = Math.abs(inputPressure - outputPressure);

                for(LiquidStack stack : l) {
                    if(stack.amount > 0) {
                        consume(stack, pressureDifference);
                        liquids.remove(stack.liquid, pressureDifference * liquidConsumption);
                    }
                }
            }
        }

        @Override
        public boolean canConsume() {
            var liquidArray = getLiquid();
            if(liquidArray.length == 0 || liquidArray[0] == null) return super.canConsume();
            return super.canConsume() && liquids.get(liquidArray[0].liquid) > 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == getLiquid()[0].liquid;
        }

        private void consume(LiquidStack liquidStack, float amount) {
            if (liquidConsumption > 0 && liquidStack.amount > 0) {
                liquidStack.amount -= amount;
            }
        }
    }
}