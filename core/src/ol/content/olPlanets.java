package ol.content;

import mindustry.graphics.g3d.SunMesh;
import ol.graphics.olPal;
import ol.maps.generators.OmaLoonPlanetGenerator;
import ol.maps.generators.SetPlanetGenerator;
import ol.type.planets.olPlanet;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.content.Planets;
import mindustry.ctype.ContentList;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;

public class olPlanets implements ContentList {

    public static Planet amsha, omaloon, set;

    @Override
    public void load() {
        amsha = new olPlanet("amsha", Planets.sun, 4f, 0){{
            bloom = true;
            accessible = false;
            hasAtmosphere = true;
            orbitRadius = 145;
            meshLoader = () -> new SunMesh(
                    this, 4, 5, 0.3f, 1.0f, 1.2f, 1, 1.3f,
                    Color.valueOf("#8B4513"),
                    Color.valueOf("#A0522D"),
                    Color.valueOf("c2311e"),
                    Color.valueOf("ff6730"),
                    Color.valueOf("bf342f"),
                    Color.valueOf("8e261d")
            );
        }};
        
        omaloon = new olPlanet("omaloon", amsha, 1, 3){{
            generator = new OmaLoonPlanetGenerator();
            hasAtmosphere = true;
            meshLoader = () -> new HexMesh(this, 6);  
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(olPal.OLBlu).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
                new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(olPal.OLBlu, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
            );            
            atmosphereColor = Color.valueOf("166a76");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            landCloudColor = olPal.OLBlu.cpy().a(0.5f);
            orbitRadius = 60f;
            startSector = 20;
            accessible = true;
            alwaysUnlocked = false;
            orbitTime = Mathf.pow(orbitRadius, 1.5f) * 960;
        }};
        
        set = new olPlanet("set", omaloon, 0.40f){{
            generator = new SetPlanetGenerator();
            hasAtmosphere = true;
            atmosphereRadIn = 0.050f;
            atmosphereRadOut = 0.060f;
            atmosphereColor = Color.valueOf("d6dbe7");
            bloom = true;
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = false;
            landCloudColor = olPal.OLBlu.cpy().a(0.5f);
            orbitRadius = 10.3f;
            startSector = 20;
            accessible = true;
            sectorApproxRadius = 1f;
            rotateTime = 35f * 40f;
        }};
    }
}
