package ol.content;

import arc.graphics.Color;
import mindustry.annotations.Annotations.*;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.Sounds;
import mindustry.type.*;

import ol.gen.*;
import ol.type.units.ornitopter.Blade;
import ol.type.units.ornitopter.OrnitopterUnitType;

public class OlUnitTypes {

    @EntityDef(value = Ornitorpterc.class)
    public static UnitType
    t1;

    public static void load(){
        OlEntityMapping.init();
         t1 = new OrnitopterUnitType("t1"){{
             speed = 2.7f;
             accel = 0.08f;
             drag = 0.04f;
             flying = true;
             health = 210;
             range = 15 * 8f;
             maxRange = range;
             rotateSpeed = 6f;
             blades.addAll(
                     //first
                     new Blade(name + "-blade1"){{
                         x = -5f;
                         y = 3f;
                         bladeMoveSpeed = -40f;
                         bladeBlurAlphaMultiplier = 0.7f;
                     }},
                     //second
                     new Blade(name + "-blade1"){{
                         x = -5f;
                         y = 1f;
                         bladeMoveSpeed = 40f;
                         bladeBlurAlphaMultiplier = 0.7f;
                     }}
             );
             weapons.add(
                     new Weapon(name + "-w1"){{
                         layerOffset = -0.01f;
                         mirror = true;
                         alternate = false;
                         x = 2.8f; y = 9.8f;
                         reload = 19f;
                         smoothReloadSpeed = 0.3f;
                         bullet = new BasicBulletType(2.5f, 5){{
                             width = 7f;
                             height = 9f;
                             lifetime = 40f;
                             shootEffect = Fx.shootSmall;
                             smokeEffect = Fx.shootSmallSmoke;
                             ammoMultiplier = 2;
                             trailLength = 6;
                         }};
                     }},
                     new Weapon(name + "-w2"){{
                         layerOffset = 1f;
                         mirror = true;
                         x = 5; y = 2f;
                         reload = 30f;
                         smoothReloadSpeed = 0.5f;
                         shootSound = Sounds.missile;

                         bullet = new MissileBulletType(3f, 4){{
                             width = 6f;
                             height = 6f;
                             shrinkY = 0f;
                             homingRange = 60f;
                             splashDamageRadius = 25f;
                             splashDamage = 5f;
                             lifetime = 45f;
                             trailColor = Color.valueOf("90efbf");
                             backColor = Color.valueOf("90efbf");
                             frontColor = Color.valueOf("90efbf");
                             hitEffect = Fx.blastExplosion;
                             despawnEffect = Fx.blastExplosion;
                             weaveScale = 6f;
                             weaveMag = 1f;
                         }};
                     }}
             );
             hitSize = 16;
         }};
     }
}
