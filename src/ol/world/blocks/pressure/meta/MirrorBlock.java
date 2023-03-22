package ol.world.blocks.pressure.meta;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;

import mindustry.entities.units.*;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import ol.graphics.*;
import ol.utils.*;
import ol.utils.RegionUtils.*;

import org.jetbrains.annotations.*;

public class MirrorBlock extends Block {
    private final BlockRegionFinder regionFinder = new BlockRegionFinder(this);
    private TextureRegion[] regions = new TextureRegion[4];

    public MirrorBlock(String name) {
        super(name);
        destructible = true;
        solid = false;
        rotate = true;
        quickRotate = true;
        rotateDraw = false;
    }

    // Update child buildings
    public static void updateChildren() {
        Events.run(EventType.Trigger.update, () -> {
            // Loop through each building in the map
            OlMapInvoker.eachBuild(building -> {
                // If the building is a mirror block, process it
                if(building instanceof MirrorBlockBuild cast) {
                    Building aa = building.nearby(building.rotation);
                    Building bb = cast.getAntiNearby();

                    // If the opposite building is not null and both buildings can consume pressure
                    if(bb != null && aa instanceof PressureAbleBuild && building.canConsume()) {
                        if (bb instanceof PressureAbleBuild && cast.isActive()) {
                            cast.updateNearby();
                            cast.updateAntiNearby();
                            cast.updateBoth(aa, bb);
                        }
                    }
                }
            });
        });
    }

    // Load textures
    @Override
    public void load() {
        super.load();
        regions = OlGraphics.getRegions(regionFinder.getRegion("-sprites"), 4, 1, 32*this.size);
    }

    // Draw the plan region
    @Override
    public void drawPlanRegion(@NotNull BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(regions[plan.rotation], plan.drawx(), plan.drawy());
    }

    // Inner class that represents the building instance of the mirror block
    public class MirrorBlockBuild extends Building {
        // Get the opposite building based on the current rotation
        public Building getAntiNearby() {
            return switch(rotation) {
                case 0 -> nearby(2);
                case 1 -> nearby(3);
                case 2 -> nearby(0);
                case 3 -> nearby(1);
                //unreached
                default -> null;
            };
        }

        // Determine if the block is active
        public boolean isActive() {
            return true;
        }

        // Update nearby buildings
        public void updateNearby() {}

        // Update the opposite building
        public void updateAntiNearby() {}

        // Update both nearby and opposite buildings
        public void updateBoth(Building aa, Building bb) {}

        // Determine the status of the block
        @Override
        public BlockStatus status() {
            return !canConsume() ? BlockStatus.noInput : BlockStatus.active;
        }

        // Determine if the block can consume pressure
        @Override
        public boolean canConsume() {
            for(Consume consume : consumers) {
                if(consume.efficiency(this) < 1) {
                    return false;
                }
            }
            return true;
        }

        // Draw the block
        @Override
        public void draw() {
            Draw.rect(regions[rotation], x, y);
            drawTeamTop();
        }
    }
}