package ol.world.blocks.pressure.meta;

import mindustry.gen.Building;
import mindustry.world.consumers.Consume;

public class ConsumePressure extends Consume {
    public boolean mulEfficiency = true;
    public float pressureConsume = 0;

    public ConsumePressure() {
        super();
    }

    @Override public float efficiency(Building build) {
        if(build instanceof PressureAbleBuild ableBuild) {
            if(this.mulEfficiency) {
                return Math.min(ableBuild.pressure() / this.pressureConsume, 1F);
            } else {
                return ableBuild.pressure() >= this.pressureConsume ? 1F : 0F;
            }
        }

        return super.efficiency(build);
    }

    @Override public float efficiencyMultiplier(Building build) {
        if (build instanceof PressureAbleBuild ableBuild && this.mulEfficiency) {
            return ableBuild.pressure() / this.pressureConsume;
        }

        return super.efficiencyMultiplier(build);
    }
}