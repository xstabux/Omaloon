package ol;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mma.*;

import static mindustry.Vars.headless;

public class olVars extends ModVars {
    private final static Seq<Runnable> onLoad = new Seq<>();
//    public static ModSettings settings;
//    public static ModNetClient netClient;
//    public static ModNetServer netServer;
//    public static olUI ui;
//    public static ModLogic logic;

    static {
        new olVars();
    }

    public static void create() {
        //none
    }

    public static void init() {}

    /**
     * Here you can initialize your classes as ModSettings or ModLogic and
     * add listeners to ModListener (listener variable in ModVars)
     */
    public static void load() {
        onLoad.each(Runnable::run);
        onLoad.clear();
        //if (!headless) {
            //listener.add(ui = new olUI());
        //}
    }


    public static void modLog(String text, Object... args) {
        Log.info("[@] @", modInfo == null ? "test-java" : modInfo.name, Strings.format(text, args));
    }

    @Override
    protected void onLoad(Runnable runnable) {
        onLoad.add(runnable);
    }

    @Override
    public String getFullName(String name) {
        return null;
    }

    @Override
    public ContentList[] getContentList() {
        return new ContentList[]{
        };
    }

    @Override
    protected void showException(Throwable ex) {
        Log.err(ex);
        if (headless) {
            return;
        }
        if (Vars.ui == null) {
            Events.on(EventType.ClientLoadEvent.class, e -> showException(ex));
            return;
        }
        Vars.ui.showException(ex);
    }

    public interface ThrowableRunnable {
        void run() throws Exception;
    }
}
