package ol.world.blocks.pressure.meta;

import arc.func.Func;
import mindustry.gen.Building;

public class ConsumePressureDynamic extends ConsumePressure {
    public Func<Building, Float> func = ignored -> 0F;

    public ConsumePressureDynamic(Func<Building, Float> func) {
        if(func != null) {
            this.func = func;
        }
    }

    @Override
    public float efficiency(Building build) {
        this.pressureConsume = this.func.get(build);
        return super.efficiency(build);
    }

    @Override
    public float efficiencyMultiplier(Building build) {
        this.pressureConsume = this.func.get(build);
        return super.efficiencyMultiplier(build);
    }
}