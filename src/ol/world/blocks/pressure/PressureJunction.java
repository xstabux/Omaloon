package ol.world.blocks.pressure;

import arc.*;
import arc.graphics.g2d.*;

import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import ol.world.blocks.GraphBlock;

public class PressureJunction extends GraphBlock {
    public PressureJunction(String name) {
        super(name);
    }

    @Override
    public boolean canReplace(Block other) {
        return other instanceof PressurePipe;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        TextureRegion pressureIcon = Core.atlas.find("ol-arrow");

        float dx = x * 8;
        float dy = y * 8;
        float ds = size * 8;

        Draw.rect(pressureIcon, dx, dy + ds, -90);
        Draw.rect(pressureIcon, dx, dy - ds, 90);

        Draw.color(Pal.lightishGray);
        Draw.rect(pressureIcon, dx + ds, dy, 180);
        Draw.rect(pressureIcon, dx - ds, dy, 0);

        Draw.reset();
    }

    @SuppressWarnings("all") //IDEA
    public class PressureJunctionBuild extends GraphBlockBuild {
        public Building getInvert(Building other){
            return nearby((relativeTo(other) + 2) % 4);
        }
    }
}