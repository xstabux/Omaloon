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

import static mindustry.Vars.world;

public class PressureGraph extends PressureConduit {
    public TextureRegion arrowRegion;
    public boolean fullRadius = false;
    public boolean noNetDestroy = true;

    public PressureGraph(String name) {
        super(name);

        mapDraw = false;
        rotateDraw = false;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
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

        arrowRegion = Core.atlas.find(name + "-arrow");
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
                    Draw.reset();
                });
            }
        }
    }
}