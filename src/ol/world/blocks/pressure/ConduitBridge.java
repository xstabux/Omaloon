package ol.world.blocks.pressure;

import arc.Core;
import arc.func.Boolf;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.scene.ui.layout.Table;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.Renderer;
import mindustry.entities.Effect;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerNode;
import ol.graphics.OlDraw;
import ol.world.blocks.Ranged;
import ol.world.blocks.defense.OlWall;

import static arc.graphics.g2d.Draw.scl;
import static arc.graphics.g2d.Draw.xscl;
import static mindustry.Vars.world;
import static ol.graphics.OlPal.*;
import static ol.graphics.OlPal.OLPressure;

public class ConduitBridge extends OlWall implements PressureReplaceable {
    private static BuildPlan otherReq;
    public int tier = -1;

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

    public void drawBridge(Position self, Position other) {
        float sx = self.getX(), sy = self.getY();
        float ox = other.getX(), oy = other.getY();

        float sa = self.angleTo(other);
        float oa = other.angleTo(self);

        boolean line = sx == ox || sy == oy;
        int segments = length(sx, sy, ox, oy) + 1;

        if(line) {
            if(sy == oy) {
                Position a = sx < ox ? other : self;
                Position b = sx < ox ? self : other;

                segments = (int) (a.getX()/8 - b.getX()/8);
            }

            if(sx == ox) {
                Position a = sy < oy ? other : self;
                Position b = sy < oy ? self : other;

                segments = (int) (a.getY()/8 - b.getY()/8);
            }
        }

        float sl = 0;
        if(!line) {
            sl = len(sx, ox, sy, oy) / segments;
        }

        OlDraw.l(Layer.power - 5);

        Draw.alpha(Renderer.bridgeOpacity);
        Lines.stroke(4);

        boolean reverse = sx > ox;

        if(line) {
            reverse |= sy < oy;
        }

        float r = sa + (reverse ? 180 : 0);

        TextureRegion end = reverse ? bridgeEnd2 : bridgeEnd;
        TextureRegion str = reverse ? bridgeEnd : bridgeEnd2;

        Draw.rect(end, sx, sy, sa);
        Draw.rect(str, ox, oy, oa);

        for(int i = 1; i < segments; i++) {
            float s_x = Mathf.lerp(sx, ox, (float) i / segments);
            float s_y = Mathf.lerp(sy, oy, (float) i / segments);

            if(line) {
                Draw.rect(bridge, s_x, s_y, r);
            } else {
                Draw.rect(bridge, s_x, s_y, sl, bridge.height * scl * xscl, r);
            }
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
        update = true;
        underBullets = true;
        noUpdateDisabled = true;
        copyConfig = false;
        allowConfigInventory = false;
        priority = TargetPriority.transport;
        swapDiagonalPlacement = true;

        config(Integer.class, (ConduitBridgeBuild c, Integer link) -> {
            if(c.link == link) {
                c.unlink();
            } else {
                c.link = link;
            }
        });

        config(Point2.class, (ConduitBridgeBuild tile, Point2 i) -> {
            tile.configure(Point2.pack(i.x + tile.tileX(), i.y + tile.tileY()));
        });
    }

    @Override
    public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list){
        otherReq = null;
        list.each(other -> {
            if(other.block == this && plan != other && plan.config instanceof Point2 p && p.equals(other.x - plan.x, other.y - plan.y)) {
                otherReq = other;
            }
        });

        if(otherReq != null){
            drawBridge(plan, otherReq);
        }
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        for(int i = 0; i < plans.size - 1; i++){
            var cur = plans.get(i);
            var next = plans.get(i + 1);

            if(validLink(cur, next.x, next.y)){
                Point2 config = new Point2(next.x - cur.x, next.y - cur.y);
                cur.config = config;

                if(world.build(cur.x, cur.y) instanceof ConduitBridgeBuild bridgec) {
                    if(validLink(next, cur.x, cur.y)) {
                        bridgec.configure(config);
                    }
                }
            }
        }
    }

    @Override
    public boolean canReplace(Block other) {
        return canBeReplaced(other);
    }

    public boolean validLink(BuildPlan other, float x, float y) {
        if(other == null) {
            return false;
        }

        return collision(x, y, other.x, other.y, range);
    }

    public boolean validLink(Building other, int x, int y) {
        if(other == null) {
            return false;
        }

        ConduitBridgeBuild b2 = (ConduitBridgeBuild) world.build(x, y);
        return collision(b2.x, b2.y, other.x, other.y, range) && other instanceof ConduitBridgeBuild b &&
                (b2.tier() == -1 || b.tier() == -1 || b.tier() == b2.tier());
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

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;

        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, range - 8),
                Tmp.r1.setSize(size * 8).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    public class ConduitBridgeBuild extends OlWall.olWallBuild implements PressureAble, Ranged {
        public int link = -1;

        @Override
        @SuppressWarnings("unchecked")
        public ConduitBridgeBuild self() {
            return this;
        }

        @Override
        public int tier() {
            return tier;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            validLinks(b -> b.linked(this) || linked(b) || b == this).each(b -> {
                Drawf.line(Color.sky, b.x, b.y, x, y);
                Drawf.square(b.x, b.y, 4, 45, Color.sky);
            });

            drawRange();
        }

        @Override
        public Point2 config() {
            return Point2.unpack(link).sub(tile.x, tile.y);
        }

        @Override
        public byte version(){
            return 1;
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

        @Override
        public boolean WTR() {
            return true;
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
        public void buildConfiguration(Table table) {
        }

        public Building link() {
            if(link == -1) {
                return null;
            }

            Tile tile = Vars.world.tile(link);
            return tile == null ? null : tile.build;
        }

        public boolean linked(Building b) {
            return link() == b;
        }

        public boolean linked() {
            return link != -1 && link() instanceof ConduitBridgeBuild;
        }

        public void unlink() {
            link = -1;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if(other instanceof ConduitBridgeBuild b && validLink(other, tileX(), tileY()) && other != this) {
                configure(b.pos());
                return false;
            }

            return super.onConfigureBuildTapped(other);
        }

        @Override
        public void drawConfigure() {
            float s = size * 8 / 2f + 2f + (jumpDelta() / 30);

            validLinks(b -> true).each(b -> {
                Drawf.select(b.x, b.y, s, b.linked(this) || linked(b) ? Pal.heal : Pal.accent);
            });

            Drawf.select(x, y, s, Pal.place);
        }

        public Seq<ConduitBridgeBuild> validLinks(Boolf<ConduitBridgeBuild> boolf) {
            Seq<ConduitBridgeBuild> builds = new Seq<>();

            int range = (int) (this.range()*2 / 8);
            for(int x = tileX() - range; x < tileX() + range; x++) {
                for(int y = tileY() - range; y < tileY() + range; y++) {
                    Tile t = world.tile(x, y);

                    if(t.build instanceof ConduitBridgeBuild b && boolf.get(b)) {
                        if(validLink(t.build, tileX(), tileY())) {
                            builds.add(b);
                        }
                    }
                }
            }

            return builds;
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            onUpdate(false, maxPressure, explodeEffect);
        }

        @Override
        public Seq<Building> net(Building building, Cons<PressureJunction.PressureJunctionBuild> cons, Seq<Building> buildings) {
            for(ConduitBridgeBuild b : validLinks(b -> b.linked(this) || linked(b))) {
                if(b == this || b == building || buildings.contains(b)) {
                    continue;
                }

                buildings.add(b);
                b.net(b, cons, buildings);
            }

            return PressureAble.super.net(building, cons, buildings);
        }

        @Override
        public float range() {
            return range;
        }
    }
}