package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.annotations.Annotations;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class PressureReleaser extends PressurePipe {
    public float releasePower = 2.5f;

    public PressureReleaser(String name) {
        super(name);

        this.mapDraw = true;
        this.configurable = true;
    }

    public class PressureReleaserBuild extends PressurePipeBuild {
        public boolean opened = false;
        public int needAngle = 0;
        public int angle = 0;

        @Override public void updateTile() {
            super.updateTile();

            if(this.opened) {
                this.pressureModule.pressure -= releasePower;
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

            Draw.rect(Core.atlas.find("ajgsio"), x, y, angle + this.drawrot());
        }

        @Override public void write(Writes write) {
            super.write(write);
            write.bool(this.opened);
        }

        @Override public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.opened = read.bool();
        }

        @Override public void buildConfiguration(Table table) {
            table.setBackground(Styles.black5);

            table.button(Icon.rotate, () -> {
                opened = !opened;

                if(opened) {
                    needAngle = 90;
                } else {
                    needAngle = 0;
                }
            }).pad(6f);

            //TODO automated open button / aromatization of opened
        }
    }
}