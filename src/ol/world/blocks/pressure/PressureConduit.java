package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;

import static ol.graphics.OlPal.*;

public class PressureConduit extends Block {
    //max pressure that can store block. if pressure is bigger when boom
    public float maxPressure;

    //joints or no
    public boolean mapDraw = false;

    //when pressure need in the block to alert
    public float dangerPressure = -1;

    //if false when conduit sandbox block
    public boolean canExplode = true;

    //boom
    public Effect explodeEffect = Fx.none;
    public TextureRegion mapRegion;

    public void drawT(int x, int y, int rotation) {
        TextureRegion pressureIcon = Core.atlas.find("ol-pressure-icon");

        float dx = x * 8;
        float dy = y * 8;
        float ds = size * 8;

        if(rotation == 1 || rotation == 3) {
            Draw.rect(pressureIcon, dx, dy + ds);
            Draw.rect(pressureIcon, dx, dy - ds);
        }

        if(rotation == 0 || rotation == 2) {
            Draw.rect(pressureIcon, dx + ds, dy);
            Draw.rect(pressureIcon, dx - ds, dy);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        drawT(x, y, rotation);
    }

    public PressureConduit(String name) {
        super(name);

        rotate = true;
        update = true;
        solid = true;
        drawArrow = false;
    }

    @Override
    public void load() {
        super.load();

        if(mapDraw) {
            mapRegion = Core.atlas.find(name + "-map");
        }
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("pressure", (PressureConduitBuild b) -> {
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
    }

    @Override
    public void drawBase(Tile tile) {
        if(mapDraw) {
            //TODO joints
        } else {
            super.drawBase(tile);
        }
    }

    public class PressureConduitBuild extends Building implements PressureAble {
        public float pressure;
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

        public float sumx(FloatSeq arr) {
            return arr.sum();
        }

        @Override
        public void updateTile() {
            dt++;
            if(dt >= 60) {
                dt = 0;
            }

            int len = 1;
            FloatSeq sum_arr = new FloatSeq();
            Seq<PressureAble> prox = new Seq<>();
            for(Building b : net(this)) {
                PressureAble p = (PressureAble) b;
                if(!p.storageOnly()) {
                    sum_arr.add(p.pressureThread());
                }

                prox.add(p);
            }

            float sum = sumx(sum_arr);
            if(sum < 0) {
                sum = 0;
            }

            pressure = sum;
            prox.each(p -> p.pressure(pressure));
            if(pressure > maxPressure && canExplode) {
                explodeEffect.at(x, y);
                kill();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public PressureConduitBuild self() {
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
    }
}