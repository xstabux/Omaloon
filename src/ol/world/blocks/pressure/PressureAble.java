package ol.world.blocks.pressure;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.Tile;

import static mindustry.Vars.*;

public interface PressureAble {
    Seq<Building> proximity();
    Building self();

    float pressure();
    void pressure(float pressure);

    default Seq<Building> net(Building source) {
        Seq<Building> buildings = new Seq<>();

        for(Building b : proximity()) {
            if(b != source && b instanceof PressureAble p && inNet(b, p)) {
                buildings.add(b);

                buildings.addAll(p.net(self()));
            }
        }

        return buildings;
    }

    default boolean alignX(int rotation) {
        return rotation == 0 || rotation == 2;
    }

    default boolean alignY(int rotation) {
        return rotation == 1 || rotation == 3;
    }

    default boolean inNet(Building b, PressureAble p) {
        Building self = self();

        int tx = self.tileX();
        int ty = self.tileY();

        Tile left = world.tile(tx - 1, ty);
        Tile right = world.tile(tx + 1, ty);

        if(left.build == b || right.build == b) {
            return alignX(self.rotation) || alignX(b.rotation);
        }

        Tile top = world.tile(tx, ty + 1);
        Tile bottom = world.tile(tx, ty - 1);

        if(top.build == b || bottom.build == b) {
            return alignY(self.rotation) || alignY(b.rotation);
        }

        return false;
    }
}