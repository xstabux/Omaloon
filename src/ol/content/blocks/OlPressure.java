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
            //tier 1
            pressurePipe,
            pressureReleaser,
            pressureCounter,
            pressureBridge,
            pressureSmaller,
            //tier 2
            improvedPressurePipe,
            improvedPressureReleaser,
            improvedPressureCounter,
            improvedPressureBridge,
            //tier 3
            reinforcedPressurePipe,
            reinforcedPressureReleaser,
            reinforcedPressureCounter,
            reinforcedPressureBridge,
            //sandbox
            /*
            sandboxPressurePipe,
            sandboxPressureCounter,
            sandboxPressureBridge,
            */
            sandboxCompressor,
            //other
            pressureJunction,
            pressureLeveler,
    end;

    /*temporary*/
    public static void tmp1(@NotNull Block block) {
        block.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
    }

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
            drawer = new DrawMulti(new DrawDefault());
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
            drawer = new DrawMulti(new DrawDefault());
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

        //end compressors
        //tier 1
        pressurePipe = new PressurePipe("pressure-pipe") {{
            tmp1(this);
            maxPressure = 50;
            tier = 1;
        }};

        pressureReleaser = new PressureReleaser("pressure-releaser") {{
            tmp1(this);
            dangerPressure = 42;
            releasePower = 0.5f;
            maxPressure = 50;
            releaseEffect = new RadialEffect() {{
                effect = OlFx.release;
                amount = 2;
                rotationSpacing = 180;
                lengthOffset = 2;
            }};
            tier = 1;
        }};

        pressureCounter = new PressureCounter("pressure-counter") {{
            tmp1(this);
            maxPressure = 50;
            dangerPressure = 44;
            tier = 1;
        }};

        pressureBridge = new PressureBridge("pressure-bridge") {{
            tmp1(this);
            maxPressure = 50;
            range = 5.0f*tilesize;
            tier = 1;
        }};

        pressureSmaller = new PressureSmaller("pressure-smaller") {{
            tmp1(this);
            tier = 1;
            consumePower(1);
        }};

        //tier 2
        improvedPressurePipe = new PressurePipe("improved-pressure-pipe"){{
            tmp1(this);
            maxPressure = 125;
            tier = 2;
        }};

        improvedPressureReleaser = new PressureReleaser("improved-pressure-releaser"){{
            tmp1(this);
            dangerPressure = 119;
            releasePower = 0.5f;
            maxPressure = 125;
            releaseEffect = new RadialEffect() {{
                effect = OlFx.improvedRelease;
                amount = 2;
                rotationSpacing = 180;
                lengthOffset = 2;
            }};
            tier = 2;
        }};

        improvedPressureCounter = new PressureCounter("improved-pressure-counter"){{
            tmp1(this);
            maxPressure = 125;
            dangerPressure = 119;
            tier = 2;
        }};

        improvedPressureBridge = new PressureBridge("improved-pressure-bridge"){{
            tmp1(this);
            maxPressure = 125;
            range = 5.0f*tilesize;
            tier = 2;
        }};

        //tier 3
        reinforcedPressurePipe = new PressurePipe("reinforced-pressure-pipe"){{
            tmp1(this);
            maxPressure = 240;
            tier = 3;
        }};

        reinforcedPressureCounter = new PressureCounter("reinforced-pressure-counter"){{
            tmp1(this);
            maxPressure = 240;
            dangerPressure = 234;
            tier = 3;
        }};

        reinforcedPressureBridge = new PressureBridge("reinforced-pressure-bridge"){{
            tmp1(this);
            maxPressure = 240;
            range = 5.0f*tilesize;
            tier = 3;
        }};

        //sandbox
        sandboxCompressor = new SandboxCompressor("sandbox-compressor") {{
            requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
            maxPressure = 1000;
        }};

        /*
        sandboxPressurePipe = new PressurePipe("sandbox-pressure-pipe") {{
            requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
            bridgeReplacement = OlPressure.sandboxPressureBridge;
            maxPressure = Float.POSITIVE_INFINITY;
            dangerPressure = -1;
            canExplode = false;
        }};

        sandboxPressureCounter = new PressureCounter("sandbox-pressure-counter") {{
            requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
            bridgeReplacement = OlPressure.sandboxPressureBridge;
            maxPressure = 512;
            dangerPressure = Float.POSITIVE_INFINITY;
            canExplode = false;
        }};

        sandboxPressureBridge = new PressureBridge("sandbox-pressure-bridge") {{
            requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
            maxPressure = Float.POSITIVE_INFINITY;
            dangerPressure = -1;
            canExplode = false;
            range = 7.0f*tilesize;
        }};
        */

        //end tiers
        //other
        pressureJunction = new PressureJunction("pressure-junction") {{
            tmp1(this);
        }};

        pressureLeveler = new PressureLeveler("pressure-leveler") {{
            tmp1(this);
            size = 2;
            liquidCapacity = 10;
            consumePower(0.5f);
            liquidConsumption = 4f/60f;
            hasLiquids = true;
        }};
        //end other
    }
}
