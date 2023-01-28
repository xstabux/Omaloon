package ol.entites;

import arc.math.*;
import arc.util.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import ol.gen.*;
import ol.type.units.ornitopter.*;
import ol.type.units.ornitopter.Blade.*;

@Component

public abstract class OrnitorpterComp implements Unitc, Ornitorpterc{
    @Import
    float x,y,rotation;
    @Import
    boolean dead;
    @Import
    UnitType type;
    public BladeMount[] blades;
    public float bladeMoveSpeedScl = 1f;

    @Override
    public void afterRead(){
      /*  OrnitopterUnitType type = (OrnitopterUnitType)this.type;
        for(int i = 0; i < type.blades.size; i++){
            blades[i].blade=type.blades.get(i);
        }*/
        setBlades(type);
        ;
    }

    @Override
    public void setType(UnitType type) {

        setBlades(type);
    }

    public void setBlades(UnitType type){
        if (type instanceof OrnitopterUnitType ornitopter) {
            blades = new BladeMount[ornitopter.blades.size];
            for (int i = 0; i < blades.length; i++) {
                Blade bladeType = ornitopter.blades.get(i);
                blades[i] = new BladeMount(bladeType);
            }
        }
    }

    public long drawSeed=0;
    @Override
    public void update() {
        drawSeed++;
        OrnitopterUnitType type = (OrnitopterUnitType) this.type;
        float rX = x + Angles.trnsx(rotation - 90, type.fallSmokeX, type.fallSmokeY);
        float rY = y + Angles.trnsy(rotation - 90, type.fallSmokeX, type.fallSmokeY);

        // Slows down rotor when dying
        if (dead || health() <= 0) {
            if (Mathf.chanceDelta(type.fallSmokeChance)) {
                Fx.fallSmoke.at(rX, rY);
                Fx.burning.at(rX, rY);
            }
            bladeMoveSpeedScl = Mathf.lerpDelta(bladeMoveSpeedScl, 0f, type.bladeDeathMoveSlowdown);
        } else {
            bladeMoveSpeedScl = Mathf.lerpDelta(bladeMoveSpeedScl, 1f, type.bladeDeathMoveSlowdown);
        }

        for (BladeMount blade : blades) {
            blade.bladeRotation += ((blade.blade.bladeMoveSpeed * bladeMoveSpeedScl) + blade.blade.minimumBladeMoveSpeed) * Time.delta;
        }
        type.fallSpeed = 0.006f;
    }
}
