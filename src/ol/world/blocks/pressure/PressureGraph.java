package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.graphics.Layer;
import ol.graphics.OlPal;

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