package ol.world.blocks.pressure.meta;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;

import mindustry.Vars;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import ol.graphics.*;
import ol.utils.*;
import ol.utils.RegionUtils.*;

import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

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
        drawArrow = false;
    }
    @Override public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        var pressureIcon = Core.atlas.find("ol-arrow");

        float dx = x * 8;
        float dy = y * 8;
        float ds = size * 8;

        if(size == 2) {
            if(rotation == 0) {
                Draw.rect(pressureIcon, dx+ds-3, dy, 0);
                Draw.rect(pressureIcon, dx-ds/2, dy, 0);
                Draw.rect(pressureIcon, dx+ds-3, dy+8, 0);
                Draw.rect(pressureIcon, dx-ds/2, dy+8, 0);
            }

            if(rotation == 1) {
                Draw.rect(pressureIcon, dx, dy+ds-3, 90);
                Draw.rect(pressureIcon, dx, dy-ds/2, 90);
                Draw.rect(pressureIcon, dx+8, dy+ds-3, 90);
                Draw.rect(pressureIcon, dx+8, dy-ds/2, 90);
            }

            if(rotation == 2) {
                Draw.rect(pressureIcon, dx+ds, dy, 180);
                Draw.rect(pressureIcon, dx-ds/2+3, dy, 180);
                Draw.rect(pressureIcon, dx+ds, dy+8, 180);
                Draw.rect(pressureIcon, dx-ds/2+3, dy+8, 180);
            }

            if(rotation == 3) {
                Draw.rect(pressureIcon, dx, dy+ds, -90);
                Draw.rect(pressureIcon, dx, dy-ds/2+3, -90);
                Draw.rect(pressureIcon, dx+8, dy+ds, -90);
                Draw.rect(pressureIcon, dx+8, dy-ds/2+3, -90);
            }
        } else {
            if(rotation == 1 || rotation == 3){
                Draw.rect(pressureIcon, dx, dy + ds, -90);
                Draw.rect(pressureIcon, dx, dy - ds, 90);
            }

            if(rotation == 0 || rotation == 2){
                Draw.rect(pressureIcon, dx + ds, dy, 180);
                Draw.rect(pressureIcon, dx - ds, dy, 0);
            }
        }
    }
    // Update child buildings
    public static void updateChildren() {
        Events.run(EventType.Trigger.update, () -> {
            // Loop through each building in the map
            OlMapInvoker.eachBuild(building -> {
                // If the building is a mirror block, process it
                if(building instanceof MirrorBlockBuild cast) {
                    Building[] aa2 = cast.nearbyA(cast.rotation);
                    Building[] bb2 = cast.getAntiNearby();

                    cast.updateNearby();
                    cast.updateAntiNearby();

                    for(Building aa : aa2) {
                        for(Building bb : bb2) {
                            if(bb != null && aa instanceof PressureAbleBuild && building.canConsume()) {
                                if (bb instanceof PressureAbleBuild && cast.isActive()) {
                                    cast.updateBoth(aa, bb);
                                }
                            }
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
        public int notificator = new Random().nextInt(Integer.MAX_VALUE);

        // Get the opposite building based on the current rotation
        public Building[] getAntiNearby() {
            return switch(rotation) {
                case 0 -> nearbyA(2);
                case 1 -> nearbyA(3);
                case 2 -> nearbyA(0);
                case 3 -> nearbyA(1);
                //unreached
                default -> null;
            };
        }
        public Building tmp(int x, int y) {
            var b = tileAt(tileX() + x, tileY() + y);
            if(b != null && b.block == MirrorBlock.this) {
                int x2 = x == 0 ? 0 : x + 1;
                int y2 = y == 0 ? 0 : y + 1;
                return ((MirrorBlockBuild) b).tmp(x2, y2);
            } else {
                return b;
            }
        }
        public Building tileAt(int x, int y) {
            return Vars.world.build(x, y);
        }
        public Tile[] _func_539530(int rotation) {
            int tx = tileX();
            int ty = tileY();
            Function<Point2, Tile> cons = (p) -> {
                return Vars.world.tile(p.x, p.y);
            };
            Point2 center2 = new Point2(tx, ty);
            Point2 a = center2.cpy();
            Point2 b = center2.cpy();
            var ex = new IllegalStateException("Unexpected value: " + rotation);
            return new Tile[] {
                    cons.apply(switch(rotation) {
                        case 0 -> a.add(2, 0);
                        case 1 -> a.add(0, 2);
                        case 2 -> a.add(-1, 0);
                        case 3 -> a.add(0, -1);
                        default -> throw ex;
                    }),
                    cons.apply(switch(rotation) {
                        case 0 -> b.add(2, 1);
                        case 1 -> b.add(1, 2);
                        case 2 -> b.add(-1, 1);
                        case 3 -> b.add(1, -1);
                        default -> throw ex;
                    })
            };
        }
        public Building[] nearbyA(int rotation) {
            if(size <= 1) {
                return new Building[] {
                        super.nearby(rotation)
                };
            }
            if(size == 2) {
                Tile[] pack = _func_539530(rotation);
                return new Building[] {
                        pack[0].build,
                        pack[1].build
                };
            }
            throw new ArcRuntimeException("unreachable");
            //var b = new Building[size];
            //for(int i = 0; i < size; i++) {
            //    b[i] = tmp(p.x, p.y);
            //}
            //return b;
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