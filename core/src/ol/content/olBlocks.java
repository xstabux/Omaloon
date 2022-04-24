package ol.content;

import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.TreeBlock;
import ol.world.blocks.defense.olColdWall;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.ctype.ContentList;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawMixer;
import mindustry.world.draw.DrawSmelter;

import static mindustry.type.ItemStack.with;

public class olBlocks implements ContentList {

	public static Block
	//Environment
	iceSpikes,
	//Defence
	omaliteAlloyWall, omaliteAlloyWallLarge,
	//Distribution
	//Drills
	//percussionTower,
	//Power
	hyperReceiver, //powerTower,
	//Production
	lowTemperatureSmelter, fuser,
	//Turrets
	bluenight,
	//Storages
	//Units
	//Ores
	oreOmalite;

        @Override
	public void load() {
		//region Environment
        iceSpikes = new TreeBlock("ice-spikes");

		//endregion
		//region Defence
		omaliteAlloyWall = new olColdWall("omalite-alloy-wall"){{
			requirements(Category.defense, ItemStack.with(olItems.omaliteAlloy, 5,Items.titanium, 10));
			size = 1;
			health = 920 * size * size;
			insulated = true;
		}};
		omaliteAlloyWallLarge = new olColdWall("omalite-alloy-wall-large"){{
			requirements(Category.defense, ItemStack.with(olItems.omaliteAlloy, 50,Items.titanium, 110));
			size = 2;
			health = 1840 * size * size;
			insulated = true;
		}};
		//endregion
		//region Production
		lowTemperatureSmelter = new GenericCrafter("low-temperature-smelter"){{
			size = 3;
			health = 540;
			requirements(Category.crafting, ItemStack.with(olItems.omalite, 80,Items.thorium, 80,Items.titanium, 100));
			craftTime = 350f;
			craftEffect = Fx.shieldBreak;
			updateEffect = new MultiEffect(
					Fx.pulverize,
					Fx.hitLancer
			);
			updateEffectChance = 0.08f;
			ambientSound = Sounds.cutter;
			ambientSoundVolume = 0.4f;
			drawer = new DrawSmelter(Color.valueOf("abcdef"));
			consumes.power(5);
			consumes.items(with(Items.titanium, 4, olItems.omalite, 2));
			outputItems = with(olItems.omaliteAlloy, 6);
			itemCapacity = 30;
		}};

		fuser = new GenericCrafter("fuser") {
			{
				requirements(Category.crafting, with(Items.surgeAlloy, 20, olItems.omalite, 50, Items.titanium, 80, Items.thorium, 65));
				craftEffect = Fx.freezing;
				updateEffect = Fx.freezing;
				updateEffectChance = 0.08f;
				ambientSound = Sounds.mineDeploy;
				ambientSoundVolume = 0.6f;
				outputLiquid = new LiquidStack(olLiquids.liquidOmalite, 16f);
				craftTime = 150;
				size = 3;
				drawer = new DrawMixer(true);
				itemCapacity = 30;
				liquidCapacity  = 30;
				hasPower = hasLiquids = hasItems = true;
				consumes.liquid(Liquids.cryofluid,0.15f);
				consumes.items(new ItemStack(olItems.omalite, 5));
				consumes.power(4f);
			}
		};

		//endregion
		//region Turrets
			bluenight = new PowerTurret("blue-night"){{
			    requirements(Category.turret, with(Items.copper, 20, Items.lead, 50, Items.graphite, 20, olItems.omaliteAlloy, 25, Items.silicon, 15));
				size = 3;
				range = 175f;
				recoilAmount = 2f;
				inaccuracy = 3f;
				rotateSpeed = 6f;
				shootCone = 3f;
				shootSound = Sounds.laser;
				ammoUseEffect = Fx.casing1;
				targetAir = true;
				shootType = olBullets.blueSphere;
				chargeTime = 30f;
				chargeMaxDelay = 30f;
				chargeSound = Sounds.lasercharge2;
				powerUse = 3f;
				chargeEffect = olFx.blueSphere;
				smokeEffect = Fx.none;
		}};
		//endregion
		//region Ores
		oreOmalite = new OreBlock("omalite-ore"){{
			oreDefault = true;
			variants = 3;
			oreThreshold = 0.95F;
			oreScale = 20.380953F;
			itemDrop = olItems.omalite;
			localizedName = itemDrop.localizedName;
			mapColor.set(itemDrop.color);
			useColor = true;
		}};
		//endregion
		//region Power
		hyperReceiver = new SolarGenerator("hyper-receiver"){{
			requirements(Category.power, with(Items.titanium, 200, Items.surgeAlloy, 110, olItems.omaliteAlloy, 40));
			size = 4;
			powerProduction = 3f;
		}};
		/*powerTower = new olPowerNode("power-tower"){{
        }};*/

//endregion
//region Drills
		/*percussionTower = new PercussionTower("percussion-tower"){{
		}};*/
//endregion
   }
}
