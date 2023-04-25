package ol.type.units.ornitopter;

import arc.*;
import arc.graphics.Color;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.*;

import mindustry.Vars;
import mindustry.annotations.Annotations;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.io.*;

public class Blade {
    public final String spriteName;
    public TextureRegion bladeRegion, blurRegion, bladeOutlineRegion, shadeRegion;

    /**
     * Rotor offsets from the unit
     */
    public float x = 0f, y = 0f;
    /**
     * Rotor Size Scaling
     */
    public float bladeSizeScl = 1, shadeSizeScl = 1;
    /**
     * Blade base movement speed
     */
    public float bladeMoveSpeed = 12;
    /**
     * Minimum Movement Speed for blade, the blade speed won't go below this value, even when dying
     */
    public float minimumBladeMoveSpeed = 0f;
    /**
     * On what bladeLayer is the Blade drawn at
     */
    public float bladeLayer = 0.5f;
    /**
     * Multiplier for blurs alpha
     */
    public float bladeBlurAlphaMultiplier = 0.9f;
    /**
     * Duplicates the initial blade and moves it on the opposite dirrection
     */
    public boolean doubleBlade = false;

    public Blade(String name) {
        this.spriteName = name;
    }

    public static class BladeMount {
        public Blade blade;
        public float bladeRotation;
        public float bladeBlurRotation;
        public long seed;

        public BladeMount(Blade blade) {
            this.blade = blade;
        }
    }

    public void load() {
        bladeRegion = Core.atlas.find(spriteName);
        blurRegion = Core.atlas.find(spriteName + "-blur");
        bladeOutlineRegion = Core.atlas.find(spriteName + "-outline");
        shadeRegion = Core.atlas.find(spriteName + "-shade");
    }

    void makeOutline(MultiPacker.PageType page, MultiPacker packer, TextureRegion region, boolean makeNew, Color outlineColor, int outlineRadius){
        if(region instanceof TextureAtlas.AtlasRegion at && region.found()){
            String name = at.name;
            if(!makeNew || !packer.has(name + "-outline")){
                String regName = name + (makeNew ? "-outline" : "");
                if(packer.registerOutlined(regName)){
                    PixmapRegion base = Core.atlas.getPixmap(region);
                    var result = Pixmaps.outline(base, outlineColor, outlineRadius);
                    Drawf.checkBleed(result);
                    packer.add(page, regName, result);
                }
            }
        }
    }

    // For mirroring
    public Blade copy() {
        return JsonIO.copy(this, new Blade(spriteName));
    }
}