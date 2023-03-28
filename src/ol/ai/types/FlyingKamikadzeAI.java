package ol.ai.types;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.gen.TimedKillc;
import mindustry.world.meta.BlockFlag;

public class FlyingKamikadzeAI extends AIController {
    public @Nullable Vec2 targetPoint;

    @Override
    public void updateMovement(){
        unloadPayloads();

        float time = unit instanceof TimedKillc t ? t.time() : 1000000f;

        if(targetPoint != null){
            unit.lookAt(targetPoint);
        } else {
            //choose a random point near the unit
            targetPoint = new Vec2(unit.x + Mathf.range(32f), unit.y + Mathf.range(32f));
        }

        //move forward forever
        unit.moveAt(vec.trns(unit.rotation, unit.type.missileAccelTime <= 0f ? unit.speed() : Mathf.pow(Math.min(time / unit.type.missileAccelTime, 1f), 2f) * unit.speed()));

        var build = unit.buildOn();

        //kill instantly on enemy building contact
        if(build != null && build.team != unit.team && (build == target || !build.block.underBullets)){
            unit.kill();
        }
    }

    @Override
    public boolean retarget(){
        //more frequent retarget due to high speed. TODO won't this lag?
        return timer.get(timerTarget, 4f);
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground){
        var result = findMainTarget(x, y, range, air, ground);

        //if the main target is in range, use it, otherwise target whatever is closest
        return checkTarget(result, x, y, range) ? target(x, y, range, air, ground) : result;
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
        var core = targetFlag(x, y, BlockFlag.core, true);

        if(core != null && Mathf.within(x, y, core.getX(), core.getY(), range)){
            return core;
        }

        for(var flag : unit.type.targetFlags){
            if(flag == null){
                Teamc result = target(x, y, range, air, ground);
                if(result != null) return result;
            }else if(ground){
                Teamc result = targetFlag(x, y, flag, true);
                if(result != null) return result;
            }
        }

        return core;
    }
}