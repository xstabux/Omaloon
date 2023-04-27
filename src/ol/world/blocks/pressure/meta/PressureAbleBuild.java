package ol.world.blocks.pressure.meta;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.world.Tile;

import ol.content.OlFx;
import ol.utils.Angles;
import ol.utils.pressure.PressureAPI;

import arc.struct.Seq;
import ol.world.blocks.pressure.PressureJunction;
import org.jetbrains.annotations.NotNull;

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

    default float priority() {
        return 1;
    }

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

    default boolean online() {
        return true;
    }

    default Seq<Building> proximityNeighbor() {
        return new Seq<>();
    }

    default int netLen() {
        return net(new Seq<>()).size;
    }

    default Seq<Building> net(Seq<Building> res) {
        Building self = asBuilding();
        res.add(self);

        for(var b : self.proximity()) {
            if(!res.contains(b) && b instanceof PressureAbleBuild build && PressureAPI.netAble(b, self)) {
                build.net(res);
            }
        }

        for(var b : proximityNeighbor()) {
            if(!res.contains(b) && b instanceof PressureAbleBuild build && PressureAPI.netAble(b, self)) {
                build.net(res);
            }
        }

        return res;
    }

    default boolean COUNTER_IN_NET_CALL(Building b, PressureAbleBuild p, boolean junction) {
        Building self = asBuilding();

        int delta = 1;
        if(junction) {
            delta++;
        }

        int tx = self.tileX();
        int ty = self.tileY();

        Tile left = world.tile(tx - delta, ty);
        Tile right = world.tile(tx + delta, ty);

        if((left.build == b || right.build == b) && !Angles.alignX(self.rotation)) {
            return false;
        }

        Tile top = world.tile(tx, ty + delta);
        Tile bottom = world.tile(tx, ty - delta);

        if((top.build == b || bottom.build == b) && !Angles.alignY(self.rotation)) {
            return false;
        }

        return p.online() && (p.tier() == PressureAPI.NULL_TIER || p.tier() == tier());
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
        float cluster = modules.sumf(PressureModule::getPressure) / modules.size;
        ObjectMap<PressureModule, Float> clusters = new ObjectMap<>();
        for(var mod : modules) {
            float prop = mod.priority();
            if(prop != module.priority()) {
                float cl = prop * cluster;
                cluster += cluster - cl;
            }
            clusters.put(mod, cluster);
        }

        modules.each(module32 -> {
            module32.pressure = clusters.get(module32);
        });

        module.update(self);
    }

    default @NotNull Effect effect32() {
        return this.pressure() < 0 ? null : OlFx.pressureDamage;
    }

    /** returns damage to building including build health and pressure overload */
    default float dynamicPressureDamage() {
        this.checkComp();

        if(this.isPressureDamages()) {
            float pressure = this.pressure();
            float overload = pressure / this.maxPressure();
            return this.pressureDamage() + overload/40 + Mathf.random(0.02f,0.1f);
        }

        return 0F;
    }


    /**
     * @return true, if block to damage itself, false if not need to damage
     */
    default boolean isPressureDamages() {
        float p = this.pressure(), mp = this.maxPressure();
        return this.canExplode() && (p > mp | p < -mp);
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