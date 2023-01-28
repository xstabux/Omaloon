package ol.world.blocks.defense;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;
import ol.graphics.*;
import ol.world.meta.*;

import static mindustry.Vars.world;

public class OlJoinWall extends OlWall{
    protected static final IntSet tmpBlockSet = new IntSet();
    protected final static Queue<OlJoinWallBuild> doorQueue = new Queue<>();
    /** percent of damage will the nearby walls receive. 0-0%, 1-100% */
    public float damageScl = 0.5f;
    /** (int) radius in which the walls receive damage */
    public int damageRadius = 3;

    TextureRegion[] joins;

    public OlJoinWall(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();

        joins = OlGraphics.getRegions(Core.atlas.find(name + "-joins"), 12, 4, 32);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.addPercent(OlStat.damageSpread, 1 * damageScl);
    }

    @Override
    public void drawBase(Tile tile){
        Tile[][] grid = new Tile[3][3];

        int avail = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                grid[i][j] = world.tile(i + tile.x - 1, j + tile.y - 1);

                if(grid[i][j] != null){
                    avail++;
                }
            }
        }

        int index = OlGraphics.getTilingIndex(grid, 1, 1, t -> t != null && t.block() == OlJoinWall.this);
        Draw.rect(avail == 0 ? region : joins[index], tile.worldx(), tile.worldy());
    }

    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name, name)};
    }

    public class OlJoinWallBuild extends OlWallBuild{

        int totalNeighbors;

        @Override
        public void updateTile(){
            super.updateTile();
        }

        public void damageChain(int length, float amount, IntSet visited){

            float neighborDamage = (damageScl) * amount / totalNeighbors;
            float selfDamage=amount-amount*damageScl;
            if(length >= damageRadius){
                neighborDamage=0f;
                selfDamage=amount;

            }
            super.damage(selfDamage);
//            Log.info(length);
            if(Mathf.zero(neighborDamage)){
                return;
            }

            for(Building b : proximity){
                if(b instanceof OlJoinWallBuild w && b.block == block && visited.add(w.pos())){
                    w.damageChain(length + 1, neighborDamage, visited);
                }
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            totalNeighbors = proximity.count(it -> it.block == block);
        }

        @Override
        public void damage(float damage){
            if (tmpBlockSet.size!=0){
                super.damage(damage);
                return;
            }
            tmpBlockSet.add(pos());
            damageChain(1, damage, tmpBlockSet);
            tmpBlockSet.clear();
        }
    }
}