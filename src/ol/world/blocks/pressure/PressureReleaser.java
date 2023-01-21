package ol.world.blocks.pressure;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.annotations.Annotations;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class PressureReleaser extends PressurePipe {
    @Annotations.Load("@-open") public TextureRegion openRegion;
    public float releasePower = 2.5f;

    public PressureReleaser(String name) {
        super(name);

        this.configurable = true;
    }

    public class PressureReleaserBuild extends PressurePipeBuild {
        public boolean opened = false;

        @Override public void updateTile() {
            super.updateTile();

            if(this.opened) {
                this.pressureModule.pressure -= releasePower;
            }
        }

        @Override public void draw() {
            if(this.opened) {
                Draw.rect(openRegion, this.x, this.y, this.drawrot());
            } else {
                Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            }

            this.drawTeamTop();
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

            table.button(Icon.rotate, () -> opened = !opened).pad(6f);
            //TODO automated open button / aromatization of opened
        }
    }
}