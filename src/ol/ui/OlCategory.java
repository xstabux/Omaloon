package ol.ui;


import arc.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;
import ol.content.blocks.*;
import ol.world.blocks.*;
import ol.world.blocks.pressure.meta.*;
import org.jetbrains.annotations.*;

public class OlCategory{
    public static final Seq<OlCategory> all = new Seq<>();
    public final TextureRegionDrawable icon;
    public final int id;
    public static OlCategory turret = new OlCategory(Icon.turret);
    public static OlCategory production = new OlCategory(Icon.production);
    public static OlCategory distribution = new OlCategory(Icon.distribution);
    public static OlCategory liquid = new OlCategory(Icon.liquid);
    public static OlCategory power = new OlCategory(Icon.power);
    public static OlCategory defence = new OlCategory(Icon.defense);
    public static OlCategory crafting = new OlCategory(Icon.crafting);
    public static OlCategory units = new OlCategory(Icon.units);
    public static OlCategory effect = new OlCategory(Icon.effect);
    public static OlCategory logic = new OlCategory(Icon.logic);
    public static OlCategory pressure = new OlCategory(OlPressure.pressurePipe.region, "pressure");
    public static OlCategory rails = new OlCategory(Icon.terrain);

    public OlCategory(TextureRegionDrawable icon){
        this.icon = icon;
        id=all.size;
        all.add(this);
    }

    public OlCategory(TextureRegion region, String name){
        this(generate(region, name));
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static OlCategory of(mindustry.type.Category category){
        if(category == null) return null;
        return switch(category){
            case turret -> turret;
            case production -> production;
            case distribution -> distribution;
            case liquid -> liquid;
            case power -> power;
            case defense -> defence;
            case crafting -> crafting;
            case units -> units;
            case effect -> effect;
            case logic -> logic;
        };
    }

    public static OlCategory of(int id){
        return all.get(id);
    }

    @Contract(value = "null -> null", pure = true)
    public static OlCategory of(Block block){
        if(block == null) return null;

        if(block instanceof RailBlock){
            return rails;
        }

        if(block instanceof PressureAble || block instanceof MirrorBlock){
            return pressure;
        }

        return of(block.category);
    }

    @NotNull
    public static TextureRegionDrawable generate(TextureRegion region,
                                                 String name){
        var reg = new TextureRegionDrawable(region);
        reg.setName(name);
        return reg;
    }

    public String name(){
        if(this.icon == null) return "";
        return this.icon.getName();
    }

    public OlCategory prev(){
        return of(this.id - 1);
    }

    public OlCategory next(){
        return of(this.id + 1);
    }

    public String locale(){
        return Core.bundle.get("category." + this.name());
    }

    public boolean equals(Block block){
        return this.equals(of(block));
    }
}
