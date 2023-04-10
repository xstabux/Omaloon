/*package ol.world.blocks.rails;

import arc.*;
import arc.graphics.*;

import mma.type.formloaders.*;
import mma.world.blocks.*;

public class RailBlock extends CustomShapeBlock {
    public RailBlock(String name){
        super(name);
        rotate = true;
    }
    @Override
    public void load(){
        super.load();
        Pixmap pixmap = Core.atlas.getPixmap(region).crop();
        SpriteShapeLoader loader = new SpriteShapeLoader(32, new SpriteShapeLoader.ChunkProcessor.PercentProcessor(
                0.25f, pixmap.width / 64, pixmap.height / 64
        ));
        loader.load(pixmap);
        customShape = loader.toShape();
    }
}*/
