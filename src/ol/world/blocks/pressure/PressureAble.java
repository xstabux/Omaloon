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

    default Seq<Building> net(Building source) {
        return net(source, j -> {});
    }

    default Seq<Building> net(Building source, Cons<PressureJunctionBuild> cons) {
        Seq<Building> buildings = new Seq<>();

        for(Building b : proximity()) {
            Building b2 = b;

            boolean jun = false;
            if(b instanceof PressureJunctionBuild bj) {
                b2 = bj.getInvert(self());
                cons.get(bj);
                jun = true;
            }

            if(b2 != source && b2 instanceof PressureAble p && inNet(b2, p, jun) && !buildings.contains(b2)) {
                buildings.add(b2);

                buildings.addAll(p.net(self(), cons));
            }
        }

        return buildings;
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

        return p.online();
    }
}