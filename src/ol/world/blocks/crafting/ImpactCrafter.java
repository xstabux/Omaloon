package ol.world.blocks.crafting;

import arc.*;
import arc.graphics.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

public class ImpactCrafter extends PressureCrafter{
    public static float startAcceleration = 0.000001F;
    public Effect stopEffect = Fx.none;
    public float deadlineTime = 3F;
    public float accelerationSpeed;
    public float decelerationSpeed;
    public float powerProduction;

    public ImpactCrafter(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction * 60.0f, StatUnit.powerSecond);
    }

    @Override
    public void setBars() {
        super.setBars();

        this.addBar("acceleration", (ImpactCrafterBuild building) -> new Bar(
                () -> Core.bundle.format("bar.acceleration") + " " + Math.round(building.getAcceleration() * 100F) + "%",
                () -> Color.orange,
                building::getAcceleration
        ));
    }

    public class ImpactCrafterBuild extends PressureCrafterBuild {
        private float acceleration = startAcceleration;
        private boolean deadDisabled = false;
        private float deadlineTimer = 0F;

        @Override
        public void craft() {
            super.craft();
            this.setAcceleration(startAcceleration);

            this.setDeadlineTimer(getDeadlineTime());
            stopEffect.at(this);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(getAcceleration());
            write.f(getDeadlineTimer());
            write.bool(isDeadDisabled());
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            setAcceleration(read.f());
            setDeadlineTimer(read.f());
            setDeadDisabled(read.bool());
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public float getPowerProduction() {
            return powerProduction * acceleration * super.efficiency();
        }

        public float getAccelerationHandler() {
            if(canConsume() && enabled()) {
                return accelerationSpeed *
                        (getAcceleration() * 2F + 1F) * efficenty() * super.efficiency();
            }

            return -decelerationSpeed;
        }

        @Override
        public float handleProgress() {
            return getAccelerationHandler();
        }

        public float getEnergy() {

            float timer = getDeadlineTimer() / getDeadlineTime();
            timer = 1 - timer;

            if(timer > 0.3) {
                return 0;
            }

            return 1f - (timer / 0.3f);
        }

        @Override
        public float handleTotalProgress() {
            return enabled() ? delta() * (acceleration * 2) : getEnergy() * delta();
        }

        @Override
        public void updateTile() {
            if(getDeadlineTimer() > 0F) {
                enabled(false);
                setDeadDisabled(true);

                setDeadlineTimer(getDeadlineTimer() - 1F);
            }

            if(getDeadlineTimer() <= 0F && isDeadDisabled()) {
                enabled(true);
                setDeadDisabled(false);
            }

            float accelerationHandler = getAccelerationHandler();
            scaleAcceleration(accelerationHandler);

            if(getAcceleration() < 0F) {
                setAcceleration(0F);
            }

            super.updateTile();

            progress = getAcceleration();
        }

        public void scaleAcceleration(float scale) {
            setAcceleration(getAcceleration() + scale);
        }

        public boolean isDeadDisabled() {
            return deadDisabled;
        }

        public float getAcceleration() {
            return acceleration;
        }

        public float getDeadlineTimer() {
            return deadlineTimer;
        }

        public void setAcceleration(float acceleration) {
            this.acceleration = acceleration;
        }

        public void setDeadlineTimer(float deadlineTimer) {
            this.deadlineTimer = deadlineTimer;
        }

        public void setDeadDisabled(boolean deadDisabled) {
            this.deadDisabled = deadDisabled;
        }

        @Override
        public float ambientVolume(){
            return acceleration * 2;
        }
    }

    public float getDeadlineTime() {
        return deadlineTime * 100;
    }
}