package ol.utils.pressure;

import mindustry.gen.Building;
import ol.world.blocks.pressure.PressureJunction.PressureJunctionBuild;
import ol.world.blocks.pressure.meta.PressureAbleBuild;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class PressureAPI {
    public static final int NULL_TIER = -1;

    @Contract("null, _ -> fail; _, null -> fail")
    public static boolean tierAble(PressureAbleBuild a, PressureAbleBuild b) {
        return tierAble(Objects.requireNonNull(a).tier(), Objects.requireNonNull(b).tier());
    }

    @Contract(pure = true)
    public static boolean tierAble(int tierA, int tierB) {
        return tierA == NULL_TIER || tierB == NULL_TIER || tierA == tierB;
    }

    @Contract("null, null -> false")
    public static<T extends Building> boolean netAble(T t1, T t2) {
        if(t1 instanceof PressureAbleBuild p1 && t2 instanceof PressureAbleBuild p2) {
            return p1.inNet(t2, p2, false) && p2.inNet(t1, p1, false);
        }

        return t1 instanceof PressureJunctionBuild || t2 instanceof PressureJunctionBuild;
    }
}