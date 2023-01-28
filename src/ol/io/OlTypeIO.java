package ol.io;

import arc.util.io.*;
import mindustry.annotations.Annotations.*;
import mma.io.*;
import ol.type.units.ornitopter.Blade.*;
import org.jetbrains.annotations.*;

@TypeIOHandler
public class OlTypeIO extends ModTypeIO{
    public static void writeBladeMounts(Writes write, BladeMount[] mounts){
        write.s(0);
        write.b(mounts.length);
        for(BladeMount m : mounts){
            write.f(m.bladeRotation);
            write.f(m.bladeBlurRotation);
            write.l(m.seed);
        }
    }

    public static BladeMount[] readBladeMounts(Reads read, @Nullable BladeMount[] mounts){
        read.s();
        byte len = read.b();
        boolean isNew = false;
        if(mounts == null){
            mounts = new BladeMount[len];
            isNew = true;
        }
        for(int i = 0; i < len; i++){
            float bladeRotation = read.f();
            float bladeBlurRotation = read.f();
            long seed = read.l();
            if(isNew) mounts[i] = new BladeMount(null);
            if(i <= mounts.length - 1){

                BladeMount m = mounts[i];
                m.bladeRotation = bladeRotation;
                m.bladeBlurRotation = bladeBlurRotation;
                m.seed = seed;
            }
        }

        return mounts;
    }
}
