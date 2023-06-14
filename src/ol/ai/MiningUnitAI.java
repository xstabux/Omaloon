package ol.ai;

import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;
import ol.entity.MiningUnitEntity;
import ol.world.blocks.storage.MiningUnloadPoint;

import static mindustry.Vars.*;

public class MiningUnitAI extends AIController {
    public Tile target;

    public boolean isMining() {
        return !entity().isEject();
    }

    public MiningUnitEntity entity() {
        return (MiningUnitEntity) unit;
    }

    public Building findUnloadBuilding(float range) {
        return indexer.findTile(unit.team, unit.x, unit.y, range, build -> {
            return build instanceof MiningUnloadPoint.MiningUnloadPointBuild b && b.canAcceptItem(unit.stack.item);
        });
    }

    @Override
    public void updateMovement() {
        if (!entity().isBlock()) {
            if (target != null) {
                if (isMining()) {
                    if (unit.tileOn() == target) {
                        target.setNet(entity().getDrill(), unit.team, 0);
                        entity().instance = (Drill.DrillBuild) target.build;
                    } else {
                        moveTo(target, 0);
                    }
                } else {
                    Building unloadBuilding = findUnloadBuilding(8);
                    if (unloadBuilding != null) {
                        entity().unloadTo((MiningUnloadPoint.MiningUnloadPointBuild) unloadBuilding);
                        if (!entity().isEject()) {
                            target = null;
                        }
                    } else {
                        moveTo(target, 4);
                    }
                }
            } else {
                Building core = unit.closestCore();
                if (core != null) {
                    moveTo(core, 40);
                }
            }
        } else if (target.build != entity().instance) {
            entity().instance = null;
        } else if (entity().isFull()) {
            entity().eject();
            target = null;
        }
    }

    @Override
    public void updateTargeting() {
        if (!entity().isBlock()) {
            Drill drill = entity().getDrill();
            if (isMining()) {
                if (target == null) {
                    for (Item item : content.items()) {
                        target = indexer.findClosestOre(unit, item);
                        if (target != null && target.build == null && drill.canMine(target)) {
                            break;
                        } else {
                            target = null;
                        }
                    }
                } else if (target.build != null || !drill.canMine(target)) {
                    target = null;
                }
            } else if (target == null) {
                Building unloadBuilding = findUnloadBuilding(9000);
                if (unloadBuilding != null) {
                    target = unloadBuilding.tile;
                }
            } else if (!(target.build instanceof MiningUnloadPoint.MiningUnloadPointBuild)
                    || target.team() != unit.team()) {
                target = null;
            } else if (!((MiningUnloadPoint.MiningUnloadPointBuild) target.build)
                    .canAcceptItem(unit.stack.item)) {
                target = null;
            }
        }
    }
}