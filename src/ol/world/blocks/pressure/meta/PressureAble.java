package ol.world.blocks.pressure.meta;

import ol.utils.pressure.PressureAPI;

/** interface of the PressureBlock, or for pressure things that can`t be extends on PressureBlock */
public interface PressureAble {
    /** the block can destroy self if pressure > maxPressure */
    boolean canExplode();

    /** the max pressure that can have this block */
    float maxPressure();

    /** returns the pressure tier */
    default int tier() {
        return PressureAPI.NULL_TIER;
    }

    /** how much damage was taken by pressure */
    default float pressureDamage() {
        return 0.002F;
    }
}