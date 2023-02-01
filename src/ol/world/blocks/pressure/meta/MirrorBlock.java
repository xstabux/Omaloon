package ol.world.blocks.pressure.meta;

import mindustry.gen.Building;
import mindustry.world.Block;

public class MirrorBlock extends Block {
    public MirrorBlock(String name) {
        super(name);

        this.destructible = true;
        this.solid = false;
    }

    public class MirrorBlockBuild extends Building {
        public Building antiNearby() {
            return switch(this.rotation) {
                case 0 -> this.nearby(2);
                case 1 -> this.nearby(3);
                case 2 -> this.nearby(0);
                case 3 -> this.nearby(1);

                //unreached
                default -> null;
            };
        }

        public boolean active() {
            return true;
        }

        public void updateNearby(Building building) {
        }

        public void updateAntiNearby(Building building) {
        }

        public void updateBoth(Building aa, Building bb) {
        }

        @Override
        public void updateTile() {
            Building aa = this.nearby(this.rotation);
            Building bb = this.antiNearby();

            if(bb != null && aa instanceof PressureAbleBuild && this.canConsume()) {
                if(bb instanceof PressureAbleBuild && this.active()) {
                    this.updateNearby(aa);
                    this.updateAntiNearby(bb);
                    this.updateBoth(aa, bb);
                }
            }
        }
    }
}