package ol.content;

import ol.graphics.olPal;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.Rand;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;

public class olFx {
    private static final Rand rand = new Rand();

    public static final Effect
            blueSphere = new Effect(30f, e -> {
        color(olPal.OLBlu);
        Fill.circle(e.x, e.y, e.fin() * 4f);
    });
}
