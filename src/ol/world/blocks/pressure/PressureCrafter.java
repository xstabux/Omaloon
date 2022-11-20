package ol.world.blocks.pressure;

import mindustry.gen.Building;
import ol.world.blocks.crafting.OlCrafter;

public class PressureCrafter extends OlCrafter {
    //how many pressure crafter consumes
    public float pressureConsume;

    //how many pressure crafter
    public float pressureProduce;

    public PressureCrafter(String name) {
        super(name);
    }

    public class PressureCrafterBuild extends OlCrafter.olCrafterBuild implements PressureAble {
        public float pressure;
        public float effect;

        @Override
        @SuppressWarnings("unchecked")
        public PressureCrafterBuild self() {
            return this;
        }

        @Override
        public float pressure() {
            return pressure;
        }

        @Override
        public void pressure(float pressure) {
            this.pressure = pressure;
        }

        @Override
        public boolean online() {
            return pressureConsume > 0 || pressureProduce > 0;
        }

        @Override
        public boolean storageOnly() {
            return !(pressureConsume > 0);
        }

        @Override
        public float pressureThread() {
            return pressureProduce * (effect / 100);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(canConsume() && effect < 100) {
                effect++;
                if(effect > 100) {
                    effect = 100;
                }
            } else {
                if(!canConsume() && effect > 0) {
                    effect--;
                    if(effect < 0) {
                        effect = 0;
                    }
                }
            }
        }
    }
}