package ol.world.blocks.pressure.meta;

import arc.util.io.Reads;
import arc.util.io.Writes;

import mindustry.gen.Building;
import mindustry.world.modules.BlockModule;
import org.jetbrains.annotations.NotNull;

public class PressureModule extends BlockModule {
    public float pressure;

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
            if(building.isPressureDamages()) {
                build.damage(building.dynamicPressureDamage());
                building.pressureFx().at(build);
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