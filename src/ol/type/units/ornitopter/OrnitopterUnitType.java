package ol.type.units.ornitopter;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;

import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.Env;

import mma.type.*;
import mma.type.pixmap.*;

import ol.gen.*;
import ol.world.draw.Outliner;

public class OrnitopterUnitType extends UnitType implements ImageGenerator{
    public final Seq<Blade> blades = new Seq<>();

    public float bladeDeathMoveSlowdown = 0.01f, fallDriftScl = 60f;
    public float fallSmokeX = 0f, fallSmokeY = -5f, fallSmokeChance = 0.1f;

    public OrnitopterUnitType(String name){
        super(name);
        engineSize = 0f;
        outlineColor = Color.valueOf("454552");
        envDisabled = Env.space;
    }

    public void drawRotor(Unit unit){
        float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        applyColor(unit);
        if(unit instanceof Ornitorpterc copter){
            for(int sign : Mathf.signs){
                long seedOffset = 0;
                for(Blade.BladeMount mount : copter.blades()){
                    Blade blade = mount.blade;
                    float rx = unit.x + Angles.trnsx(unit.rotation - 90, blade.x * sign, blade.y);
                    float ry = unit.y + Angles.trnsy(unit.rotation - 90, blade.x * sign, blade.y);
                    float bladeScl = Draw.scl * blade.bladeSizeScl;
                    float shadeScl = Draw.scl * blade.shadeSizeScl;


                    if(blade.bladeRegion.found()){
                        Draw.z(z + blade.bladeLayer);
                        Draw.alpha(blade.blurRegion.found() ? 1 - (copter.bladeMoveSpeedScl() / 0.8f) : 1);
                        Draw.rect(
                        blade.bladeOutlineRegion, rx, ry,
                        blade.bladeOutlineRegion.width * bladeScl * sign,
                        blade.bladeOutlineRegion.height * bladeScl,
                        unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );
                        Draw.mixcol(Color.white, unit.hitTime);
                        Draw.rect(blade.bladeRegion, rx, ry,
                        blade.bladeRegion.width * bladeScl * sign,
                        blade.bladeRegion.height * bladeScl,
                        unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );

                        if(blade.doubleBlade){
                            Draw.rect(
                            blade.bladeOutlineRegion, rx, ry,
                            blade.bladeOutlineRegion.width * bladeScl * sign,
                            blade.bladeOutlineRegion.height * bladeScl,
                            -unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                            );
                            Draw.mixcol(Color.white, unit.hitTime);
                            Draw.rect(blade.bladeRegion, rx, ry,
                            blade.bladeRegion.width * bladeScl * sign,
                            blade.bladeRegion.height * bladeScl,
                            -unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                            );
                        }
                        Draw.reset();
                    }

                    if(blade.blurRegion.found()){
                        Draw.z(z + blade.bladeLayer);
                        Draw.alpha(copter.bladeMoveSpeedScl() * blade.bladeBlurAlphaMultiplier * (copter.dead() ? copter.bladeMoveSpeedScl() * 0.5f : 1));
                        Draw.rect(
                        blade.blurRegion, rx, ry,
                        blade.blurRegion.width * bladeScl * sign,
                        blade.blurRegion.height * bladeScl,
                        unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );

                        // Double Rotor Blur
                        if(blade.doubleBlade){
                            Draw.rect(
                            blade.blurRegion, rx, ry,
                            blade.blurRegion.width * bladeScl * sign,
                            blade.blurRegion.height * bladeScl,
                            unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                            );
                        }
                        Draw.reset();
                    }

                    Draw.reset();
                    if(blade.shadeRegion.found()){
                        Draw.z(z + blade.bladeLayer + 0.001f);
                        Draw.alpha(copter.bladeMoveSpeedScl() * blade.bladeBlurAlphaMultiplier * (copter.dead() ? copter.bladeMoveSpeedScl() * 0.5f : 1));
                        Draw.rect(
                        blade.shadeRegion, rx, ry,
                        blade.shadeRegion.width * shadeScl * sign,
                        blade.shadeRegion.height * shadeScl,
                        unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
                        );
                        Draw.mixcol(Color.white, unit.hitTime);
                        Draw.alpha(copter.bladeMoveSpeedScl() * blade.bladeBlurAlphaMultiplier * (copter.dead() ? copter.bladeMoveSpeedScl() * 0.5f : 1));
                        Draw.rect(
                        blade.shadeRegion, rx, ry,
                        blade.shadeRegion.width * shadeScl * sign,
                        blade.shadeRegion.height * shadeScl,
                        unit.rotation - 90 + sign*Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMoveSpeed, -blade.minimumBladeMoveSpeed)
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
        for (Blade blade : blades) {
            Outliner.outlineRegion(packer, blade.bladeRegion, outlineColor, blade.spriteName + "-outline", outlineRadius);
            Outliner.outlineRegion(packer, blade.shadeRegion, outlineColor, blade.spriteName + "-top-outline", outlineRadius);
        }
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        drawRotor(unit);
    }

    @Override
    public void load(){
        super.load();
        blades.each(Blade::load);
    }
}
