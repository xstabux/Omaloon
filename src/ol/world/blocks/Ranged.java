package ol.world.blocks;

import arc.graphics.Color;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;

public interface Ranged extends Teamc {
    float range();

    default Color color() {
        return team().color;
    }

    default void drawRange(float x, float y) {
        Drawf.circles(x, y, range(), color());
    }

    default void drawRange() {
        drawRange(getX(), getY());
    }
}