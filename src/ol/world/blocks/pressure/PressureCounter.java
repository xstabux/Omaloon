package ol.world.blocks.pressure;

import arc.Events;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;

import mindustry.core.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.*;

import mma.type.*;
import mma.type.pixmap.*;

import ol.core.SettingsManager;
import ol.graphics.OlPal;
import ol.utils.Angles;

import ol.utils.RegionUtils;
import ol.world.blocks.pressure.meta.PressureAbleBuild;
import org.jetbrains.annotations.NotNull;

import static mindustry.Vars.*;

public class PressureCounter extends PressurePipe implements ImageGenerator {
    public boolean colorArrow = SettingsManager.clarrows.get();

    public TextureRegion arrowRegion;
    public float pointScale = 1.17f;

    public PressureCounter(String name) {
        super(name);
        mapDraw = true;
    }

    @Override
    public void load() {
        super.load();

        arrowRegion = RegionUtils.getRegion(this.name + "-arrow",
                RegionUtils.getRegion("pressure-counter-arrow")
        );
    }

    @Override public boolean inBuildPlanNet(@NotNull BuildPlan s, int x, int y, int ignored) {
        int ox = s.x - x;
        int oy = s.y - y;

        if(ox == 0 && oy == 0) {
            return true;
        }

        ox = ox < 0 ? -ox : ox;
        return (ox == 1) ? Angles.alignX(s.rotation) : Angles.alignY(s.rotation);
    }

    @Override public Pixmap generate(Pixmap icon, @NotNull PixmapProcessor processor){
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
            return this.COUNTER_IN_NET_CALL(b, p, junction);
        }
    }
}