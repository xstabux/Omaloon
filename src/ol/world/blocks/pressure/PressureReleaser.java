package ol.world.blocks.pressure;

import arc.Core;
import arc.func.Prov;
import arc.graphics.g2d.Draw;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;

import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

import ol.utils.Angles;
import ol.world.blocks.pressure.meta.PressureAbleBuild;
import org.jetbrains.annotations.NotNull;

public class PressureReleaser extends PressurePipe {
    public float releasePower = 2.5f;

    public PressureReleaser(String name) {
        super(name);

        this.mapDraw = true;
        this.configurable = true;
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

    public class PressureReleaserBuild extends PressurePipeBuild {
        public boolean opened = false;
        public boolean auto = false;
        public int needAngle = 0;
        public int angle = 0;

        @Override public void updateTile() {
            super.updateTile();

            if(this.opened || (this.auto && this.isDanger())) {
                if(this.pressure() > 0) {
                    this.pressureModule.pressure -= releasePower;
                }

                this.needAngle = 90;
            } else {
                needAngle = 0;
            }

            if(angle < needAngle) {
                angle++;
            }

            if(angle > needAngle) {
                angle--;
            }
        }

        @Override public void draw() {
            super.draw();

            Draw.rect(Core.atlas.find("ol-ajgsio"), x, y, angle + this.drawrot());
        }

        @Override public void write(Writes write) {
            super.write(write);
            write.bool(this.opened);
            write.bool(this.auto);
        }

        @Override public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.opened = read.bool();
            this.auto = read.bool();
        }

        @Override public boolean inNet(Building b, PressureAbleBuild p, boolean junction) {
            return this.COUNTER_IN_NET_CALL(b, p, junction);
        }

        @Override public void buildConfiguration(@NotNull Table table) {
            table.setBackground(Styles.black5);

            table.button(Icon.rotate, () -> {
                opened = !opened;
            }).pad(6f).size(24f);

            Prov<TextureRegionDrawable> icon = () -> {
                return auto ? Icon.cancel : Icon.add;
            };

            table.button(icon.get(), () -> {
                this.auto = !this.auto;
            }).update(btn -> {
                btn.clearChildren();
                btn.image(icon.get());
            }).pad(6f).size(24f);
        }
    }
}