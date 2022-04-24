package ol.content;

import mindustry.content.*;
import mindustry.ctype.ContentList;
import mindustry.entities.bullet.*;
import ol.graphics.olPal;

public class olBullets implements ContentList {
    public static BulletType
            blueSphere;

    @Override
    public void load() {
        blueSphere = new BasicBulletType(3f, 240f) {{
            shrinkX = 0f;
            sprite = "sphere";
            shrinkY = 0f;
            lifetime = 50f;
            despawnEffect = Fx.none;
            frontColor = olPal.OLBlu;
            backColor = olPal.OLBlu;
            hitEffect = Fx.freezing;
            width = height = 14f;
            collidesTiles = false;
            trailColor = olPal.OLBlu;
            trailWidth = 3f;
            trailLength = 8;
            trailEffect = Fx.artilleryTrail;
        }};
    }
}
