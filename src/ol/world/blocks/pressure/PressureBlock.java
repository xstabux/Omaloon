package ol.world.blocks.pressure;

import arc.util.io.*;

import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;

import ol.utils.pressure.*;
import ol.world.blocks.pressure.meta.PressureAble;
import ol.world.blocks.pressure.meta.PressureAbleBuild;
import ol.world.blocks.pressure.meta.PressureModule;
import ol.world.meta.*;

import static arc.Core.*;
import static ol.graphics.OlPal.*;

public class PressureBlock extends Block implements PressureAble {
    public float dangerPressure = -1;
    public float maxPressure = 100;
    public boolean canExplode = true;
    public int tier = PressureAPI.NULL_TIER;

    public PressureBlock(String name) {
        super(name);

        this.update = true;
        this.destructible = true;
    }

    @Override public void setBars() {
        super.setBars();

        if(canExplode) {
            this.addBar("pressure", (PressureBlockBuild build) -> new Bar(
                    () -> bundle.get("bar.pressure") + " " + (int) build.pressure(),
                    () -> oLPressure,
                    build::getPressure
            ));
        }
    }

    @Override public void setStats() {
        super.setStats();

        if(tier > 0){
            stats.add(OlStat.tier, tier);
        }

        if(canExplode) {
            stats.add(OlStat.maxPressure, maxPressure, OlStatUnit.pressure);
        }
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

    public class PressureBlockBuild extends Building implements PressureAbleBuild {
        public PressureModule pressureModule = new PressureModule();

        public boolean isDanger() {
            if(dangerPressure == -1) {
                return false;
            }

            return this.pressure() >= dangerPressure;
        }

        public float getPressure(){
            return (this.pressure() / this.maxPressure());
        }

        @Override public void write(Writes write) {
            super.write(write);
            pressureModule.write(write);
        }

        @Override public void read(Reads read, byte revision) {
            super.read(read, revision);
            pressureModule.read(read);
        }

        @Override public void updateTile() {
            this.executeDefaultUpdateTileScript();
        }

        @Override public PressureModule getModule() {
            return this.pressureModule;
        }

        @Override public PressureAble asBlock() {
            return PressureBlock.this;
        }

        @Override public Building asBuilding() {
            return this;
        }
    }
}