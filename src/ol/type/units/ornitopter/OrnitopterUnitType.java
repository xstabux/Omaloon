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

    // Drawing Rotors
    public void drawRotor(Unit unit){
        float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        //Weapon weapon = weapons.max(it -> it.layerOffset);
        //z += (weapon.layerOffset + 0.0001f);
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
    public Pixmap generate(Pixmap icon, PixmapProcessor processor){
        for(Blade blade : blades){
            Pixmap bladeOutline = PixmapProcessor.outline(processor.get(blade.bladeRegion).copy());
            processor.save(bladeOutline, blade.spriteName + "-outline");
            processor.save(PixmapProcessor.outline(processor.get(blade.shadeRegion).copy()), blade.spriteName + "-top-outline");
            icon = PixmapProcessor.drawScaleAt(icon, bladeOutline, (int)(blade.x / Draw.scl + icon.width / 2f - bladeOutline.width / 2f), (int)(-blade.y / Draw.scl + icon.height / 2f - bladeOutline.height / 2f));
            icon = PixmapProcessor.drawScaleAt(icon, bladeOutline.flipX(), (int)(-blade.x / Draw.scl + icon.width / 2f - bladeOutline.width / 2f), (int)(-blade.y / Draw.scl + icon.height / 2f - bladeOutline.height / 2f));
        }
        return icon;
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
