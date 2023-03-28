package ol.world.blocks.pressure.meta;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.Timekeeper;
import arc.util.io.Reads;
import arc.util.io.Writes;

import mindustry.gen.Building;
import mindustry.world.modules.BlockModule;

import org.jetbrains.annotations.NotNull;

public class PressureModule extends BlockModule {
    public float pressure;
    public Timekeeper timer;

    public float getPressure() {
        return this.pressure;
    }

    public PressureModule() {
        this.timer = new Timekeeper(6000);
    }

    public void update(Building build) {
        if(build == null) {
            return;
        }

        if(build instanceof PressureAbleBuild building) {
            float random = Mathf.random(-3, +3);
            if(building.isPressureDamages()) {
                if(Math.floor(Time.globalTime) % 50 == 0) {
                    building.effect32().at(build.x + random, build.y + random, build.totalProgress() * random);
                }
                build.damage(building.dynamicPressureDamage());
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
