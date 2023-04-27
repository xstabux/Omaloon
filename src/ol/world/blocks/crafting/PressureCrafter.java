package ol.world.blocks.crafting;

import arc.math.*;
import arc.struct.*;
import arc.util.io.*;

import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import ol.world.blocks.pressure.meta.*;
import ol.world.meta.*;

import static arc.Core.*;
import static ol.graphics.OlPal.*;

public class PressureCrafter extends GenericCrafter implements PressureAble {
    public int tier = -1;
    /**how many pressure crafter consumes*/
    public float pressureConsume = 0;
    /**how many pressure produce*/
    public float pressureProduce = 0;
    public float volume = 0.25f;

    public float maxPressure;
    public boolean canExplode = true;
    public boolean showPressure = false;

    /**when block works pressure is make lower*/
    public boolean downPressure;
    public float downPercent = 0.25f;

    public PressureCrafter(String name) {
        super(name);

        flags = EnumSet.of(BlockFlag.factory);
    }

    @Override public void setStats() {
        super.setStats();

        if(showPressure) {
            stats.remove(Stat.productionTime);
        }

        if(tier > 0){
            stats.add(OlStat.tier, tier);
        }

        if(pressureProduce > 0) {
            stats.add(OlStat.pressureProduction, (int) pressureProduce, OlStatUnit.pressure);
        }

        if(pressureConsume > 0) {
            stats.add(OlStat.pressureConsume, (int) pressureConsume, OlStatUnit.pressure);
        }

        if(canExplode) {
            stats.add(OlStat.maxPressure, maxPressure, OlStatUnit.pressure);
        }
    }

    @Override public void setBars() {
        super.setBars();

        if(!showPressure && pressureConsume > 0) {
            this.addBar("pressure", (PressureCrafterBuild b) -> new Bar(
                    () -> bundle.format("bar.pressureEfficient", (int) Math.floor(b.pressure()), (int) (b.efficenty() * 100 + 0.0001f)),
                    () -> oLPressure,
                    b::getPressure
            ));
        } else {
            addBar("pressure", (PressureCrafterBuild b) -> new Bar(
                    () -> bundle.get("bar.pressure") + " " + (int) (b.pressure()),
                    () -> oLPressure,
                    b::getPressure
            ));
        }
    }

    @Override public void init() {
        if(this.pressureConsume > 0) {
            this.consumeBuilder.add(new ConsumePressure() {{
                this.pressureConsume = PressureCrafter.this.pressureConsume;
            }});
        }

        super.init();
    }

    @Override public boolean canExplode() {
        return this.canExplode;
    }

    @Override public float maxPressure() {
        return this.maxPressure;
    }

    @Override public int tier() {
        return this.tier;
    }

    public class PressureCrafterBuild extends GenericCrafterBuild implements PressureAbleBuild {
        public PressureModule pressureModule = new PressureModule();
        public float effect;
        public float effectx;

        @Override public void write(Writes write) {
            super.write(write);
            pressureModule.write(write);
        }

        @Override public void read(Reads read, byte revision) {
            super.read(read, revision);
            pressureModule.read(read);
        }

        @Override public PressureModule getModule() {
            return pressureModule;
        }

        @Override public PressureAble asBlock() {
            return PressureCrafter.this;
        }

        @Override public Building asBuilding() {
            return this;
        }

        @Override public boolean online() {
            return pressureConsume() > 0 || pressureProduce() > 0;
        }

        public float pressureConsume() {
            return pressureConsume;
        }

        public float pressureProduce() {
            return pressureProduce;
        }

        public boolean downPressure() {
            return downPressure;
        }

        public float downPercent() {
            return downPercent;
        }

        public float getPressure(){
            return this.pressure() / this.maxPressure();
        }

        public float pressureThread() {
            return (pressureProduce() * (effect / 100) * efficenty()) -
                    (downPressure() && status() == BlockStatus.active ? (pressureConsume() * downPercent()) : 0);
        }

        @Override
        public void craft() {
            if(pressureConsume() > 0 && efficenty() == 0) {
                return;
            }

            super.craft();
            this.pressureModule.pressure += this.pressureThread() * netLen() * volume;
        }

        @Override
        public BlockStatus status() {
            if(!online()) {
                return super.status();
            }

            BlockStatus SUPER = super.status();
            if(SUPER == BlockStatus.logicDisable || SUPER == BlockStatus.noOutput) {
                return SUPER;
            }

            return this.pressure() <= 0 ? BlockStatus.noInput : SUPER;
        }

        public float efficenty() {
            if(pressureConsume() <= 0) {
                return 1;
            }

            return Math.max(Math.min(this.pressure()/pressureConsume(), 2), 0);
        }

        public float handleProgress() {
            return this.efficenty() * this.getProgressIncrease(craftTime);
        }

        public float handleTotalProgress() {
            return this.efficenty() * this.delta();
        }

        @Override
        public void updateTile() {
            this.executeDefaultUpdateTileScript();

            progress += this.handleProgress();
            totalProgress += this.handleTotalProgress();

            if (Mathf.chanceDelta(updateEffectChance * this.efficenty())) {
                updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
            }

            if (canConsume()) {
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            if (progress >= 1f) {
                craft();
            }

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
        }

        @Override public boolean inNet(Building b, PressureAbleBuild p, boolean junction) {
            return !(b instanceof PressureCrafterBuild);
        }
    }
}