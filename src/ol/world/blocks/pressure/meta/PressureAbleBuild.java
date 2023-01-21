package ol.world.blocks.pressure.meta;

import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.world.Tile;

import ol.utils.Angles;
import ol.utils.pressure.PressureAPI;

import arc.struct.Seq;
import ol.world.blocks.pressure.PressureJunction;

import java.util.Objects;
import static mindustry.Vars.world;

/** interface of the building of the block, that extends PressureAble */
public interface PressureAbleBuild {
    /** gets pressure module */
    PressureModule getModule();

    /** get`s pressureAble block */
    PressureAble asBlock();

    /** get`s self but as building */
    Building asBuilding();

    default float pressure() {
        this.checkComp();
        return this.getModule().pressure;
    }

    default void pressure(float num) {
        this.checkComp();
        this.getModule().pressure = num;
    }

    default boolean canExplode() {
        this.checkComp();
        return this.asBlock().canExplode();
    }

    default float maxPressure() {
        this.checkComp();
        return this.asBlock().maxPressure();
    }

    default int tier() {
        this.checkComp();
        return this.asBlock().tier();
    }

    default float pressureDamage() {
        this.checkComp();
        return this.asBlock().pressureDamage();
    }

    default Effect pressureFx() {
        this.checkComp();
        return this.asBlock().damageFx();
    }

    default boolean online() {
        return true;
    }

    default Seq<Building> proximityNeighbor() {
        return new Seq<>();
    }

    default boolean inNet(Building b, PressureAbleBuild p, boolean junction) {
        if(b == null) {
            return false;
        }

        Building self = this.asBuilding();
        if(self == b) {
            return true;
        }

        int delta = 1;
        if(junction) {
            delta++;
        }

        if(!(PressureAPI.tierAble(p.tier(), this.tier())) || !p.online()) {
            return false;
        }

        int tx = self.tileX();
        int ty = self.tileY();

        Tile left = world.tile(tx - delta, ty);
        Tile right = world.tile(tx + delta, ty);

        if(left.build == b || right.build == b) {
            return Angles.alignX(self.rotation) || Angles.alignX(b.rotation);
        }

        Tile top = world.tile(tx, ty + delta);
        Tile bottom = world.tile(tx, ty - delta);

        if(top.build == b || bottom.build == b) {
            return Angles.alignY(self.rotation) || Angles.alignY(b.rotation);
        }

        return false;
    }

    /** set`s self pressure using other blocks */
    default void executeDefaultUpdateTileScript() {
        this.checkComp();

        Seq<PressureModule> modules = new Seq<>();
        PressureModule module = this.getModule();
        Building self = this.asBuilding();

        for(Building prox : self.proximity()) {
            Building prox32 = prox;
            boolean junction = false;

            if(prox32 instanceof PressureJunction.PressureJunctionBuild jun) {
                prox32 = jun.getInvert(self);
                junction = true;
            }

            if(prox32 instanceof PressureAbleBuild build) {
                if(build.inNet(self, this, junction) && this.inNet(prox32, build, junction)) {
                    modules.add(Objects.requireNonNull(build.getModule()));
                }
            }
        }

        for(Building neighbor : this.proximityNeighbor()) {
            if(neighbor instanceof PressureAbleBuild build) {
                modules.add(Objects.requireNonNull(build.getModule()));
            }
        }

        modules.add(module);
        float[] total = new float[1];
        total[0] = modules.sumf(PressureModule::getPressure);
        total[0] /= modules.size;

        modules.each(module32 -> {
            module32.pressure = total[0];
        });

        module.update(self);
    }

    /** return how many pressure bigger then maxPressure */
    default float pressureOverload() {
        return this.pressure() - this.maxPressure();
    }

    /** returns damage to building including build health and pressure overload */
    default float dynamicPressureDamage() {
        this.checkComp();

        if(this.isPressureDamages()) {
            float overload = this.pressure() / (float) this.maxPressure();
            return this.pressureDamage() * overload;
        }

        return 0F;
    }

    /**
     * @return true, if block to damage itself, false if not need to damage
     */
    default boolean isPressureDamages() {
        return this.canExplode() && this.pressure() > this.maxPressure();
    }

    /**
     * check interface on errors, if error found when throws
     * @throws NullPointerException if 1 of Object need components is null
     */
    default void checkComp() {
        Objects.requireNonNull(this.asBlock());
        Objects.requireNonNull(this.getModule());
        Objects.requireNonNull(this.asBuilding());
    }
}