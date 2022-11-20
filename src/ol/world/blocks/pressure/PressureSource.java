package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.struct.FloatSeq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ui.Styles;
import mindustry.world.Tile;

//maxPressure in this block is max value in the config (!!!)
public class PressureSource extends PressureGraph {
    public boolean voidable, drawArrow = true;
    public TextureRegion voidRegion;

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        rotation++;
        if(rotation > 3) {
            rotation -= 4;
        }

        drawT(x, y, rotation);
    }

    @Override
    public void drawBase(Tile tile) {
        if(tile.build != null){
            tile.build.draw();
        }else{
            Draw.rect(
                    variants == 0 ? region :
                            variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))],
                    tile.drawx(), tile.drawy());
        }
    }

    public PressureSource(String name) {
        super(name);

        canExplode = false;
    }

    @Override
    public void load() {
        super.load();

        if(voidable) {
            voidRegion = Core.atlas.find(name + "-void");
        }

        configurable = true;

        config(Float.class, (PressureSourceBuild b, Float i) -> {
            b.val = i;
        });

        config(Integer.class, (PressureSourceBuild b, Integer i) -> {
            b.val = i;
        });
    }

    public class PressureSourceBuild extends PressureGraphBuild {
        public float val = 0;

        @Override
        public void draw() {
            Draw.rect(voidMode() ? voidRegion : region, this.x, this.y, this.drawrot());
            this.drawTeamTop();
            drawArrow();
        }

        @Override
        public boolean isDanger() {
            return false;
        }

        public boolean voidMode() {
            return voidable && val == 0;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(val);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            val = read.f();
        }

        @Override
        public float sumx(FloatSeq arr, int len) {
            return val;
        }

        @Override
        public boolean visibleArrow() {
            return drawArrow && !voidMode();
        }

        @Override
        public void buildConfiguration(Table table) {
            table.pane(t -> {
                t.setBackground(Styles.black5);
                t.add("pressure").center().row();
                t.slider(0, maxPressure, 1, pressure, this::configure).growX();
            }).size(200f, 75f);
        }
    }
}