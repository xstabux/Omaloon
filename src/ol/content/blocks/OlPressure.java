package ol.content.blocks;

import arc.util.OS;
import mindustry.entities.effect.RadialEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.*;

import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawFrames;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.BuildVisibility;
import ol.content.OlFx;
import ol.content.OlLiquids;
import ol.world.blocks.crafting.PressureCrafter;
import ol.world.blocks.pressure.*;
import ol.world.blocks.sandbox.SandboxCompressor;
import org.jetbrains.annotations.NotNull;

import static mindustry.Vars.tilesize;

public class OlPressure {
    public static Block
            //compressors
            mechanicalCompressor,
            electricCompressor,
            improvedCompressor,
            sandboxCompressor,
            //pipes
            pressurePipe,
            improvedPressurePipe,
            reinforcedPressurePipe,
            sandboxPressurePipe,
            //releasers,
            pressureReleaser,
            //counters
            pressureCounter,
            improvedPressureCounter,
            reinforcedPressureCounter,
            //other
            pressureJunction,
            //bridges
            pressureBridge,
            improvedPressureBridge,
            reinforcedPressureBridge,
            sandboxPressureBridge,
            //levelers
            pressureLeveler,
            //smaller
            pressureSmaller,
    end;

    public static void load(){

        //compressors
        mechanicalCompressor = new PressureCrafter("mechanical-compressor") {{
            tmp1(this);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawFrames(){{
                        frames = 3;
                        interval = 5f;
                    }}
            );

            ambientSound = Sounds.none;
            pressureProduce = 5;
            maxPressure = 50;
            showPressure = true;
            hasItems = false;
            hasLiquids= false;
            tier = 1;
        }};

        electricCompressor = new PressureCrafter("electric-compressor") {{
            tmp1(this);

            craftEffect = new RadialEffect() {{
                effect = OlFx.psh;
                layer = Layer.blockUnder;
                amount = 4;
                lengthOffset = 8;
            }};

            drawer = new DrawMulti(
                    new DrawDefault()
            );

            craftTime = 100f;
            ambientSound = Sounds.none;
            pressureProduce = 15;

            consumePower(2f);

            maxPressure = 50;
            showPressure = true;
            hasItems = false;
            hasLiquids= false;

            size = 2;
            tier = 1;
        }};

        improvedCompressor = new PressureCrafter("improved-compressor"){{
            tmp1(this);

            drawer = new DrawMulti(
                    new DrawDefault()
            );

            craftTime = 100f;
            ambientSound = Sounds.none;
            pressureProduce = 25;

            consumePower(2.6f);
            consumeLiquid(OlLiquids.angeirum, 14f/60f);

            maxPressure = 125;
            showPressure = true;

            size = 2;
            tier = 2;
        }};

        sandboxCompressor = new SandboxCompressor("sandbox-compressor") {{
            requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
            maxPressure = 1000;
        }};

        //end compressors
        //pipes

        pressurePipe = new PressurePipe("pressure-pipe") {{
            tmp1(this);
            maxPressure = 50;
            tier = 1;
        }};

        improvedPressurePipe = new PressurePipe("improved-pressure-pipe"){{
            tmp1(this);
            maxPressure = 125;
            tier = 2;
        }};

        reinforcedPressurePipe = new PressurePipe("reinforced-pressure-pipe"){{
            tmp1(this);
            maxPressure = 240;
            tier = 3;
        }};

        sandboxPressurePipe = new PressurePipe("sandbox-pressure-pipe") {{
            this.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
            this.bridgeReplacement = OlPressure.sandboxPressureBridge;
            this.maxPressure = Float.POSITIVE_INFINITY;
            this.dangerPressure = -1;
            this.canExplode = false;
        }};

        //end pipes
        //releasers

        pressureReleaser = new PressureReleaser("pressure-releaser") {{
            tmp1(this);
            this.dangerPressure = 44;
            this.maxPressure = 50;
            this.tier = 1;
        }};

        //end releasers
        //counters

        pressureCounter = new PressureCounter("pressure-counter") {{
            tmp1(this);
            maxPressure = 50;
            dangerPressure = 44;
            tier = 1;
        }};

        improvedPressureCounter = new PressureCounter("improved-pressure-counter"){{
            tmp1(this);
            maxPressure = 125;
            dangerPressure = 119;
            tier = 2;
        }};

        reinforcedPressureCounter = new PressureCounter("reinforced-pressure-counter"){{
            tmp1(this);
            maxPressure = 240;
            dangerPressure = 234;
            tier = 3;
        }};

        //end counters
        //other

        pressureJunction = new PressureJunction("pressure-junction") {{
            tmp1(this);
        }};

        //end other
        //bridges

        pressureBridge = new PressureBridge("pressure-bridge") {{
            tmp1(this);
            maxPressure = 50;
            range = 5.0f*tilesize;
            tier = 1;
        }};

        improvedPressureBridge = new PressureBridge("improved-pressure-bridge"){{
            tmp1(this);
            maxPressure = 125;
            range = 5.0f*tilesize;
            tier = 2;
        }};

        reinforcedPressureBridge = new PressureBridge("reinforced-pressure-bridge"){{
            tmp1(this);
            maxPressure = 240;
            range = 5.0f*tilesize;
            tier = 3;
        }};

        sandboxPressureBridge = new PressureBridge("sandbox-pressure-bridge") {{
            this.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
            this.maxPressure = Float.POSITIVE_INFINITY;
            this.dangerPressure = -1;
            this.canExplode = false;

            this.range = 7.0f*tilesize;
        }};

        //end bridges
        //levelers

        pressureLeveler = new PressureLeveler("pressure-leveler") {{
            tmp1(this);
            this.consumePower(1);
        }};

        //end levelers
        //smallers

        pressureSmaller = new PressureSmaller("pressure-smaller") {{
            tmp1(this);

            this.consumePower(0.5f);
            this.consumeLiquid(OlLiquids.angeirum, 0.4f);
        }};

        //end smallers
    }

    public static void tmp1(@NotNull Block block) {
        block.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
    }
}
