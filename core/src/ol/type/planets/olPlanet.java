package ol.type.planets;

import mindustry.game.Waves;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

public class olPlanet extends Planet{
    public Waves waves;

    public olPlanet(String name, Planet parent, float radius){
        super(name, parent, radius);
    }

    public olPlanet(String name, Planet parent, float radius, int sectorSize){
        super(name, parent, radius, 0);
        if(sectorSize > 0){
            grid = PlanetGrid.create(sectorSize);

            sectors.ensureCapacity(grid.tiles.length);
            for(int i = 0; i < grid.tiles.length; i++){
                sectors.add(new Sector(this, grid.tiles[i]));
            }

            sectorApproxRadius = sectors.first().tile.v.dst(sectors.first().tile.corners[0].v);
        }
    }

    @Override
    public void init(){
        if(waves == null){
            waves = new Waves();
        }
        super.init();
    }
}
