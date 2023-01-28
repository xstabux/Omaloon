package ol.tools;

import arc.*;
import arc.backend.sdl.SdlApplication.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.util.*;
import ol.tools.shapes.*;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class IconRasterizer{
    Graphics2D out = null;
    BufferedImage outImage = null;
    float width, height;
    private float scl,cellSize;

    public static void main(String[] inputArgs){
        try{
            executeMain(inputArgs);
        }catch(SdlError error){
            Log.warn("Cannot run " + IconRasterizer.class.getSimpleName() + ", reason: @", Strings.getStackTrace(error));
        }catch(IOException e){
            RuntimeException exception = new RuntimeException(e.getMessage());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }

    private static void executeMain(String[] inputArgs) throws IOException{
        int[] sizes = new int[Structs.count(inputArgs, Strings::canParseInt)];

        for(int i = 0, sizeI = 0; i < inputArgs.length; i++){
            if(!Strings.canParseInt(inputArgs[i])) continue;
            sizes[sizeI++] = Strings.parseInt(inputArgs[i]);
        }
        System.out.println("sizes: " + Arrays.toString(sizes));
        /*
        Process for adding an icon to the font:
        1. Have an SVG ready, possibly created with this tool.
        2. Go to Fontello and load the config.json from core/assets-raw/fontgen/config.json
        3. Drag the SVG in.
        4. Export the config and font file, replace the old config.
        5. Take the font (ttf) from the zip, open it in FontForge, and merge it into font.woff and icon.ttf. Usually, you would do view -> go to (the 0x unicode index).
        **/
        Fi rootDirectory = Fi.get("../../../assets-raw");
        if(Core.atlas == null){
            rootDirectory = Fi.get("core/assets-raw");
        }
//        System.out.println(rootDirectory.file().getAbsoluteFile().getAbsoluteFile().getAbsolutePath());
//        if (true) throw null;
        Log.info("Converting icons...");
        Time.mark();
        Fi svgOutputFolder = rootDirectory.child("sprites/ui/svg-icons");
        svgOutputFolder.deleteDirectory();
        Fi[] list = rootDirectory.child("icons").list();

//        Seq<Fi> files = new Seq<>();

        for(Fi img : list){
            System.out.println(img);
            if(img.extension().equals("png")){
//                Fi dst = iconsFolder.child(img.nameWithoutExtension().replace("icon-", "") + ".svg");
                String fileName = img.nameWithoutExtension()/*.replace("icon-", "")*/;
//                dst.copyTo(iconsPartFolder.child(dst.name()));
                //dst.copyTo(svgOutputFolder.child(dst.name()));
                BufferedImage image = ImageIO.read(img.file());
                Pixmap pixmap = new Pixmap(image.getWidth(),image.getHeight());
                for(int x = 0; x < pixmap.width; x++){
                    for(int y = 0; y < pixmap.height; y++){
                        pixmap.set(x,y,Tmp.c1.argb8888(image.getRGB(x,y)));
                    }
                }

                for(int size : sizes){
                    new IconRasterizer().convert(pixmap.copy(), size, svgOutputFolder.child(fileName + "-" + size + ".png"));
                    /*Main main = new Main(new String[]{
//                    "-d",svgOutputFolder.absolutePath()+"/"+dst.nameWithoutExtension()+"-"+size+".png",
                    "-w", size + "",
                    "-h", size + "",
                    dst.file().getAbsoluteFile().getAbsolutePath(),
                    "-scriptSecurityOff"
                    });
                    main.execute();*/
                }
//                files.add(dst);
            }
        }

        Fi svgIcons = rootDirectory.sibling("assets/sprites/ui/svg-icons");
        svgIcons.deleteDirectory();
        for(Fi fi : svgOutputFolder.list()){
            fi.copyTo(svgIcons.child(fi.name()));
        }
        Log.info("Done converting icons in &lm@&lgs.", Time.elapsed() / 1000f);
//        System.exit(0);
    }

    void convert(Pixmap pixmap, int size, Fi output){


        width = pixmap.width;
        height = pixmap.height;
        cellSize=size;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        out= (Graphics2D)image.getGraphics();
        out.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        out.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
//        out.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//        out.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        marchingSquare(pixmap,size/(width)/2);


        System.out.println(output.absolutePath());
        Pixmap pix = new Pixmap(image.getWidth(), image.getHeight());
        pix.each((x,y)->{
            int argb=image.getRGB(x,y);

            Tmp.c1.argb8888(argb);
            pix.set(x,y,Tmp.c1);
        });

        output.writePng(pix);


//        out = null;
//        outImage = null;
//        output.writeString(out.toString());
    }

    private void marchingSquare(Pixmap pixmap, float halfScale){
        scl=halfScale*2;
        boolean[][] grid = new boolean[pixmap.width][pixmap.height];

        for(int x = 0; x < pixmap.width; x++){
            for(int y = 0; y < pixmap.height; y++){
                grid[x][pixmap.height - 1 - y] = !pixmap.empty(x, y);
            }
        }
        float scl=halfScale*2;

        //resolution / (float)pixmap.getWidth(), scl = resolution / (float)pixmap.getHeight();
        for(int x = -1; x < pixmap.width; x++){
            for(int y = -1; y < pixmap.height; y++){
                int index = index(x, y, pixmap.width, pixmap.height, grid);

                float leftx = x * scl, boty = y * scl, rightx = x * scl + scl, topy = y * scl + scl,
                midx = x * scl + halfScale, midy = y * scl + halfScale;

                switch(index){
                    case 0:
                        break;
                    case 1:
                        tri(
                        leftx, midy,
                        leftx, topy,
                        midx, topy
                        );
                        break;
                    case 2:
                        tri(
                        midx, topy,
                        rightx, topy,
                        rightx, midy
                        );
                        break;
                    case 3:
                        rect(leftx, midy, scl, scl / 2f);
                        break;
                    case 4:
                        tri(
                        midx, boty,
                        rightx, boty,
                        rightx, midy
                        );
                        break;
                    case 5:
                        //ambiguous

                        //7
                        tri(
                        leftx, midy,
                        midx, midy,
                        midx, boty
                        );

                        //13
                        tri(
                        midx, topy,
                        midx, midy,
                        rightx, midy
                        );

                        rect(leftx, midy, scl / 2f, scl / 2f);
                        rect(midx, boty, scl / 2f, scl / 2f);

                        break;
                    case 6:
                        rect(midx, boty, scl / 2f, scl);
                        break;
                    case 7:
                        //invert triangle
                        tri(
                        leftx, midy,
                        midx, midy,
                        midx, boty
                        );

                        //3
                        rect(leftx, midy, scl, scl / 2f);

                        rect(midx, boty, scl / 2f, scl / 2f);
                        break;
                    case 8:
                        tri(
                        leftx, boty,
                        leftx, midy,
                        midx, boty
                        );
                        break;
                    case 9:
                        rect(leftx, boty, scl / 2f, scl);
                        break;
                    case 10:
                        //ambiguous

                        //11
                        tri(
                        midx, boty,
                        midx, midy,
                        rightx, midy
                        );

                        //14
                        tri(
                        leftx, midy,
                        midx, midy,
                        midx, topy
                        );

                        rect(midx, midy, scl / 2f, scl / 2f);
                        rect(leftx, boty, scl / 2f, scl / 2f);

                        break;
                    case 11:
                        //invert triangle

                        tri(
                        midx, boty,
                        midx, midy,
                        rightx, midy
                        );

                        //3
                        rect(leftx, midy, scl, scl / 2f);

                        rect(leftx, boty, scl / 2f, scl / 2f);
                        break;
                    case 12:
                        rect(leftx, boty, scl, scl / 2f);
                        break;
                    case 13:
                        //invert triangle

                        tri(
                        midx, topy,
                        midx, midy,
                        rightx, midy
                        );

                        //12
                        rect(leftx, boty, scl, scl / 2f);

                        rect(leftx, midy, scl / 2f, scl / 2f);
                        break;
                    case 14:
                        //invert triangle

                        tri(
                        leftx, midy,
                        midx, midy,
                        midx, topy
                        );

                        //12
                        rect(leftx, boty, scl, scl / 2f);

                        rect(midx, midy, scl / 2f, scl / 2f);
                        break;
                    case 15:
                        square(midx, midy, scl);
                        break;
                }
            }
        }
    }

    public Pixmap toPixmap(FrameBuffer buffer){
        buffer.begin();
        int h = buffer.getHeight();
        int w = buffer.getWidth();
        byte[] lines = ScreenUtils.getFrameBufferPixels(0, 0, w, h, true);
        buffer.end();

        for(int i = 0; i < lines.length; i += 4){
//            lines[i + 3] = (byte) 255;
        }
        Pixmap fullPixmap = new Pixmap(w, h);
        Buffers.copy(lines, 0, fullPixmap.pixels, lines.length);
        return fullPixmap;
    }

    void square(float x, float y, float size){
        rect(x - size / 2f, y - size / 2f, size, size);
    }

    void tri(float x1, float y1, float x2, float y2, float x3, float y3){
//        float scaleX = outImage.getWidth() / width;
//        float scaleY = outImage.getHeight() / height;
        if (out!=null){
            float offsetX=scl/2f,offsetY=scl/2f;

            out.fill(new Tri(
            Tmp.v1.set(x1+offsetX, flip(y1+offsetY)),
            Tmp.v2.set(x2+offsetX, flip(y2+offsetY)),
            Tmp.v3.set(x3+offsetX, flip(y3+offsetY))
            ));
        }else{
            Fill.tri(
            x1 + 0.5f, flip(y1 + 0.5f),
            x2 + 0.5f, flip(y2 + 0.5f),
            x3 + 0.5f, flip(y3 + 0.5f)
            );
        }

       /* Tri tri = new Tri(
                new Vec2(x1 + 0.5f, flip(y1 + 0.5f)).scl(scaleX, scaleY),
                new Vec2(x2 + 0.5f, flip(y2 + 0.5f)).scl(scaleX, scaleY),
                new Vec2(x3 + 0.5f, flip(y3 + 0.5f)).scl(scaleX, scaleY)
        );
*/
        /*out.draw(tri);*/
    }

    void rect(float x1, float y1, float width, float height){
//        Fill.quad(x1 + 0.5f, flip(y1 + 0.5f), width, height);
        if (out!=null){
            JustPoly poly = new JustPoly(4);

            float x = x1 +scl/2;
            float y = flip(y1 +scl/2);
            poly.setAll(
            Tmp.v1.set(x, y),
            Tmp.v2.set(x + width, y),
            Tmp.v3.set(x + width, y + height),
            Tmp.v4.set(x, y + height)
            );
            out.fill(poly);
        } else{
            float x = x1 + 0.5f;
            float y = flip(y1 + 0.5f);
            Fill.quad(
            x, y,
            x + width, y,
            x + width, y + height,
            x, y + height
            );
        }
        /*float scaleX = outImage.getWidth() / width;
        float scaleY = outImage.getHeight() / height;
        Square square = new Square(
                new Vec2(Tmp.v1.set(x1 + 0.5f, flip(y1 + 0.5f))).scl(scaleX, scaleY),
                new Vec2(Tmp.v1.x + width, Tmp.v1.y).scl(scaleX, scaleY),
                new Vec2(Tmp.v1.x + width, Tmp.v1.y + height).scl(scaleX, scaleY),
                new Vec2(Tmp.v1.x, Tmp.v1.y + height).scl(scaleX, scaleY)
        );

        out.draw(square);*/
    }

    float flip(float y){
        return y;
    }

    int index(int x, int y, int w, int h, boolean[][] grid){
        int botleft = sample(grid, x, y);
        int botright = sample(grid, x + 1, y);
        int topright = sample(grid, x + 1, y + 1);
        int topleft = sample(grid, x, y + 1);
        return (botleft << 3) | (botright << 2) | (topright << 1) | topleft;
    }

    int sample(boolean[][] grid, int x, int y){
        return (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) ? 0 : grid[x][y] ? 1 : 0;
    }
}
