package ol.type.units.ornitopter;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;

import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import ol.world.draw.*;

public class OrnitopterUnitType extends UnitType {
    public final Seq<Blade> blade = new Seq<>();

    public float spinningFallSpeed = 0.5f;
    public float bladeDeathMoveSlowdown = 0.01f;
    public float fallSmokeX = 0f, fallSmokeY = -5f, fallSmokeChance = 0.1f;

    public OrnitopterUnitType(String name) {
        super(name);
        engineSize = 0f;
        constructor = OrnitopterUnitEntity::new;
    }

    // Drawing Rotors
    public void drawRotor(Unit unit) {
        float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
        applyColor(unit);
        if (unit instanceof OrnitopterUnitEntity copter) {
            for (Blade.BladeMount mount : copter.blades) {
                Blade blade = mount.blade;
                float rx = unit.x + Angles.trnsx(unit.rotation - 90, blade.x, blade.y);
                float ry = unit.y + Angles.trnsy(unit.rotation - 90, blade.x, blade.y);
                float bladeScl = Draw.scl * blade.bladeSizeScl;
                float shadeScl = Draw.scl * blade.shadeSizeScl;

                for (int i = 0; i < blade.bladeCount; i++) {
                    if (blade.bladeRegion.found()) {
                        Draw.z(z + blade.bladeLayer);
                        Draw.alpha(blade.blurRegion.found() ? 1 - (copter.bladeMoveSpeedScl / 0.8f) : 1);
                        Draw.rect(
                                blade.bladeOutlineRegion, rx, ry,
                                blade.bladeOutlineRegion.width * bladeScl,
                                blade.bladeOutlineRegion.height * bladeScl,
                                unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );
                        Draw.mixcol(Color.white, unit.hitTime);
                        Draw.rect(blade.bladeRegion, rx, ry,
                                blade.bladeRegion.width * bladeScl,
                                blade.bladeRegion.height * bladeScl,
                                unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );

                        if (blade.doubleBlade) {
                            Draw.rect(
                                    blade.bladeOutlineRegion, rx, ry,
                                    blade.bladeOutlineRegion.width * bladeScl * -Mathf.sign(false),
                                    blade.bladeOutlineRegion.height * bladeScl,
                                    -unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                            );
                            Draw.mixcol(Color.white, unit.hitTime);
                            Draw.rect(blade.bladeRegion, rx, ry,
                                    blade.bladeRegion.width * bladeScl * -Mathf.sign(false),
                                    blade.bladeRegion.height * bladeScl,
                                    -unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                            );
                        }
                        Draw.reset();
                    }

                    if (blade.blurRegion.found()) {
                        Draw.z(z + blade.bladeLayer);
                        Draw.alpha(copter.bladeMoveSpeedScl * blade.bladeBlurAlphaMultiplier * (copter.dead() ? copter.bladeMoveSpeedScl * 0.5f : 1));
                        Draw.rect(
                                blade.blurRegion, rx, ry,
                                blade.blurRegion.width * bladeScl,
                                blade.blurRegion.height * bladeScl,
                                unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );

                        // Double Rotor Blur
                        if (blade.doubleBlade) {
                            Draw.rect(
                                    blade.blurRegion, rx, ry,
                                    blade.blurRegion.width  * bladeScl * -Mathf.sign(false),
                                    blade.blurRegion.height * bladeScl,
                                    unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed,-blade.minimumBladeMoveSpeed)
                            );
                        }
                        Draw.reset();
                    }

                    Draw.reset();
                    if (blade.shadeRegion.found()) {
                        Draw.z(z + blade.bladeLayer + 0.001f);
                        Draw.alpha(copter.bladeMoveSpeedScl * blade.bladeBlurAlphaMultiplier * (copter.dead() ? copter.bladeMoveSpeedScl * 0.5f : 1));
                        Draw.rect(
                                blade.shadeRegion, rx, ry,
                                blade.shadeRegion.width * shadeScl,
                                blade.shadeRegion.height * shadeScl,
                                unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );
                        Draw.mixcol(Color.white, unit.hitTime);
                        Draw.alpha(copter.bladeMoveSpeedScl * blade.bladeBlurAlphaMultiplier * (copter.dead() ? copter.bladeMoveSpeedScl * 0.5f : 1));
                        Draw.rect(
                                blade.shadeRegion, rx, ry,
                                blade.shadeRegion.width * shadeScl,
                                blade.shadeRegion.height * shadeScl,
                                unit.rotation - 90 + Mathf.random(blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );
                        Draw.reset();
                    }
                }
            }
        }
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);
        // Helicopter Rotors
        for (Blade blade : blade) {
            Outliner.outlineRegion(packer, blade.bladeRegion, outlineColor, blade.name + "-outline", outlineRadius);
            Outliner.outlineRegion(packer, blade.shadeRegion, outlineColor, blade.name + "-top-outline", outlineRadius);
        }
    }

    @Override
    public void draw(Unit unit) {
        float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
        super.draw(unit);
        Draw.z(z);
        drawRotor(unit);
    }

    @Override
    public void load() {
        super.load();
        blade.each(Blade::load);
    }
}
