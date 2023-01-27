package ol.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
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
