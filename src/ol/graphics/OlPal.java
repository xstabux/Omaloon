package ol.graphics;

import arc.graphics.*;
import arc.graphics.g2d.Draw;

public class OlPal {
    public static Color

    OLBlue = Color.valueOf("83c1ed"),
    OLDarkBlue = Color.valueOf("517d9d"),
    OLDalanite = Color.valueOf("a8d4ff"),

    OLPressureDanger = Color.red,
    OLPressureMin = Color.darkGray, //place other color
    OLPressure = Color.gray; //place other color

    //OLPressureMin -> OLPressure mixcol
    public static Color mixcol(Color colorFrom, Color colorTo, float progress) {
        if(progress == 0) {
            return colorFrom;
        }

        if(progress == 1) {
            return colorTo;
        }

        float reverse = 1f - progress;
        return new Color(
                colorFrom.r * reverse + colorTo.r * progress,
                colorFrom.g * reverse + colorTo.g * progress,
                colorFrom.b * reverse + colorTo.b * progress,
                colorFrom.a * reverse + colorTo.a * progress
        );
    }
}