package ol.world.blocks.pressure;

import arc.Core;
import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.Renderer;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;
import ol.graphics.OlDraw;
import ol.world.blocks.Ranged;
import ol.world.blocks.defense.OlWall;

import static mindustry.Vars.tilesize;
import static ol.graphics.OlPal.*;
import static ol.graphics.OlPal.OLPressure;

public class ConduitBridge extends OlWall implements PressureReplaceable {
    //max pressure that can store block. if pressure is bigger when boom
    public float maxPressure;

    //when pressure need in the block to alert
    public float dangerPressure = -1;

    //if false when conduit sandbox block
    public boolean canExplode = true;

    //boom
    public Effect explodeEffect = Fx.none;

    public TextureRegion bridge, bridgeEnd, bridgeEnd2;
    public float range = 80;

    public static float pow(float n) {
        return n*n;
    }

    public static float len(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt(pow(x2 - x1) + pow(y2 - y1));
    }

    public static boolean collision(float x1, float y1, float x2, float y2, float radius) {
        return len(x1, x2, y1, y2) < radius;
    }

    public void drawBridge(Building self, Building other) {
        float sx = self.x, sy = self.y;
        float ox = other.x, oy = other.y;

        int segments = length(sx, sy, ox, oy) + 1;

        float sa = self.angleTo(other);
        float oa = other.angleTo(self);

        OlDraw.l(Layer.power - 5);
        Lines.stroke(4);

        Draw.rect(bridgeEnd, sx, sy, sa);
        Draw.rect(bridgeEnd2, ox, oy, oa);

        for(int i = 1; i < segments; i++) {
            float s_x = Mathf.lerp(sx, ox, (float) i / segments);
            float s_y = Mathf.lerp(sy, oy, (float) i / segments);

            Draw.rect(bridge, s_x, s_y, sa);
        }

        Draw.reset();
    }

    @Override
    public void load() {
        super.load();

        bridge = Core.atlas.find(name + "-bridge");
        bridgeEnd = Core.atlas.find(name + "-end");
        bridgeEnd2 = Core.atlas.find(name + "-end2");
    }

    public ConduitBridge(String name) {
        super(name);
        solid = true;
        configurable = true;

        config(Integer.class, (ConduitBridgeBuild c, Integer link) -> {
            if(c.link == link) {
                c.unlink();
            }

            c.link = link;
        });
    }

    @Override
    public boolean canReplace(Block other) {
        return canBeReplaced(other);
    }

    public boolean validLink(Building other, float x, float y) {
        if(other == null) {
            return false;
        }

        return collision(x, y, other.x, other.y, range);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.circles(x * 8, y * 8, range, Pal.place);
    }

    public int length(float x1, float y1, float x2, float y2) {
        return (int) (len(x1, x2, y1, y2) / 8);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("pressure", (ConduitBridgeBuild b) -> {
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

    public class ConduitBridgeBuild extends OlWall.olWallBuild implements PressureAble, Ranged {
        public int link = -1;

        @Override
        @SuppressWarnings("unchecked")
        public ConduitBridgeBuild self() {
            return this;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            drawRange();
        }

        @Override
        public void draw() {
            super.draw();

            if(linked()) {
                drawBridge(this, link());
            }
        }

        @Override
        public float pressure() {
            return pressure;
        }

        @Override
        public void pressure(float pressure) {
            this.pressure = pressure;
        }

        public float pressure;
        public float dt = 0;

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(pressure);
            write.i(link);
        }

        @Override
        public boolean inNet(Building b, PressureAble p, boolean j) {
            return true;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            pressure = read.f();
            configure(read.i());
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

                net(this, PressureJunction.PressureJunctionBuild::netKill)
                        .filter(b -> ((PressureAble) b).online()).each(Building::kill);


                kill();
            }

            if(link != -1 && link() == null) {
                unlink();
            }
        }

        @Override
        public void buildConfiguration(Table table) {
        }

        public Building link() {
            if(link == -1) {
                return null;
            }

            return Vars.world.tile(link).build;
        }

        public boolean linked(Building b) {
            return link() == b;
        }

        public boolean linked() {
            return link != -1 && link() != null;
        }

        public void unlink() {
            link = -1;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if(this == other) {
                this.deselect();
                return false;
            }

            if(other instanceof ConduitBridgeBuild b && validLink(other, x, y)) {
                configure(b.pos());
                if(b.link() == this) {
                    b.unlink();
                }

                return true;
            }

            return super.onConfigureBuildTapped(other);
        }

        @Override
        public void drawConfigure() {
            float s = size * 8 / 2f + 2f + (jumpDelta() / 30);

            validLinks(b -> true).each(b -> {
                Drawf.select(b.x, b.y, s, b.linked(this) || linked(this) ? Pal.place : Pal.accent);
            });

            Drawf.select(x, y, s, Pal.place);
        }

        public Seq<ConduitBridgeBuild> validLinks(Boolf<ConduitBridgeBuild> boolf) {
            Seq<ConduitBridgeBuild> builds = new Seq<>();
            Vars.world.tiles.eachTile(t -> {
                if(validLink(t.build, x, y) && t.build instanceof ConduitBridgeBuild b && boolf.get(b)) {
                    builds.add(b);
                }
            });

            return builds;
        }

        @Override
        public Seq<Building> net(Building source, Cons<PressureJunction.PressureJunctionBuild> cons) {
            Seq<Building> buildings = PressureAble.super.net(source, cons);
            for(ConduitBridgeBuild b : validLinks(b -> b.linked(this) || linked(b))) {
                if(b == this || b == source) {
                    continue;
                }

                buildings.add(b);
                buildings.add(b.net(this));
            }

            return buildings;
        }

        @Override
        public float range() {
            return range;
        }
    }
}