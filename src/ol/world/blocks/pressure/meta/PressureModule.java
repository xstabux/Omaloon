package ol.world.blocks.pressure.meta;

import arc.math.Mathf;
import arc.util.Timer;
import arc.util.io.Reads;
import arc.util.io.Writes;

import kotlin.concurrent.TimersKt;
import mindustry.gen.Building;
import mindustry.world.modules.BlockModule;
import ol.content.OlFx;
import ol.world.blocks.pressure.PressurePipe;
import org.jetbrains.annotations.NotNull;

public class PressureModule extends BlockModule {
    public float pressure;
    int timer = 0;

    public float getPressure() {
        return this.pressure;
    }

    public PressureModule() {
    }

    public void update(Building build) {
        if(build == null) {
            return;
        }

        if(build instanceof PressureAbleBuild building) {
            float random = Mathf.random(-3, +3);
            timer++;
            if(building.isPressureDamages()) {
                build.damage(building.dynamicPressureDamage());
                if(timer > Mathf.random(35, 65) * 60) {
                    OlFx.pressureDamage.at(build.x + random, build.y + random, Mathf.random(0, 360));
                }
            }
        }
    }

    @Override public void write(@NotNull Writes write) {
        write.f(this.pressure);
    }

    @Override public void read(@NotNull Reads read) {
        this.pressure = read.f();
    }
}