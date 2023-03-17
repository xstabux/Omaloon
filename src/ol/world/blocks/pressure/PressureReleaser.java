package ol.world.blocks.pressure;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;

import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;

import ol.utils.Angles;
import ol.world.blocks.pressure.meta.PressureAbleBuild;
import org.jetbrains.annotations.NotNull;

public class PressureReleaser extends PressurePipe {
    public float releasePower = 2.5f;

    public PressureReleaser(String name) {
        super(name);

        this.mapDraw = true;
    }

    @Override public boolean inBuildPlanNet(@NotNull BuildPlan s, int x, int y, int ignored) {
        int ox = s.x - x;
        int oy = s.y - y;

        if(ox == 0 && oy == 0) {
            return true;
        }

        ox = ox < 0 ? -ox : ox;
        return (ox == 1) ? Angles.alignX(s.rotation) : Angles.alignY(s.rotation);
    }

    public class PressureReleaserBuild extends PressurePipeBuild {
        public boolean opened = false;
        public boolean auto = false;
        public int needAngle = 0;

        public int angle = switch(this.rotation) {
            case 0, 3 -> 0;
            case 1, 2 -> 180;

            //unreachable
            default -> throw new RuntimeException();
        };

        @Override public void updateTile() {
            super.updateTile();

            if(this.isDanger()) {
                if(this.pressure() > 0 && Math.floor(Time.globalTime) % 5 == 0) {
                    this.pressureModule.pressure -= releasePower;
                }

                this.needAngle = switch(this.rotation) {
                    case 0, 1 -> -90;
                    case 2, 3 -> 90;

                    //unreachable
                    default -> throw new RuntimeException();
                };
            } else {
                needAngle = switch(this.rotation) {
                    case 0, 3 -> 0;
                    case 1, 2 -> 180;

                    //unreachable
                    default -> throw new RuntimeException();
                };
            }

            if(angle < needAngle) {
                angle++;
            }

            if(angle > needAngle) {
                angle--;
            }
        }

        @Override public void draw() {
            super.draw();

            Draw.rect(Core.atlas.find(block.name + "-valve"), x, y, angle + this.drawrot());
        }

        @Override public void write(Writes write) {
            super.write(write);
            write.bool(this.opened);
            write.bool(this.auto);
        }

        @Override public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.opened = read.bool();
            this.auto = read.bool();
        }

        @Override public boolean inNet(Building b, PressureAbleBuild p, boolean junction) {
            return this.COUNTER_IN_NET_CALL(b, p, junction);
        }
    }
}