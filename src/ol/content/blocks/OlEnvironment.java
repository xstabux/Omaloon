package ol.content.blocks;

import mindustry.content.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import ol.content.*;
import ol.graphics.*;

public class OlEnvironment {
    public static Block
            oreGrumon, oreOmalite, oreTungsten,
            gravelDalanii, greeniteDalanii, darkGreeniteDalanii, dalanii, deepDalanii,
            grun, grunWall,
            gravel, gravelWall,
            greenite, greeniteWall,
            darkGreenite, darkGreeniteWall;

    public static void load() {
        //region Ores
        oreGrumon = new OreBlock("grumon-ore"){{
            oreDefault = true;
            variants = 3;
            oreThreshold = 45F;
            oreScale = 0.3F;
            itemDrop = OlItems.grumon;

            localizedName = itemDrop.localizedName;
            mapColor.set(itemDrop.color);
            useColor = true;
        }};

        oreTungsten = new OreBlock("tungsten-ore"){{
            oreDefault = true;
            variants = 3;
            oreThreshold = 25F;
            oreScale = 0.3F;
            itemDrop = Items.tungsten;

            mapColor.set(itemDrop.color);
            useColor = true;
        }};

        oreOmalite = new OreBlock("omalite-ore"){{
            oreDefault = true;
            variants = 3;
            oreThreshold = 25.4F;
            oreScale = 0.3F;
            itemDrop = OlItems.omalite;

            localizedName = itemDrop.localizedName;
            mapColor.set(itemDrop.color);
            useColor = true;
        }};
        //endregion Ores
        gravelDalanii = new Floor("gravel-dalanii") {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            speedMultiplier = 0.8f;
            variants = 3;

            status = OlStatusEffects.slime;

            statusDuration = 6f;
            supportsOverlay = true;
            albedo = 0.9f;
            isLiquid = true;

            liquidDrop = OlLiquids.dalanii;
            liquidMultiplier = 1.5f;
            cacheLayer = OlCacheLayer.dalaniteLayer;
        }};

        greeniteDalanii = new Floor("greenite-dalanii"){{
            speedMultiplier = 0.8f;
            variants = 3;

            status = OlStatusEffects.slime;

            statusDuration = 6f;
            supportsOverlay = true;
            albedo = 0.9f;
            isLiquid = true;

            liquidDrop = OlLiquids.dalanii;
            liquidMultiplier = 1.5f;
            cacheLayer = OlCacheLayer.dalaniteLayer;
        }};

        darkGreeniteDalanii = new Floor("dark-greenite-dalanii"){{
            speedMultiplier = 0.8f;
            variants = 3;

            status = OlStatusEffects.slime;

            statusDuration = 6f;
            supportsOverlay = true;
            albedo = 0.9f;
            isLiquid = true;

            liquidDrop = OlLiquids.dalanii;
            liquidMultiplier = 1.5f;
            cacheLayer = OlCacheLayer.dalaniteLayer;
        }};

        dalanii = new Floor("flor-dalanii") {{
            speedMultiplier = 0.5f;
            variants = 0;
            status = OlStatusEffects.slime;
            statusDuration = 6f;
            supportsOverlay = true;

            albedo = 0.9f;
            isLiquid = true;
            liquidDrop = OlLiquids.dalanii;
            liquidMultiplier = 1.5f;

            cacheLayer = OlCacheLayer.dalaniteLayer;
        }};

        deepDalanii = new Floor("deep-dalanii") {{
            speedMultiplier = 0.3f;
            variants = 0;
            status = OlStatusEffects.slime;
            statusDuration = 6f;
            supportsOverlay = true;

            drownTime = 210f;
            albedo = 0.9f;
            isLiquid = true;
            liquidDrop = OlLiquids.dalanii;
            liquidMultiplier = 1.5f;

            cacheLayer = OlCacheLayer.dalaniteLayer;
        }};

        grun = new Floor("grun"){{
            variants = 4;
            wall = grunWall;
        }};

        grunWall = new StaticWall("grun-wall"){{
           variants = 3;
        }};

        gravel = new Floor("gravel"){{
           variants = 3;
           itemDrop = Items.sand;
           wall = gravelWall;
        }};

        gravelWall = new StaticWall("gravel-wall"){{
           variants = 2;
            itemDrop = Items.sand;
        }};

        greenite = new Floor("greenite"){{
           variants = 3;
           wall = greeniteWall;
        }};

        greeniteWall = new StaticWall("greenite-wall"){{
           variants = 2;
        }};

        darkGreenite = new Floor("dark-greenite"){{
           variants = 3;
           wall = darkGreeniteWall;
        }};

        darkGreeniteWall = new StaticWall("dark-greenite-wall"){{
            variants = 2;
        }};
    }
}
