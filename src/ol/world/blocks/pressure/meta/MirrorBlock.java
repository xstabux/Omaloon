package ol.world.blocks.pressure.meta;

import arc.ApplicationListener;
import arc.Core;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.BlockStatus;

import ol.utils.OlMapInvoker;

public class MirrorBlock extends Block {
    public MirrorBlock(String name) {
        super(name);

        this.destructible = true;
        this.solid = false;

        this.rotate = true;
        this.quickRotate = true;
        this.rotateDraw = false;
    }

    //TODO fix why updateTile() don`t calls
    public static void loadListener() {
        Core.app.addListener(new ApplicationListener() {
            @Override
            public void update() {
                if(Vars.world == null || Vars.state == null || Vars.world.tiles == null) {
                    return;
                }

                if(Vars.state.isPaused() || Vars.state.isEditor()) {
                    return;
                }

                if(!Vars.state.isPlaying()) {
                    return;
                }

                OlMapInvoker.eachBuild(building -> {
                    if(building instanceof MirrorBlockBuild) {
                        ((MirrorBlockBuild) building).updateChildren();
                    }
                });
            }
        });
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

        public void updateChildren() {
            Building aa = this.nearby(this.rotation);
            Building bb = this.antiNearby();

            if(bb != null) {
                if(aa instanceof PressureAbleBuild) {
                    if(this.canConsume()) {
                        if(bb instanceof PressureAbleBuild) {
                            if(this.active()) {
                                this.updateNearby(aa);
                                this.updateAntiNearby(bb);
                                this.updateBoth(aa, bb);
                                //Log.info("all is ok");
                            } else {
                                //Log.info("protocol 0");
                            }
                        } else {
                            //Log.info("protocol 1");
                        }
                    } else {
                        //Log.info("protocol 2");
                    }
                } else {
                    //Log.info("protocol 3");
                }
            } else {
                //Log.info("protocol 4");
            }
        }

        @Override
        public BlockStatus status() {
            return this.canConsume() ? BlockStatus.noInput : BlockStatus.active;
        }

        @Override
        public boolean canConsume() {
            for(Consume consume : consumers) {
                if(consume.efficiency(this) == 0) {
                    return false;
                }
            }

            return true;
        }
    }
}