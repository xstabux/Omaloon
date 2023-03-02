package ol.world.blocks.pressure;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;

import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import ol.content.blocks.*;
import ol.input.*;
import ol.utils.Angles;
import ol.utils.OlPlans;
import ol.utils.pressure.*;
import ol.world.blocks.GraphBlock;
import ol.world.blocks.pressure.meta.MirrorBlock;
import org.jetbrains.annotations.NotNull;

import static mindustry.Vars.*;

public class PressurePipe extends PressureBlock implements PressureReplaceable {
    public @Nullable Block junctionReplacement, bridgeReplacement;

    public PressurePipe(String name){
        super(name);

        conveyorPlacement = underBullets = rotate = true;
        drawArrow = solid = squareSprite = false;
        group = BlockGroup.power;
        priority = TargetPriority.transport;
        drawStyle = DrawStyle.ENABLE_JOINS_MAP;
    }

    public void drawT(int x, int y, int rotation) {
        TextureRegion pressureIcon = Core.atlas.find("ol-arrow");

        float dx = x * 8;
        float dy = y * 8;
        float ds = size * 8;

        if(rotation == 1 || rotation == 3){
            Draw.rect(pressureIcon, dx, dy + ds, -90);
            Draw.rect(pressureIcon, dx, dy - ds, 90);
        }

        if(rotation == 0 || rotation == 2){
            Draw.rect(pressureIcon, dx + ds, dy, 180);
            Draw.rect(pressureIcon, dx - ds, dy, 0);
        }
    }

    @Override public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        drawT(x, y, rotation);
    }

    @Override public boolean canReplace(Block other){
        boolean valid = true;
        if(other instanceof PressurePipe pipe) {
            valid = PressureAPI.tierAble(pipe.tier, tier);
        }

        return canBeReplaced(other) && valid;
    }

    @Override public void init() {
        super.init();

        if (junctionReplacement == null) {
            junctionReplacement = OlPressure.pressureJunction;
        }

        //I did this because there will be no other pipes for pressure anyway.
        //If there is an idea that it is better to implement, implement it.
        if (bridgeReplacement == null || !(bridgeReplacement instanceof PressureBridge)) {
            bridgeReplacement = switch (tier) {
                case 2 -> OlPressure.improvedPressureBridge;
                case 3 -> OlPressure.reinforcedPressureBridge;
                default -> OlPressure.pressureBridge;
            };
        }
    }

    public boolean checkType(BuildPlan s, @NotNull BuildPlan o) {
        Block block = o.block;

        if(block instanceof PressureJunction) {
            return true;
        }

        if(block instanceof PressureBlock pressureBlock) {
            boolean valid = PressureAPI.tierAble(pressureBlock.tier, tier);

            if(block instanceof PressurePipe pipe) {
                try {
                    valid &= pipe.acceptJointPlan(o, s);
                } catch(StackOverflowError ignored) {
                }
            }

            return valid;
        }

        return false;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(drawStyle == DrawStyle.ENABLE_JOINS_MAP) {
            OlPlans.set(plan, list);

            BuildPlan
                    top    = OlPlans.get(0, 1),
                    bottom = OlPlans.get(0, -1),
                    left   = OlPlans.get(-1, 0),
                    right  = OlPlans.get(1, 0);

            boolean
                    validTop    = top != null,
                    validBottom = bottom != null,
                    validLeft   = left != null,
                    validRight  = right != null;

            Func<BuildPlan, Boolean> canWork = plnFunc -> {
                Block block = plnFunc.block;

                if(block instanceof PressureBlock pressureBlock) {
                    boolean valid = PressureAPI.tierAble(pressureBlock.tier, tier);

                    if(block instanceof PressurePipe pipe) {
                        valid &= pipe.acceptJointPlan(plan, plnFunc);
                    }

                    return valid;
                }

                return block instanceof PressureJunction;
            };

            if(validTop) {
                validTop = canWork.get(top);
            }

            if(validBottom) {
                validBottom = canWork.get(bottom);
            }

            if(validLeft) {
                validLeft = canWork.get(left);
            }

            if(validRight) {
                validRight = canWork.get(right);
            }

            TextureRegion reg = sprites[GraphBlock.of(validTop, validBottom, validLeft, validRight)];
            Draw.rect(reg, plan.drawx(), plan.drawy());

            if(plan.worldContext && player != null && teamRegion != null && teamRegion.found()){
                if(teamRegions[player.team().id] == teamRegion){
                    Draw.color(player.team().color);
                }

                Draw.rect(teamRegions[player.team().id], plan.drawx(), plan.drawy());
                Draw.color();
            }

            drawPlanConfig(plan, list);
        }else{
            super.drawPlanRegion(plan, list);
        }
    }

    @Override
    public boolean acceptJointPlan(BuildPlan s, BuildPlan o) {
        if(s == null || o == null) return false;

        if(!checkType(s, o)) {
            return false;
        }

        int ox = s.x - o.x;
        int oy = s.y - o.y;

        if(ox == 0 && oy == 0){
            return true;
        }

        ox = ox < 0 ? -ox : ox;
        return (ox == 1) ? (Angles.alignX(o.rotation) || Angles.alignX(s.rotation))
                : (Angles.alignY(o.rotation) || Angles.alignY(s.rotation));
    }

    @Override public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) {
            return this;
        }

        Boolf<Point2> cont = p -> plans.contains(o ->
                o.x == req.x + p.x && o.y == req.y + p.y && o.rotation
                == req.rotation && (req.block instanceof PressurePipe ||
                        req.block instanceof PressureJunction
                || req.block instanceof MirrorBlock)
        );

        return cont.get(Geometry.d4(req.rotation)) &&
                   cont.get(Geometry.d4(req.rotation - 2)) &&
                   req.tile() != null &&
                   req.tile().block() instanceof PressurePipe &&
                   Mathf.mod(req.build().rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans) {
        if(bridgeReplacement == null) return;
        OLPlacement.calculateBridges(plans, (PressureBridge)bridgeReplacement);
    }

    public class PressurePipeBuild extends PressureBlockBuild {
        public boolean avalible(Building b) {
            return PressureAPI.netAble(this, b);
        }

        public boolean avalibleX() {
            return true;
        }

        public boolean avalibleY() {
            return true;
        }

        public boolean isCorrectType(Building building) {
            return building instanceof PressureJunction.PressureJunctionBuild ||
                    building instanceof MirrorBlock.MirrorBlockBuild;
        }

        @Override
        public void draw(){
            if(drawStyle != DrawStyle.ENABLE_JOINS_MAP) {
                super.draw();
                return;
            }

            Building left   = nearby(-1, 0);
            Building right  = nearby(1, 0);
            Building bottom = nearby(0, -1);
            Building top    = nearby(0, 1);

            boolean bLeft   = avalible(left)   || isCorrectType(left);
            boolean bRight  = avalible(right)  || isCorrectType(right);
            boolean bTop    = avalible(top)    || isCorrectType(top);
            boolean bBottom = avalible(bottom) || isCorrectType(bottom);

            float angle = ((avalibleY() && bTop ? 1 : 0) +
                    (avalibleY() && bBottom ? 1 : 0) +
                    (avalibleX() && bLeft ? 1 : 0) +
                    (avalibleX() && bRight ? 1 : 0)) == 0 ?
                    Angles.alignY(this.rotation) ? -90 : 0 : 0;

            byte comb = GraphBlock.of(
                    avalibleY() && bTop,
                    avalibleY() && bBottom,
                    avalibleX() && bLeft,
                    avalibleX() && bRight
            );

            TextureRegion region = sprites[comb];
            if(this.isPressureDamages()) {
                if(state.is(GameState.State.paused)) {
                    Draw.rect(region, this.x, this.y, angle);
                } else{
                    Draw.rect(region, this.x, this.y, angle + Mathf.random(-4, 4));
                }
            }else{
                Draw.rect(region, this.x, this.y, angle);
            }

            this.drawTeamTop();
        }
    }
}
