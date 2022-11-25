package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import ol.graphics.OlPal;
import ol.world.blocks.RegionAble;

import static mindustry.Vars.world;

public class PressureGraph extends PressureConduit implements RegionAble {
    public TextureRegion arrowRegion, topRegion;
    public boolean fullRadius = false;
    public boolean noNetDestroy = true;

    public PressureGraph(String name) {
        super(name);

        mapDraw = false;
        rotateDraw = true;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if(!noNetDestroy) {
            return super.canPlaceOn(tile, team, rotation);
        }

        int tx = (int) (tile.drawx() / 8);
        int ty = (int) (tile.drawy() / 8);

        Building left = world.tile(tx - 1, ty).build;
        Building right = world.tile(tx + 1, ty).build;
        Building bottom = world.tile(tx, ty - 1).build;
        Building top = world.tile(tx, ty + 1).build;

        if(rotation == 0 || rotation == 2) {
            return left instanceof PressureAble || right instanceof PressureAble;
        }

        if(rotation == 1 || rotation == 3) {
            return top instanceof PressureAble || bottom instanceof PressureAble;
        }

        return false;
    }

    @Override
    public void load() {
        super.load();

        arrowRegion = loadRegion("-arrow");
        topRegion = loadRegion("-top");

        uiIcon = loadRegion("-preview", uiIcon);
    }

    @Override
    public String name() {
        return name;
    }

    public class PressureGraphBuild extends PressureConduitBuild {
        public float angle() {
            return !isDanger() ? ((pressure / dangerPressure) * 360) : Mathf.random(0, 360);
        }

        public boolean visibleArrow() {
            return true;
        }

        public boolean fullRad() {
            return isDanger() || fullRadius;
        }

        @Override
        public void draw() {
            super.draw();
            drawArrow();
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(noNetDestroy && net(this).isEmpty()) {
                kill();
            }
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();

            onUpdate(canExplode, maxPressure, explodeEffect);
        }

        public void drawArrow() {
            if(visibleArrow()) {
                Draw.draw(Layer.blockBuilding + 5, () -> {
                    float angle = angle();

                    Color color = OlPal.mixcol(Color.green, Color.red, angle / 360);
                    if(isDanger()) {
                        color = Color.white;
                    }

                    Draw.color(color);
                    Draw.rect(arrowRegion, x, y, fullRad() ? angle : (angle / 360) * -180);
                    Draw.rect(topRegion, x, y);
                    Draw.reset();
                });
            }
        }

        @Override
        public float drawrot() {
            if(!quickRotate || !rotate || alignX(rotation)) {
                return 0;
            }

            if(alignY(rotation)) {
                return -90;
            }

            return 0;
        }

        @Override
        public boolean inNet(Building b, PressureAble p, boolean junction) {
            Building self = self();
            int delta = 1;
            if(junction) {
                delta++;
            }

            int tx = self.tileX();
            int ty = self.tileY();

            Tile left = world.tile(tx - delta, ty);
            Tile right = world.tile(tx + delta, ty);

            if((left.build == b || right.build == b) && !alignX(rotation)) {
                return false;
            }

            Tile top = world.tile(tx, ty + delta);
            Tile bottom = world.tile(tx, ty - delta);

            if((top.build == b || bottom.build == b) && !alignY(rotation)) {
                return false;
            }

            return p.online() && (tier() == -1 || p.tier() == tier());
        }
    }
}