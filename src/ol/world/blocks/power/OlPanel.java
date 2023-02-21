package ol.world.blocks.power;

import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class OlPanel extends PowerGenerator {
    public OlPanel(String name){
        super(name);
        flags = EnumSet.of(BlockFlag.generator);
        envEnabled = Env.any;
    }

    public class OlPanelBuild extends GeneratorBuild {

        @Override
        public void updateTile() {

            //Check if the amount of liquid in the block is greater than 0
            if (liquids.currentAmount() > 0f) {

                //Calculate production efficiency based on the amount of liquid and lighting on the map
                productionEfficiency = enabled ? Mathf.clamp(liquids.currentAmount() / block.liquidCapacity, 0f, 1f) * (Attribute.light.env() + (state.rules.lighting ? 1f - state.rules.ambientLight.a : 1f)) : 0f;

            } else {
                //If there is no liquid in the block, set the production efficiency to 0
                productionEfficiency = 0f;
            }

            //Dump liquid to adjacent blocks of the same type if the amount of liquid in the block is greater than 0.01f
            if (liquids.currentAmount() > 0.01f) {
                dumpLiquid(liquids.current());
            }
        }


        //The amount of energy produced depends on the amount of liquid in the block
        @Override
        public float getPowerProduction() {
            return super.getPowerProduction() * efficiency;
        }

        //Dump liquid to adjacent blocks of the same type
        public void dumpLiquid(Liquid liquid) {

            //Get the number of adjacent blocks
            int dump = this.cdump;

            //If there is almost no liquid in the block, return
            if(liquids.get(liquid) <= 0.0001f) {
                return;
            }

            //Unlock the liquid if playing in campaign mode and on the default team
            if(!net.client() && state.isCampaign() && team == state.rules.defaultTeam) {
                liquid.unlock();
            }

            //Loop through adjacent blocks
            for(int i = 0; i < proximity.size; i++) {

                //Increment dump count
                incrementDump(proximity.size);

                //Get the current adjacent block
                Building other = proximity.get((i + dump) % proximity.size);

                //Check if the current adjacent block is of the same type as this block
                if(!(other.block instanceof OlPanel)) {
                    continue;
                }

                //Get the destination block for the liquid
                other = other.getLiquidDestination(self(), liquid);

                //Check if the destination block is valid and can receive the liquid
                if(other != null && other.team == team && other.block.hasLiquids && canDumpLiquid(other, liquid) && other.liquids != null) {

                    //Calculate the fraction of liquid in the destination block and this block
                    float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                    float fract = liquids.get(liquid) / block.liquidCapacity;

                    //If the destination block has less liquid than this block, transfer the liquid
                    if(ofract < fract) {
                        transferLiquid(other, fract - ofract, liquid);
                    }
                }
            }
        }
    }
}
