package ol.world.blocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.BlockStatus;
import ol.graphics.OlGraphics;
import ol.utils.OlPlans;
import ol.utils.RegionUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static mindustry.Vars.*;

public class GraphBlock extends Block {
    public final RegionUtils.BlockRegionFinder regionFinder = new RegionUtils.BlockRegionFinder(this);
    public TextureRegion[] sprites = new TextureRegion[0];
    public boolean normalBlockStatusResultReturn = true;
    public boolean normalCanConsumeResultReturn = true;
    public DrawStyle drawStyle = DrawStyle.DEFAULT;
    public String joinsSpritesPrefix = "-sheet";
    public boolean enableSpriteSplitting = true;
    public boolean enableJoinsLoading = true;
    public boolean enableRange = false;
    public float rangeRadius = 0F;

    @Contract(pure = true)
    public static byte of(boolean top, boolean bottom, boolean left, boolean right) {
        byte
                b = (byte) (bottom ? 0b0001 : 0),
                t = (byte) (top    ? 0b0010 : 0),
                r = (byte) (right  ? 0b0100 : 0),
                l = (byte) (left   ? 0b1000 : 0);

        return (byte) (l | r | t | b);
    }

    public GraphBlock(String name) {
        super(name);

        this.destructible = true;
        this.solid = true;
    }

    public TextureRegion loadRegion(String prefix) {
        return this.regionFinder.getRegion(prefix);
    }

    public boolean hasRegion(String prefix) {
        return Core.atlas.has(this.name + prefix);
    }

    public boolean acceptJointPlan(BuildPlan plan, BuildPlan other) {
        return plan != null && plan.block == this;
    }

    @Override
    public void drawPlanRegion(@NotNull BuildPlan plan, Eachable<BuildPlan> list) {
        switch(drawStyle) {
            case DEFAULT -> super.drawPlanRegion(plan, list);
            case ARTIFICIAL_ROTATION -> Draw.rect(sprites[plan.rotation], plan.drawx(), plan.drawy());
            case ENABLE_JOINS_MAP -> {
                OlPlans.set(plan, list);

                var regs = new byte[2];
                for(var p : Geometry.d4) {
                    if(acceptJointPlan(plan, OlPlans.get(p.x, p.y))) {
                        regs[0] += 1 << regs[1]++;
                    }
                }

                Draw.rect(sprites[regs[0]], plan.drawx(), plan.drawy());
            }
        }
    }

    @Override
    public void load() {
        super.load();

        if(hasRegion("-preview")) {
            this.uiIcon = loadRegion("-preview");
        }

        if(this.enableJoinsLoading) {
            if(this.enableSpriteSplitting) {
                TextureRegion sheet = loadRegion(joinsSpritesPrefix);

                this.sprites = switch(drawStyle) {
                    case DEFAULT -> null;
                    case ENABLE_JOINS_MAP -> OlGraphics.getRegions(sheet, 4, 4, 32);
                    case ARTIFICIAL_ROTATION -> OlGraphics.getRegions(sheet, 4, 1, 32);
                };
            } else {
                var regions = new Seq<TextureRegion>();

                int i = 1;
                while(hasRegion("-" + i)) {
                    regions.add(this.loadRegion("-" + i));
                    i++;
                }

                this.sprites = regions.items;
            }
        }
    }

    public enum DrawStyle {
        DEFAULT,
        ENABLE_JOINS_MAP,
        ARTIFICIAL_ROTATION
    }

    public class GraphBlockBuild extends Building implements Ranged {
        public boolean acceptJoint(Building building) {
            return building != null && building.block == this.block;
        }

        public Building nearby() {
            return this.nearby(this.rotation);
        }

        public Building antiNearby() {
            return switch(this.rotation) {
                case 0 -> this.nearby(2);
                case 1 -> this.nearby(3);
                case 2 -> this.nearby(0);
                case 3 -> this.nearby(1);
                //unreached
                default -> null;
            };
        }

        public boolean isActive() {
            return true;
        }

        @Override
        public boolean enabledRange() {
            return enableRange;
        }

        @Override
        public float range() {
            return rangeRadius;
        }

        @Override
        public void draw() {
            if(drawStyle == DrawStyle.DEFAULT) {
                super.draw();
            } else {
                if(drawStyle == DrawStyle.ARTIFICIAL_ROTATION) {
                    Draw.rect(sprites[this.rotation], x, y, this.drawrot());
                } else {
                    var regs = new byte[2];
                    for(var p : Geometry.d4) {
                        var p2 = p.cpy().add(tileX(), tileY());
                        if(acceptJoint(world.build(p2.x, p2.y))) {
                            regs[0] += 1 << regs[1]++;
                        }
                    }

                    Draw.rect(sprites[regs[0]], x, y, this.drawrot());
                }

                this.drawTeamTop();
            }
        }

        @Override
        public BlockStatus status() {
            if(normalBlockStatusResultReturn) {
                return super.status();
            }

            return !canConsume() ? BlockStatus.noInput : BlockStatus.active;
        }

        @Override
        public boolean canConsume() {
            if(normalCanConsumeResultReturn) {
                return super.canConsume();
            }

            for(Consume consume : consumers) {
                if(consume.efficiency(this) < 0.2f) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            this.drawRange();
        }
    }
}