package ol.world.blocks.pressure;

import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.*;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import ol.world.blocks.crafting.OlCrafter;

import static ol.graphics.OlPal.*;

public class PressureCrafter extends OlCrafter {
    //how many pressure crafter consumes
    public float pressureConsume = 0;

    //how many pressure crafter
    public float pressureProduce = 0;

    public float maxPressure, dangerPressure;
    public boolean canExplode = true;

    //boom
    public Effect explodeEffect = Fx.none;

    public PressureCrafter(String name) {
        super(name);
    }

    public class PressureCrafterBuild extends OlCrafter.olCrafterBuild implements PressureAble {
        public float pressure;
        public float effect;
        public float dt = 0;

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(pressure);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            pressure = read.f();
        }

        public boolean isDanger() {
            if(dangerPressure == -1) {
                return false;
            }

            return pressure > dangerPressure && canExplode;
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
        public void pressure(float pressure) {
            this.pressure = pressure;
        }

        @Override
        public boolean online() {
            return pressureConsume > 0 || pressureProduce > 0;
        }

        @Override
        public boolean storageOnly() {
            return !(pressureProduce > 0);
        }

        @Override
        public float pressureThread() {
            return pressureProduce * (effect / 100);
        }

        @Override
        public void craft() {
            //HA
            if(pressure < pressureConsume && pressureConsume > 0) {
                return;
            }

            super.craft();
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(net(this).filter(b -> b != this).isEmpty()) {
                pressure = pressureThread();
            }

            if(canConsume() && effect < 100) {
                effect++;
                if(effect > 100) {
                    effect = 100;
                }
            } else {
                if(!canConsume() && effect > 0) {
                    effect--;
                    if(effect < 0) {
                        effect = 0;
                    }
                }
            }

            if(pressure > maxPressure && canExplode) {
                explodeEffect.at(x, y);

                kill();
                net(this).filter(b -> ((PressureAble) b).online()).each(Building::kill);
            }

            dt++;
            if(dt >= 60) {
                dt = 0;
            }
        }
    }
}