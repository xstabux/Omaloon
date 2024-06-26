package omaloon.world.blocks.liquid;

import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.liquid.*;
import omaloon.world.interfaces.*;
import omaloon.world.meta.*;
import omaloon.world.modules.*;

public class PressureLiquidJunction extends LiquidJunction {
	public PressureConfig pressureConfig = new PressureConfig();

	public PressureLiquidJunction(String name) {
		super(name);
	}

	public class PressureLiquidJunctionBuild extends LiquidJunctionBuild implements HasPressure {
		PressureModule pressure = new PressureModule();

		@Override public boolean acceptLiquid(Building source, Liquid liquid) {
			return false;
		}
		@Override public boolean acceptsPressure(HasPressure from, float pressure) {
			return false;
		}

		@Override
		public boolean connects(HasPressure to) {
			return HasPressure.super.connects(to) && !(to instanceof PressureLiquidPump);
		}

		@Override
		public HasPressure getPressureDestination(HasPressure source, float pressure) {
			if(!enabled) return this;

			int dir = (source.relativeTo(tile.x, tile.y) + 4) % 4;
			HasPressure next = nearby(dir) instanceof HasPressure ? (HasPressure) nearby(dir) : null;
			if(next == null || (!next.acceptsPressure(source, pressure) && !(next.block() instanceof PressureLiquidJunction))){
				return this;
			}
			return next.getPressureDestination(this, pressure);
		}

		@Override
		public Seq<HasPressure> nextBuilds(boolean flow) {
			return Seq.with();
		}

		@Override public PressureModule pressure() {
			return pressure;
		}
		@Override public PressureConfig pressureConfig() {
			return pressureConfig;
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
