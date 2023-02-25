package ol.ui;

import arc.func.Cons;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import org.jetbrains.annotations.NotNull;

public class Invoker {
    public static void invoke(String name, @NotNull Cons<BaseDialog> cons) {
        BaseDialog baseDialog = new BaseDialog(name);
        baseDialog.buttons.defaults().size(200, 75);
        baseDialog.buttons.button("@back", Icon.left, baseDialog::hide);
        cons.get(baseDialog);
        baseDialog.show();
    }

    public static void invoke(String name, String text) {
        invoke(name, table -> table.cont.add(text));
    }

    public static void invokeFail(String text) {
        invoke("Fail", "[red]" + text + "[]");
    }
}