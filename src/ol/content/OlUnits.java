package ol.content;

import mindustry.graphics.Layer;
import mindustry.type.*;

import ol.type.units.ornitopter.Blade;
import ol.type.units.ornitopter.OrnitopterUnitEntity;
import ol.type.units.ornitopter.OrnitopterUnitType;

public class OlUnits {
    public static UnitType
    t1;

    public static void load(){
         t1 = new OrnitopterUnitType("t1"){{
             speed = 2.7f;
             accel = 0.08f;
             drag = 0.04f;
             flying = true;
             health = 120;
             fallSpeed = 0.1f;
             constructor = OrnitopterUnitEntity::new;
             engineSize = 0f;
             range = 15 * 8f;
             maxRange = range;
             rotateSpeed = 6f;
             blade.addAll(
                     //first
                     new Blade(name + "-blade"){{
                         x = 5f;
                         y = 3f;
                         bladeMoveSpeed = 40f;
                         bladeCount = 1;
                         bladeBlurAlphaMultiplier = 0.7f;
                     }},
                     new Blade(name + "-blade1"){{
                         x = -5f;
                         y = 3f;
                         bladeMoveSpeed = -40f;
                         bladeCount = 1;
                         bladeBlurAlphaMultiplier = 0.7f;
                     }},
                     //second
                     new Blade(name + "-blade2"){{
                         x = 5f;
                         y = 1f;
                         bladeMoveSpeed = -40f;
                         bladeCount = 1;
                         bladeBlurAlphaMultiplier = 0.7f;
                     }},
                     new Blade(name + "-blade3"){{
                         x = -5f;
                         y = 1f;
                         bladeMoveSpeed = 40f;
                         bladeCount = 1;
                         bladeBlurAlphaMultiplier = 0.7f;
                     }}
             );
             weapons.add(
                     new Weapon(name + "-w1"){{
                         layerOffset = -0.01f;
                         mirror = true;
                         x = 2.8f; y = 9.8f;
                     }},
                     new Weapon(name + "-w2"){{
                         layerOffset = 1f;
                         mirror = true;
                         x = 5; y = 2f;
                     }}
             );
             hitSize = 13;
         }};
     }
}
