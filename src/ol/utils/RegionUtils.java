package ol.utils;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.ctype.UnlockableContent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RegionUtils {
    private static TextureRegion error;

    public static @NotNull TextureRegion getErrorRegion() {
        return error == null ? error = Core.atlas.find("error") : error;
    }

    @Contract("null -> true")
    public static boolean isErrorRegion(TextureRegion region) {
        return region == null || !region.found() || region.equals(getErrorRegion());
    }

    @Contract(pure = true)
    public static TextureRegion getRegion(String path, TextureRegion def) {
        path = toUtilPath(path);

        if(isErrorRegion(def)) {
            def = getErrorRegion();
        }

        return Core.atlas.find("ol-" + path, Core.atlas.find(path, def));
    }

    @Contract(pure = true)
    public static TextureRegion getRegion(String path) {
        return getRegion(path, getErrorRegion());
    }

    @Contract("null -> null")
    public static String toUtilPath(String olPath) {
        if(olPath != null && olPath.startsWith("ol-")) {
            return olPath.substring("ol-".length());
        }

        return olPath;
    }

    public record BlockRegionFinder(UnlockableContent content) {
        public TextureRegion getRegion(String prefix) {
            String name = this.content.name + prefix;

            return RegionUtils.getRegion(name, RegionUtils.getRegion(
                    this.content.getContentType().name().toLowerCase() + "-" + name
            ));
        }
    }
}