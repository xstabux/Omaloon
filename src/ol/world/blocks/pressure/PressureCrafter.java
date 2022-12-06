package ol.world.blocks.pressure;

import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.*;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;
import ol.world.blocks.crafting.OlCrafter;

import static ol.graphics.OlPal.*;

public class PressureCrafter extends OlCrafter {
    //how many pressure crafter consumes
    public float pressureConsume = 0;

    //how many pressure crafter
    public float pressureProduce = 0;

    public float maxPressure, dangerPressure;
    public boolean canExplode = true;

    //when block works pressure is make lower
    public boolean downPressure;
    public float downPercent = 0.25f;

    //boom
    public Effect explodeEffect = Fx.none;

    public PressureCrafter(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("pressure", (PressureCrafterBuild b) -> {
            float pressure = b.pressure / maxPressure;

            return new Bar(
                    () -> "pressure",
                    () -> {
                        if(b.isDanger()) {
                            return mixcol(Color.black, OLPressureDanger, b.jumpDelta() / 30);
                        }

                        return mixcol(OLPressureMin, OLPressure, pressure);
                    },
                    () -> pressure
            );
        });

        if(pressureConsume > 0) {
            addBar("efficient", (PressureCrafterBuild b) -> {
                float x = b.efficenty() * 100;

                return new Bar(
                        () -> "efficient: " + (int) Math.floor(x) + "%",
                        () -> Color.orange,
                        () -> Math.min(x / 100, 1)
                );
            });
        }
    }

    public class PressureCrafterBuild extends OlCrafter.olCrafterBuild implements PressureAble {
        public float pressure;
        public float effect;
        public float effectx;
        public float dt = 0;

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(pressure);
        }

        public float maxPressure() {
            return maxPressure;
        }

        public float dangerPressure() {
            return dangerPressure;
        }

        public float pressureConsume() {
            return pressureConsume;
        }

        public float pressureProduce() {
            return pressureProduce;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            pressure = read.f();
        }

        public boolean isDanger() {
            if(dangerPressure() == -1) {
                return false;
            }

            return pressure > dangerPressure() && canExplode;
        }

        public float jumpDelta() {
            return dt > 30 ? 60 - dt : dt;
        }

        @Override
        @SuppressWarnings("unchecked")
        public PressureCrafterBuild self() {
            return this;
        }

        @Override
        public float pressure() {
            return pressure;
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            onUpdate(false, maxPressure(), explodeEffect);
        }

        @Override
        public void pressure(float pressure) {
            this.pressure = pressure;
        }

        @Override
        public boolean online() {
            return pressureConsume() > 0 || pressureProduce() > 0;
        }

        @Override
        public boolean WTR() {
            return true;
        }

        @Override
        public boolean storageOnly() {
            return false;
        }

        @Override
        public float pressureThread() {
            return (pressureProduce() * (effect / 100) * efficenty()) -
                    (downPressure && status() == BlockStatus.active ? (pressureConsume() * downPercent) : 0);
        }

        @Override
        public void craft() {
            //HA
            if(pressureConsume() > 0 && efficenty() == 0) {
                return;
            }

            super.craft();
        }

        @Override
        public int tier() {
            return -1;
        }

        @Override
        public BlockStatus status() {
            BlockStatus SUPER = super.status();
            if(SUPER == BlockStatus.logicDisable || SUPER == BlockStatus.noOutput) {
                return SUPER;
            }

            return pressure <= 0 ? BlockStatus.noInput : SUPER;
        }

        public float efficenty() {
            if(pressureConsume() <= 0) {
                return 1;
            }

            return Math.max(Math.min(pressure/pressureConsume(), 2), 0);
        }

        @Override
        public void updateTile() {
            boolean prevOut = getPowerProduction() <= requestedPower();

            float s = getAcceleration() * efficenty();
            if (hasItems()) {
                progress += getProgressIncrease(craftTime) * s;
                totalProgress += delta() * s;
            }
            totalActivity += delta() * s;

            if (Mathf.chanceDelta(updateEffectChance * s)) {
                updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
            }

            if(!prevOut && (getPowerProduction() > requestedPower())) {
                Events.fire(EventType.Trigger.impactPower);
            }

            if (canConsume()) {
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);
                float e = efficiency;
                if (acceleration <= e) {
                    acceleration = Mathf.approachDelta(acceleration, e, accelerationSpeed * e);
                } else {
                    acceleration = Mathf.approachDelta(acceleration, e, decelerationSpeed);
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                acceleration = Mathf.approachDelta(acceleration, 0f, decelerationSpeed);
            }

            if (progress >= 1f) {
                craft();
                onCraft.get(this);
            }

            totalProgress += acceleration * Time.delta;

            productionEfficiency = Mathf.pow(acceleration, 5f);

            dumpOutputs();

            if(canConsume() && effectx < 100) {
                effectx++;
                if(effectx > 100) {
                    effectx = 100;
                }
            } else {
                if(!canConsume() && effectx > 0) {
                    effectx--;
                    if(effectx < 0) {
                        effectx = 0;
                    }
                }
            }

            effect = effectx * efficenty();

            dt++;
            if(dt >= 60) {
                dt = 0;
            }

            onUpdate(canExplode, maxPressure(), explodeEffect);
        }

        @Override
        public boolean inNet(Building b, PressureAble p, boolean j) {
            return true;
        }
    }
}