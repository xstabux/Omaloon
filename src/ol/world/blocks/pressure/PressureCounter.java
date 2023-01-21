package ol.world.blocks.pressure;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;

import mindustry.annotations.Annotations.*;
import mindustry.core.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import mma.type.*;
import mma.type.pixmap.*;

import ol.graphics.OlPal;
import ol.utils.Angles;

import ol.world.blocks.pressure.meta.PressureAbleBuild;

import static mindustry.Vars.*;

public class PressureCounter extends PressurePipe implements ImageGenerator {
    @Load("@-arrow") public TextureRegion arrowRegion;
    public boolean colorArrow = false; //TODO setting to set it`s default
    public float pointScale = 1.17f;

    public PressureCounter(String name) {
        super(name);
        mapDraw = true;
    }

    @Override public boolean inBuildPlanNet(BuildPlan s, int x, int y, int ignored) {
        int ox = s.x - x;
        int oy = s.y - y;

        if(ox == 0 && oy == 0) {
            return true;
        }

        ox = ox < 0 ? -ox : ox;
        return (ox == 1) ? Angles.alignX(s.rotation) : Angles.alignY(s.rotation);
    }

    @Override public Pixmap generate(Pixmap icon, PixmapProcessor processor){
        PixmapProcessor.drawCenter(icon,processor.get(arrowRegion));
        return ImageGenerator.super.generate(icon, processor);
    }

    public class PressureCounterBuild extends PressurePipeBuild {
        public float angle() {
            if(!isDanger()) {
                return ((this.pressure() / dangerPressure) * 360);
            }

            return this.totalProgress() * this.pressure();
        }

        public boolean visibleArrow() {
            return true;
        }

        public void drawArrow() {
            if(visibleArrow()) {
                Draw.draw(Layer.blockBuilding + 5, () -> {
                    float angle = angle();

                    if(this.isDanger() || !colorArrow) {
                        Draw.color(Color.white);
                    } else {
                        Draw.color(OlPal.mixcol(Color.green, Color.red, this.pressure() / this.maxPressure()));
                    }

                    if(colorArrow) {
                        Fill.circle(x, y, pointScale);
                    }

                    if(!state.is(GameState.State.paused)) {
                        Draw.rect(arrowRegion, x, y, ((angle / 360) * -180) + Mathf.random(this.pressure(), angle) / 10);
                    } else {
                        Draw.rect(arrowRegion, x, y, ((angle / 360) * -180));
                    }

                    Draw.reset();
                });
            }
        }

        @Override public boolean avalibleX() {
            return Angles.alignX(this.rotation);
        }

        @Override public boolean avalibleY() {
            return Angles.alignY(this.rotation);
        }

        @Override public void draw() {
            super.draw();
            drawArrow();
        }

        @Override public float drawrot() {
            if(!quickRotate || !rotate || Angles.alignX(rotation)) {
                return 0;
            }

            if(Angles.alignY(rotation)) {
                return -90;
            }

            return 0;
        }

        @Override public boolean inNet(Building b, PressureAbleBuild p, boolean junction) {
            Building self = self();

            int delta = 1;
            if(junction) {
                delta++;
            }

            int tx = self.tileX();
            int ty = self.tileY();

            Tile left = world.tile(tx - delta, ty);
            Tile right = world.tile(tx + delta, ty);

            if((left.build == b || right.build == b) && !Angles.alignX(rotation)) {
                return false;
            }

            Tile top = world.tile(tx, ty + delta);
            Tile bottom = world.tile(tx, ty - delta);

            if((top.build == b || bottom.build == b) && !Angles.alignY(rotation)) {
                return false;
            }

            return p.online() && (p.tier() == -1 || p.tier() == tier());
        }
    }
}