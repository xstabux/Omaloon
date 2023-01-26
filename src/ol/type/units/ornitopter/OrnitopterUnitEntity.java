package ol.type.units.ornitopter;

import arc.math.*;

import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;

import ol.type.units.ornitopter.Blade.*;

public class OrnitopterUnitEntity extends UnitEntity {
    public BladeMount[] blades;
    public float bladeMoveSpeedScl = 1f;


    @Override
    public void setType(UnitType type) {
        super.setType(type);
        if (type instanceof OrnitopterUnitType ornitopter) {
            blades = new BladeMount[ornitopter.blade.size];
            for (int i = 0; i < blades.length; i++) {
                Blade bladeType = ornitopter.blade.get(i);
                blades[i] = new BladeMount(bladeType);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        OrnitopterUnitType type = (OrnitopterUnitType) this.type;
        float rX = x + Angles.trnsx(rotation - 90, type.fallSmokeX, type.fallSmokeY);
        float rY = y + Angles.trnsy(rotation - 90, type.fallSmokeX, type.fallSmokeY);

        // Slows down rotor when dying
        if (dead || health() <= 0) {
            rotation += Time.delta * (type.spinningFallSpeed * vel().len()) * Mathf.signs[id % 2];
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
