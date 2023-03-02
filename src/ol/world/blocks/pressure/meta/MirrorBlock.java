package ol.world.blocks.pressure.meta;

import mindustry.gen.*;

import ol.world.blocks.GraphBlock;
import ol.utils.*;

public class MirrorBlock extends GraphBlock {
    public MirrorBlock(String name) {
        super(name);

        solid = false;
        rotate = true;
        quickRotate = true;
        rotateDraw = false;
        drawStyle = DrawStyle.ARTIFICIAL_ROTATION;
        joinsSpritesPrefix = "-sprites";

        normalCanConsumeResultReturn = false;
        normalBlockStatusResultReturn = false;
    }

    // Update child buildings
    public static void updateChildren() {
        // Loop through each building in the map
        OlMapInvoker.eachBuild(building -> {
            // If the building is a mirror block, process it
            if(building instanceof MirrorBlockBuild build) {
                Building aa = build.nearby();
                Building bb = build.antiNearby();

                // If the opposite building is not null and both buildings can consume pressure
                if(bb != null && aa instanceof PressureAbleBuild && building.canConsume()) {
                    if (bb instanceof PressureAbleBuild && build.isActive()) {
                        build.updateBoth(aa, bb);
                    }
                }
            }
        });
    }

    // Inner class that represents the building instance of the mirror block
    public class MirrorBlockBuild extends GraphBlockBuild {
        // Update both nearby and opposite buildings
        public void updateBoth(Building aa, Building bb) {}
    }
}