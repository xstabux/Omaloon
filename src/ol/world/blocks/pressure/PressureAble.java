package ol.world.blocks.pressure;

import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.world.Tile;

import ol.world.blocks.pressure.PressureJunction.PressureJunctionBuild;

import static mindustry.Vars.*;

public interface PressureAble {
    Seq<Building> proximity();
    Building self();

    float pressure();
    void pressure(float pressure);

    default boolean sdx(Building b2, Seq<Building> buildings, boolean jun) {
        return b2 instanceof PressureAble p && inNet(b2, p, jun) && p.inNet(self(), jun) && !buildings.contains(b2) && b2 != self() && b2.enabled;
    }

    default Seq<Building> net(Building building, Cons<PressureJunctionBuild> cons) {
        return net(building, cons, new Seq<>());
    }

    default Seq<Building> net(Building building) {
        return net(building, j -> {});
    }

    default Seq<Building> net() {
        return net(self());
    }

    default Seq<Building> net(Building building, Cons<PressureJunctionBuild> cons, Seq<Building> buildings) {
        for(Building b : building.proximity) {
            Building b2 = b;

            boolean jun = false;
            if(b instanceof PressureJunctionBuild bj) {
                b2 = bj.getInvert(self());
                cons.get(bj);
                jun = true;
            }

            if(sdx(b2, buildings, jun)) {
                buildings.add(b2);
                ((PressureAble) b2).net(b2, cons, buildings);
            }
        }

        return buildings;
    }

    default int tier() {
        return -1;
    }

    default boolean inNet(Building b, boolean junction) {
        return inNet(b, (PressureAble) b, junction);
    }

    default boolean online() {
        return true;
    }

    default boolean storageOnly() {
        return true;
    }

    default float pressureThread() {
        return 0;
    }

    default boolean alignX(int rotation) {
        return rotation == 0 || rotation == 2;
    }

    default boolean alignY(int rotation) {
        return rotation == 1 || rotation == 3;
    }

    default boolean inNet(Building b, PressureAble p, boolean junction) {
        if(b == null) {
            return false;
        }

        Building self = self();
        int delta = 1;
        if(junction) {
            delta++;
        }

        int tx = self.tileX();
        int ty = self.tileY();

        Tile left = world.tile(tx - delta, ty);
        Tile right = world.tile(tx + delta, ty);

        if(left.build == b || right.build == b) {
            return alignX(self.rotation) || alignX(b.rotation);
        }

        Tile top = world.tile(tx, ty + delta);
        Tile bottom = world.tile(tx, ty - delta);

        if(top.build == b || bottom.build == b) {
            return alignY(self.rotation) || alignY(b.rotation);
        }

        return p.online() && (tier() == -1 || p.tier() == tier());
    }
}