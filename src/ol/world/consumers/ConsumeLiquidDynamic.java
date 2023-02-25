package ol.world.consumers;

import arc.func.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.consumers.Consume;
import ol.ui.LiquidImage;

public class ConsumeLiquidDynamic extends Consume {
    public final Func<Building, LiquidStack[]> liquids;

    @SuppressWarnings("unchecked")
    public <T extends Building> ConsumeLiquidDynamic(Func<T, LiquidStack[]> liquids){
        this.liquids = (Func<Building, LiquidStack[]>)liquids;
    }

    @Override
    public void apply(Block block){
        block.hasLiquids = true;
    }

    @Override
    public void build(Building build, Table table){
        LiquidStack[][] current = {liquids.get(build)};

        table.table(cont -> {
            table.update(() -> {
                if(current[0] != liquids.get(build)){
                    rebuild(build, cont);
                    current[0] = liquids.get(build);
                }
            });

            rebuild(build, cont);
        });
    }

    private void rebuild(Building build, Table table){
        table.clear();
        int i = 0;

        for(LiquidStack liquidStack : liquids.get(build)){
            table.add(new ReqImage(new LiquidImage(liquidStack.liquid.uiIcon, Math.round(liquidStack.amount * multiplier.get(build))),
                    () -> build.liquids != null && build.liquids.get(liquidStack.liquid) >= Math.round(liquidStack.amount * multiplier.get(build)))).padRight(8).left();
            if(++i % 4 == 0) table.row();
        }
    }

    @Override
    public void trigger(Building build){
        for(LiquidStack liquidStack : liquids.get(build)){
            build.liquids.remove(liquidStack.liquid, Math.round(liquidStack.amount * multiplier.get(build)));
        }
    }

    @Override
    public float efficiency(Building build){
        for(LiquidStack liquidStack : liquids.get(build)){
            if(build.liquids.get(liquidStack.liquid) < Math.round(liquidStack.amount * multiplier.get(build))) {
                return 0f;
            }
        }
        return 1f;
    }
}
