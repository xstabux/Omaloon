package ol.type;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class NothingLiquid extends Liquid {
    public NothingLiquid() {
        super("nothing", Color.black);

        this.description = "IT`S NOT LIQUID. IT`S NOT LIQUID. IT`S NOT LIQUID.";
        this.localizedName = "";
        this.hidden = true;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean unlockedNowHost() {
        return false;
    }

    @Override
    public boolean unlocked() {
        return false;
    }

    @Override
    public boolean unlockedNow() {
        return false;
    }
}