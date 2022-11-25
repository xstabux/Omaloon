package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.Block;
import ol.world.blocks.RegionAble;

import static mindustry.Vars.world;
import static ol.graphics.OlPal.*;

public class PressureConduit extends Block implements PressureReplaceable, RegionAble {
    //max pressure that can store block. if pressure is bigger when boom
    public float maxPressure;
    public int tier = -1;

    //joints or no
    public boolean mapDraw = false;

    //when pressure need in the block to alert
    public float dangerPressure = -1;

    //if false when conduit sandbox block
    public boolean canExplode = true;

    //boom
    public Effect explodeEffect = Fx.none;

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
    public boolean canReplace(Block other) {
        return canBeReplaced(other);
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

        replaceable = true;
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
    public String name() {
        return name;
    }

    public class PressureConduitBuild extends Building implements PressureAble {
        public float pressure;
        public float dt = 0;

        @Override
        public int tier() {
            return tier;
        }

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
        public void updateTile() {
            dt++;
            if(dt >= 60) {
                dt = 0;
            }

            onUpdate(canExplode, maxPressure, explodeEffect);
        }

        @Override
        public void draw() {
            if(mapDraw && net(this).any()) {
                int tx = tileX();
                int ty = tileY();

                Building left = world.tile(tx - 1, ty).build;
                Building right = world.tile(tx + 1, ty).build;
                Building bottom = world.tile(tx, ty - 1).build;
                Building top = world.tile(tx, ty + 1).build;

                boolean bLeft = (left instanceof PressureAble && inNet(left, false)) || left instanceof PressureJunction.PressureJunctionBuild;
                boolean bRight = (right instanceof PressureAble && inNet(right, false)) || right instanceof PressureJunction.PressureJunctionBuild;
                boolean bTop = (top instanceof PressureAble && inNet(top, false)) || top instanceof PressureJunction.PressureJunctionBuild;
                boolean bBottom = (bottom instanceof PressureAble && inNet(bottom, false)) || bottom instanceof PressureJunction.PressureJunctionBuild;

                int l = bLeft ? 1 : 0;
                int r = bRight ? 1 : 0;
                int t = bTop ? 1 : 0;
                int b = bBottom ? 1 : 0;

                String sprite = "-" + l + "" + r + "" + t + "" + b;
                Draw.rect(loadRegion(sprite), this.x, this.y);
            } else {
                super.draw();
            }

            this.drawTeamTop();
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