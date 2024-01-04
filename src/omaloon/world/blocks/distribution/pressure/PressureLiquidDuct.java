package omaloon.world.blocks.distribution.pressure;

import arc.graphics.g2d.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.liquid.*;
import omaloon.utils.*;
import omaloon.world.blocks.meta.*;
import omaloon.world.interfaces.*;
import omaloon.world.modules.*;

public class PressureLiquidDuct extends LiquidRouter {
	public PressureConfig pressureConfig = new PressureConfig();

	public TextureRegion[] topRegions;

	public PressureLiquidDuct(String name) {
		super(name);
		rotate = true;
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	public void load() {
		super.load();
		topRegions = OlUtils.split(name + "-tiles", 32, 0);
	}

	public class PressureLiquidDuctBuild extends LiquidRouterBuild implements HasPressure {
		public int tiling = 0;
		PressureModule pressure = new PressureModule();


		@Override public boolean canDumpLiquid(Building to, Liquid liquid) {
			return
			(to == front() || to == back() || to instanceof PressureLiquidDuctBuild)
			&& to instanceof HasPressure toPressure && canDumpPressure(toPressure, 0);
		}

		@Override
		public void draw() {
			Draw.rect(bottomRegion, x, y);
			if (liquids().currentAmount() > 0.01f) {
				Draw.color(liquids.current().color);
				Draw.rect(liquidRegion, x, y);
				Draw.color();
			}
			Draw.rect(topRegions[tiling], x, y);
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			tiling = 0;
			for (int i = 0; i < 4; i++) {
				Building build = nearby(i);
				if (
					build instanceof PressureLiquidDuctBuild
				) tiling |= (1 << i);
			}
			tiling |= (1 << rotation);
		}

		@Override public PressureModule pressure() {
			return pressure;
		}
		@Override public PressureConfig pressureConfig() {
			return pressureConfig;
		}

		@Override
		public void updateTile() {
			super.updateTile();
			dumpPressure();
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			pressure.read(read);
		}
		@Override
		public void write(Writes write) {
			super.write(write);
			pressure.write(write);
		}
	}
}
