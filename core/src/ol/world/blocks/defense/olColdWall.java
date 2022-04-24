package ol.world.blocks.defense;

import ol.graphics.olPal;
import arc.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class olColdWall extends Wall {

    public StatusEffect status = StatusEffects.freezing;
    public float statusDuration = 600f;
    public Effect shotEffect = Fx.freezing;
    public Effect dynamicEffect = Fx.freezing;
    public float dynamicEffectChance = 0.003f;

    public olColdWall(String name) {
        super(name);
        flashHit = true;
        flashColor = olPal.OLBlu;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.abilities, table -> {
            table.image(status.uiIcon).size(18f);
            table.add(" [accent]" + status.localizedName + "[] " + (int) (statusDuration / 60) + " " + Core.bundle.get("unit.seconds"));
        });
    }

    public class OLColdWallBuild extends WallBuild {
        @Override
        public boolean collide(Bullet other) {
            if (other.type.speed > 0.01f && other.team != team) {
                other.hit = true;
                other.type.despawnEffect.at(other.x, other.y, other.rotation(), other.type.hitColor);

                damage(other.damage);
                hit = 1f;

                //create lightning if necessary
                if (lightningChance > 0f) {
                    if (Mathf.chance(lightningChance)) {
                        Lightning.create(team, lightningColor, lightningDamage, x, y, other.rotation() + 180f, lightningLength);
                        lightningSound.at(tile, Mathf.random(0.9f, 1.1f));
                    }
                }
                other.remove();
                return false;
            }
            return super.collide(other);
        }

        public void reactTo(Unit unit) {
            unit.apply(status, statusDuration);
            float angle = angleTo(unit);
            Tmp.v1.trns(angle, size * tilesize / 2f).add(this);
            shotEffect.at(Tmp.v1.x, Tmp.v1.y, angle, status.color);
        }

        @Override
        public boolean collision(Bullet bullet) {
            if (bullet.team != team && (bullet.owner instanceof Unit)) reactTo((Unit) bullet.owner);
            return super.collision(bullet);
        }

        @Override
        public void draw() {
            super.draw();
            if (Mathf.chanceDelta(dynamicEffectChance)) {
                dynamicEffect.at(x + Mathf.range(size * 3f), y + Mathf.range(size * 3));
            }
        }

        @Override
        public void drawLight() {
            super.drawLight();
            Drawf.light(team, x, y, 12f * size, status.color, 0.2f);
        }
    }
}