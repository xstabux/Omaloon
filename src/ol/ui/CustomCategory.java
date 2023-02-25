package ol.ui;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;

import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.content.Blocks;
import mindustry.core.UI;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.input.InputHandler;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

import ol.content.blocks.OlPressure;
import ol.world.blocks.RailBlock;
import ol.world.blocks.pressure.meta.MirrorBlock;
import ol.world.blocks.pressure.meta.PressureAble;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static mindustry.Vars.*;
import static mindustry.Vars.state;

public class CustomCategory {
    public static final CustomCategory blockfrag = new CustomCategory(0);

    @Contract(pure = true)
    public static void load() {
        if(removePrev()) {
            blockfrag.loadEvent();
            blockfrag.build(ui.hudGroup);

            try {
                blockfrag.rebuild32();
            } catch(Throwable ex) {
                Invoker.invokeFail(ex.toString());
            }
        } else {
            Invoker.invokeFail("Can`t remove build table");
        }
    }

    public static boolean removePrev() {
        try {
            Element elem = ui.hudGroup.find(element -> {
                if(element instanceof Table table) {
                    var ch = table.getChildren().list();
                    if(ch.size() == 1 && ch.get(0) instanceof Table tb) {
                        var ch2 = tb.getChildren().list();
                        if(ch2.size() > 0 && ch2.get(0) instanceof Table t) {
                            return t.getBackground() == Tex.buttonEdge2;
                        }
                    }
                }

                return false;
            });

            if(elem != null) {
                elem.visible(() -> false);
                elem.updateVisibility();
                return !elem.visible;
            }
        } catch(Throwable ignored) {
        }

        return false;
    }

    public record Category(TextureRegionDrawable icon) {
        private static final ObjectMap<Category, Integer> idMap = new ObjectMap<>();
        public static final Seq<Category> all = new Seq<>();

        public static Category turret = new Category(Icon.turret);
        public static Category production = new Category(Icon.production);
        public static Category distribution = new Category(Icon.distribution);
        public static Category liquid = new Category(Icon.liquid);
        public static Category power = new Category(Icon.power);
        public static Category defence = new Category(Icon.defense);
        public static Category crafting = new Category(Icon.crafting);
        public static Category units = new Category(Icon.units);
        public static Category effect = new Category(Icon.effect);
        public static Category logic = new Category(Icon.logic);
        public static Category pressure = new Category(OlPressure.pressurePipe.region, "pressure");
        public static Category rails = new Category(Icon.terrain);

        @Contract(value = "null -> null; !null -> !null", pure = true)
        public static Category of(mindustry.type.Category category) {
            if(category == null) return null;
            return all.get(category.ordinal());
        }

        public static Category of(int id) {
            return all.get(id);
        }

        @Contract(value = "null -> null", pure = true)
        public static Category of(Block block) {
            if(block == null) return null;

            if(block instanceof RailBlock) {
                return rails;
            }

            if((block instanceof PressureAble || block instanceof MirrorBlock) && power.equals(block.category))
            {
                return pressure;
            }

            return of(block.category);
        }

        @NotNull
        public static TextureRegionDrawable generate(TextureRegion region,
                                                     String name)
        {
            var reg = new TextureRegionDrawable(region);
            reg.setName(name);
            return reg;
        }

        public String name() {
            if(this.icon == null) return "";
            return this.icon.getName();
        }

        public int id() {
            return idMap.get(this);
        }

        public Category prev() {
            return of(this.id() - 1);
        }

        public Category next() {
            return of(this.id() + 1);
        }

        public String locale() {
            return Core.bundle.get("category." + this.name());
        }

        public Category(TextureRegionDrawable icon) {
            this.icon = icon;
            idMap.put(this, all.size);
            all.add(this);
        }

        public Category(TextureRegion region, String name) {
            this(generate(region, name));
        }

        public boolean equals(mindustry.type.Category category) {
            return this.equals(of(category));
        }

        public boolean equals(Block block) {
            return this.equals(of(block));
        }
    }

    //method generated by annotation processor
    public static void loadIcons() {
        Icon.fileTextFillSmall.setName("fileTextFillSmall");
        Icon.fileTextFill.setName("fileTextFill");
        Icon.fileSmall.setName("fileSmall");
        Icon.file.setName("file");
        Icon.fileTextSmall.setName("fileTextSmall");
        Icon.fileText.setName("fileText");
        Icon.leftSmall.setName("leftSmall");
        Icon.left.setName("left");
        Icon.rightSmall.setName("rightSmall");
        Icon.right.setName("right");
        Icon.upSmall.setName("upSmall");
        Icon.up.setName("up");
        Icon.downSmall.setName("downSmall");
        Icon.down.setName("down");
        Icon.homeSmall.setName("homeSmall");
        Icon.home.setName("home");
        Icon.okSmall.setName("okSmall");
        Icon.ok.setName("ok");
        Icon.imageSmall.setName("imageSmall");
        Icon.image.setName("image");
        Icon.starSmall.setName("starSmall");
        Icon.star.setName("star");
        Icon.resizeSmall.setName("resizeSmall");
        Icon.resize.setName("resize");
        Icon.wrenchSmall.setName("wrenchSmall");
        Icon.wrench.setName("wrench");
        Icon.githubSquareSmall.setName("githubSquareSmall");
        Icon.githubSquare.setName("githubSquare");
        Icon.fileImageSmall.setName("fileImageSmall");
        Icon.fileImage.setName("fileImage");
        Icon.addSmall.setName("addSmall");
        Icon.add.setName("add");
        Icon.editSmall.setName("editSmall");
        Icon.edit.setName("edit");
        Icon.chartBarSmall.setName("chartBarSmall");
        Icon.chartBar.setName("chartBar");
        Icon.planeOutlineSmall.setName("planeOutlineSmall");
        Icon.planeOutline.setName("planeOutline");
        Icon.filterSmall.setName("filterSmall");
        Icon.filter.setName("filter");
        Icon.folderSmall.setName("folderSmall");
        Icon.folder.setName("folder");
        Icon.steamSmall.setName("steamSmall");
        Icon.steam.setName("steam");
        Icon.downOpenSmall.setName("downOpenSmall");
        Icon.downOpen.setName("downOpen");
        Icon.leftOpenSmall.setName("leftOpenSmall");
        Icon.leftOpen.setName("leftOpen");
        Icon.upOpenSmall.setName("upOpenSmall");
        Icon.upOpen.setName("upOpen");
        Icon.mapSmall.setName("mapSmall");
        Icon.map.setName("map");
        Icon.rotateSmall.setName("rotateSmall");
        Icon.rotate.setName("rotate");
        Icon.playSmall.setName("playSmall");
        Icon.play.setName("play");
        Icon.pauseSmall.setName("pauseSmall");
        Icon.pause.setName("pause");
        Icon.listSmall.setName("listSmall");
        Icon.list.setName("list");
        Icon.cancelSmall.setName("cancelSmall");
        Icon.cancel.setName("cancel");
        Icon.moveSmall.setName("moveSmall");
        Icon.move.setName("move");
        Icon.terminalSmall.setName("terminalSmall");
        Icon.terminal.setName("terminal");
        Icon.undoSmall.setName("undoSmall");
        Icon.undo.setName("undo");
        Icon.redoSmall.setName("redoSmall");
        Icon.redo.setName("redo");
        Icon.infoSmall.setName("infoSmall");
        Icon.info.setName("info");
        Icon.infoCircleSmall.setName("infoCircleSmall");
        Icon.infoCircle.setName("infoCircle");
        Icon.rightOpenOutSmall.setName("rightOpenOutSmall");
        Icon.rightOpenOut.setName("rightOpenOut");
        Icon.rightOpenSmall.setName("rightOpenSmall");
        Icon.rightOpen.setName("rightOpen");
        Icon.wavesSmall.setName("wavesSmall");
        Icon.waves.setName("waves");
        Icon.filtersSmall.setName("filtersSmall");
        Icon.filters.setName("filters");
        Icon.layersSmall.setName("layersSmall");
        Icon.layers.setName("layers");
        Icon.eraserSmall.setName("eraserSmall");
        Icon.eraser.setName("eraser");
        Icon.bookOpenSmall.setName("bookOpenSmall");
        Icon.bookOpen.setName("bookOpen");
        Icon.gridSmall.setName("gridSmall");
        Icon.grid.setName("grid");
        Icon.flipXSmall.setName("flipXSmall");
        Icon.flipX.setName("flipX");
        Icon.flipYSmall.setName("flipYSmall");
        Icon.flipY.setName("flipY");
        Icon.diagonalSmall.setName("diagonalSmall");
        Icon.diagonal.setName("diagonal");
        Icon.discordSmall.setName("discordSmall");
        Icon.discord.setName("discord");
        Icon.boxSmall.setName("boxSmall");
        Icon.box.setName("box");
        Icon.redditAlienSmall.setName("redditAlienSmall");
        Icon.redditAlien.setName("redditAlien");
        Icon.githubSmall.setName("githubSmall");
        Icon.github.setName("github");
        Icon.googleplaySmall.setName("googleplaySmall");
        Icon.googleplay.setName("googleplay");
        Icon.androidSmall.setName("androidSmall");
        Icon.android.setName("android");
        Icon.trelloSmall.setName("trelloSmall");
        Icon.trello.setName("trello");
        Icon.logicSmall.setName("logicSmall");
        Icon.logic.setName("logic");
        Icon.distributionSmall.setName("distributionSmall");
        Icon.distribution.setName("distribution");
        Icon.hammerSmall.setName("hammerSmall");
        Icon.hammer.setName("hammer");
        Icon.saveSmall.setName("saveSmall");
        Icon.save.setName("save");
        Icon.linkSmall.setName("linkSmall");
        Icon.link.setName("link");
        Icon.itchioSmall.setName("itchioSmall");
        Icon.itchio.setName("itchio");
        Icon.lineSmall.setName("lineSmall");
        Icon.line.setName("line");
        Icon.adminSmall.setName("adminSmall");
        Icon.admin.setName("admin");
        Icon.spray1Small.setName("spray1Small");
        Icon.spray1.setName("spray1");
        Icon.craftingSmall.setName("craftingSmall");
        Icon.crafting.setName("crafting");
        Icon.fillSmall.setName("fillSmall");
        Icon.fill.setName("fill");
        Icon.pasteSmall.setName("pasteSmall");
        Icon.paste.setName("paste");
        Icon.effectSmall.setName("effectSmall");
        Icon.effect.setName("effect");
        Icon.bookSmall.setName("bookSmall");
        Icon.book.setName("book");
        Icon.liquidSmall.setName("liquidSmall");
        Icon.liquid.setName("liquid");
        Icon.hostSmall.setName("hostSmall");
        Icon.host.setName("host");
        Icon.productionSmall.setName("productionSmall");
        Icon.production.setName("production");
        Icon.exitSmall.setName("exitSmall");
        Icon.exit.setName("exit");
        Icon.modePvpSmall.setName("modePvpSmall");
        Icon.modePvp.setName("modePvp");
        Icon.modeAttackSmall.setName("modeAttackSmall");
        Icon.modeAttack.setName("modeAttack");
        Icon.refresh1Small.setName("refresh1Small");
        Icon.refresh1.setName("refresh1");
        Icon.noneSmall.setName("noneSmall");
        Icon.none.setName("none");
        Icon.pencilSmall.setName("pencilSmall");
        Icon.pencil.setName("pencil");
        Icon.refreshSmall.setName("refreshSmall");
        Icon.refresh.setName("refresh");
        Icon.modeSurvivalSmall.setName("modeSurvivalSmall");
        Icon.modeSurvival.setName("modeSurvival");
        Icon.commandRallySmall.setName("commandRallySmall");
        Icon.commandRally.setName("commandRally");
        Icon.unitsSmall.setName("unitsSmall");
        Icon.units.setName("units");
        Icon.commandAttackSmall.setName("commandAttackSmall");
        Icon.commandAttack.setName("commandAttack");
        Icon.trashSmall.setName("trashSmall");
        Icon.trash.setName("trash");
        Icon.chatSmall.setName("chatSmall");
        Icon.chat.setName("chat");
        Icon.turretSmall.setName("turretSmall");
        Icon.turret.setName("turret");
        Icon.playersSmall.setName("playersSmall");
        Icon.players.setName("players");
        Icon.editorSmall.setName("editorSmall");
        Icon.editor.setName("editor");
        Icon.copySmall.setName("copySmall");
        Icon.copy.setName("copy");
        Icon.treeSmall.setName("treeSmall");
        Icon.tree.setName("tree");
        Icon.lockOpenSmall.setName("lockOpenSmall");
        Icon.lockOpen.setName("lockOpen");
        Icon.pickSmall.setName("pickSmall");
        Icon.pick.setName("pick");
        Icon.exportSmall.setName("exportSmall");
        Icon.export.setName("export");
        Icon.downloadSmall.setName("downloadSmall");
        Icon.download.setName("download");
        Icon.uploadSmall.setName("uploadSmall");
        Icon.upload.setName("upload");
        Icon.settingsSmall.setName("settingsSmall");
        Icon.settings.setName("settings");
        Icon.spraySmall.setName("spraySmall");
        Icon.spray.setName("spray");
        Icon.zoomSmall.setName("zoomSmall");
        Icon.zoom.setName("zoom");
        Icon.powerOldSmall.setName("powerOldSmall");
        Icon.powerOld.setName("powerOld");
        Icon.powerSmall.setName("powerSmall");
        Icon.power.setName("power");
        Icon.menuSmall.setName("menuSmall");
        Icon.menu.setName("menu");
        Icon.lockSmall.setName("lockSmall");
        Icon.lock.setName("lock");
        Icon.eyeSmall.setName("eyeSmall");
        Icon.eye.setName("eye");
        Icon.eyeOffSmall.setName("eyeOffSmall");
        Icon.eyeOff.setName("eyeOff");
        Icon.warningSmall.setName("warningSmall");
        Icon.warning.setName("warning");
        Icon.terrainSmall.setName("terrainSmall");
        Icon.terrain.setName("terrain");
        Icon.defenseSmall.setName("defenseSmall");
        Icon.defense.setName("defense");
        Icon.planetSmall.setName("planetSmall");
        Icon.planet.setName("planet");
    }

    public void rebuild32() {
        Table fillTable = Reflect.get(ui.hudfrag.blockfrag, "toggler");
        Group group = fillTable.parent;
        int index = fillTable.getZIndex();
        fillTable.remove();
        build(group);
        fillTable.setZIndex(index);
    }

    public void loadEvent() {
        Events.on(EventType.WorldLoadEvent.class, event -> {
            Core.app.post(() -> {
                currentCategory = Category.distribution;
                control.input.block = null;
                rebuild32();
            });
        });

        Events.run(EventType.Trigger.unitCommandChange, () -> {
            if(rebuildCommand != null) {
                rebuildCommand.run();
            }
        });

        Events.on(EventType.UnlockEvent.class, event -> {
            if(event.content instanceof Block) {
                rebuild32();
            }
        });

        Events.on(EventType.ResetEvent.class, event -> {
            selectedBlocks.clear();
        });

        Events.run(EventType.Trigger.update, () -> {
            //disable flow updating on previous building, so it doesn't waste CPU
            if(lastFlowBuild != null && lastFlowBuild != nextFlowBuild){
                if(lastFlowBuild.flowItems() != null) lastFlowBuild.flowItems().stopFlow();
                if(lastFlowBuild.liquids != null) lastFlowBuild.liquids.stopFlow();
            }

            lastFlowBuild = nextFlowBuild;

            if(nextFlowBuild != null){
                if(nextFlowBuild.flowItems() != null) nextFlowBuild.flowItems().updateFlow();
                if(nextFlowBuild.liquids != null) nextFlowBuild.liquids.updateFlow();
            }
        });
    }

    public CustomCategory(int ignored) {
    }

    final int rowWidth = 4;

    public Category currentCategory = Category.distribution;

    Seq<Block> returnArray = new Seq<>(), returnArray2 = new Seq<>();
    Seq<Category> returnCatArray = new Seq<>();
    boolean[] categoryEmpty = new boolean[Category.all.size];
    ObjectMap<Category,Block> selectedBlocks = new ObjectMap<>();
    ObjectFloatMap<Category> scrollPositions = new ObjectFloatMap<>();
    @Nullable Block menuHoverBlock;
    @Nullable Displayable hover;
    @Nullable Building lastFlowBuild, nextFlowBuild;
    @Nullable Object lastDisplayState;
    @Nullable Team lastTeam;
    boolean wasHovered;
    Table blockTable, topTable, blockCatTable, commandTable;
    Stack mainStack;
    ScrollPane blockPane;
    Runnable rebuildCommand;
    boolean blockSelectEnd, wasCommandMode;
    int blockSelectSeq;
    long blockSelectSeqMillis;
    Binding[] blockSelect = {
            Binding.block_select_01,
            Binding.block_select_02,
            Binding.block_select_03,
            Binding.block_select_04,
            Binding.block_select_05,
            Binding.block_select_06,
            Binding.block_select_07,
            Binding.block_select_08,
            Binding.block_select_09,
            Binding.block_select_10,
            Binding.block_select_left,
            Binding.block_select_right,
            Binding.block_select_up,
            Binding.block_select_down
    };

    boolean gridUpdate(InputHandler input){
        scrollPositions.put(currentCategory, blockPane.getScrollY());

        if(Core.input.keyTap(Binding.pick) && player.isBuilder() && !Core.scene.hasDialog()) { //mouse eyedropper select
            var build = world.buildWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

            //can't middle-click buildings in fog
            if(build != null && build.inFogTo(player.team())){
                build = null;
            }

            Block tryRecipe = build == null ? null : build instanceof ConstructBlock.ConstructBuild c ?
                    c.current : build.block;
            Object tryConfig = build == null || !build.block.copyConfig ? null : build.config();

            for(BuildPlan req : player.unit().plans()){
                if(!req.breaking && req.block.bounds(req.x, req.y, Tmp.r1).contains(Core.input.mouseWorld())){
                    tryRecipe = req.block;
                    tryConfig = req.config;
                    break;
                }
            }

            if(tryRecipe != null && tryRecipe.isVisible() && unlocked(tryRecipe)){
                input.block = tryRecipe;
                tryRecipe.lastConfig = tryConfig;
                currentCategory = Category.of(input.block.category);
                return true;
            }
        }

        if(ui.chatfrag.shown() || ui.consolefrag.shown() || Core.scene.hasKeyboard()) return false;

        for(int i = 0; i < blockSelect.length; i++){
            if(Core.input.keyTap(blockSelect[i])){
                if(i > 9){ //select block directionally
                    Seq<Block> blocks = getUnlockedByCategory(currentCategory);
                    Block currentBlock = getSelectedBlock(currentCategory);
                    for(int j = 0; j < blocks.size; j++){
                        if(blocks.get(j) == currentBlock){
                            switch(i){
                                //left
                                case 10 -> j = (j - 1 + blocks.size) % blocks.size;
                                //right
                                case 11 -> j = (j + 1) % blocks.size;
                                //up
                                case 12 -> {
                                    j = (j > 3 ? j - 4 : blocks.size - blocks.size % 4 + j);
                                    j -= (j < blocks.size ? 0 : 4);
                                }
                                //down
                                case 13 -> j = (j < blocks.size - 4 ? j + 4 : j % 4);
                            }
                            input.block = blocks.get(j);
                            selectedBlocks.put(currentCategory, input.block);
                            break;
                        }
                    }
                }else if(blockSelectEnd || Time.timeSinceMillis(blockSelectSeqMillis) > 400){ //1st number of combo, select category
                    //select only visible categories
                    Category category = Category.all.get(i);
                    if(!getUnlockedByCategory(category).isEmpty()){
                        currentCategory = category;
                        if(input.block != null){
                            input.block = getSelectedBlock(currentCategory);
                        }
                        blockSelectEnd = false;
                        blockSelectSeq = 0;
                        blockSelectSeqMillis = Time.millis();
                    }
                }else{ //select block
                    if(blockSelectSeq == 0){ //2nd number of combo
                        blockSelectSeq = i + 1;
                    }else{ //3rd number of combo
                        //entering "X,1,0" selects the same block as "X,0"
                        i += (blockSelectSeq - (i != 9 ? 0 : 1)) * 10;
                        blockSelectEnd = true;
                    }
                    Seq<Block> blocks = getByCategory(currentCategory);
                    if(i >= blocks.size || !unlocked(blocks.get(i))) return true;
                    input.block = (i < blocks.size) ? blocks.get(i) : null;
                    selectedBlocks.put(currentCategory, input.block);
                    blockSelectSeqMillis = Time.millis();
                }
                return true;
            }
        }

        if(Core.input.keyTap(Binding.category_prev)){
            do{
                currentCategory = currentCategory.prev();
            }while(categoryEmpty[currentCategory.id()]);
            input.block = getSelectedBlock(currentCategory);
            return true;
        }

        if(Core.input.keyTap(Binding.category_next)){
            do{
                currentCategory = currentCategory.next();
            }while(categoryEmpty[currentCategory.id()]);
            input.block = getSelectedBlock(currentCategory);
            return true;
        }

        if(Core.input.keyTap(Binding.block_info)){
            var build = world.buildWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
            Block hovering = build == null ? null : build instanceof ConstructBlock.ConstructBuild c ?
                    c.current : build.block;

            Block displayBlock = menuHoverBlock != null ? menuHoverBlock : input.block != null ?
                    input.block : hovering;

            if(displayBlock != null && displayBlock.unlockedNow()) {
                ui.content.show(displayBlock);
                Events.fire(new EventType.BlockInfoEvent());
            }
        }

        return false;
    }

    Seq<Category> getCategories(){
        return returnCatArray.clear().addAll(Category.all);
    }

    Seq<Block> getByCategory(Category cat){
        return returnArray.selectFrom(content.blocks(), block ->
                cat.equals(block) && block.isVisible() && block.environmentBuildable());
    }

    Seq<Block> getUnlockedByCategory(Category cat){
        return returnArray2.selectFrom(content.blocks(), block ->
                cat.equals(block) && block.isVisible() && unlocked(block)).sort((b1, b2) ->
                Boolean.compare(!b1.isPlaceable(), !b2.isPlaceable()));
    }

    Block getSelectedBlock(Category cat){
        return selectedBlocks.get(cat, () -> getByCategory(cat).find(this::unlocked));
    }

    boolean unlocked(Block block){
        return block.unlockedNow() && block.placeablePlayer && block.environmentBuildable() &&
                block.supportsEnv(state.rules.env); //TODO this hides env unsupported blocks, not always a good thing
    }

    boolean hasInfoBox(){
        hover = hovered();
        return control.input.block != null || menuHoverBlock != null || hover != null;
    }

    /** Returns the thing being hovered over. */
    @Nullable
    Displayable hovered(){
        Vec2 v = topTable.stageToLocalCoordinates(Core.input.mouse());

        //if the mouse intersects the table or the UI has the mouse, no hovering can occur
        if(Core.scene.hasMouse() || topTable.hit(v.x, v.y, false) != null) return null;

        //check for a unit
        Unit unit = Units.closestOverlap(player.team(), Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, u ->
                !u.isLocal() && u.displayable());
        //if cursor has a unit, display it
        if(unit != null) return unit;

        //check tile being hovered over
        Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
        if(hoverTile != null){
            //if the tile has a building, display it
            if(hoverTile.build != null && hoverTile.build.displayable() && !hoverTile.build.inFogTo(player.team())){
                return nextFlowBuild = hoverTile.build;
            }

            //if the tile has a drop, display the drop
            if((hoverTile.drop() != null && hoverTile.block() == Blocks.air) || hoverTile.wallDrop() != null ||
                    hoverTile.floor().liquidDrop != null){
                return hoverTile;
            }
        }

        return null;
    }

    public void build(@NotNull Group parent) {
        parent.fill(full -> {
            Reflect.set(ui.hudfrag.blockfrag, "toggler", full);
            full.bottom().right().visible(() -> ui.hudfrag.shown);

            full.table(frame -> {
                //rebuilds the category table with the correct recipes
                Runnable rebuildCategory = () -> {
                    blockTable.clear();
                    blockTable.top().margin(5);

                    int index = 0;

                    ButtonGroup<ImageButton> group = new ButtonGroup<>();
                    group.setMinCheckCount(0);

                    for(Block block : getUnlockedByCategory(currentCategory)){
                        if(!unlocked(block)) continue;
                        if(index++ % rowWidth == 0){
                            blockTable.row();
                        }

                        ImageButton button = blockTable.button(new TextureRegionDrawable(block.uiIcon), Styles.selecti, () -> {
                            if(unlocked(block)){
                                if((Core.input.keyDown(KeyCode.shiftLeft) || Core.input.keyDown(KeyCode.controlLeft)) &&
                                        Fonts.getUnicode(block.name) != 0)
                                {
                                    Core.app.setClipboardText((char)Fonts.getUnicode(block.name) + "");
                                    ui.showInfoFade("@copied");
                                }else{
                                    control.input.block = control.input.block == block ? null : block;
                                    selectedBlocks.put(currentCategory, control.input.block);
                                }
                            }
                        }).size(46f).group(group).name("block-" + block.name).get();
                        button.resizeImage(iconMed);

                        button.update(() -> { //color unplacable things gray
                            Building core = player.core();
                            Color color = (state.rules.infiniteResources || (core != null &&
                                    (core.items.has(block.requirements, state.rules.buildCostMultiplier) ||
                                            state.rules.infiniteResources))) && player.isBuilder() ? Color.white : Color.gray;
                            button.forEach(elem -> elem.setColor(color));
                            button.setChecked(control.input.block == block);

                            if(!block.isPlaceable()){
                                button.forEach(elem -> elem.setColor(Color.darkGray));
                            }
                        });

                        button.hovered(() -> menuHoverBlock = block);
                        button.exited(() -> {
                            if(menuHoverBlock == block){
                                menuHoverBlock = null;
                            }
                        });
                    }
                    //add missing elements to even out table size
                    if(index < 4){
                        for(int i = 0; i < 4-index; i++){
                            blockTable.add().size(46f);
                        }
                    }
                    blockTable.act(0f);
                    blockPane.setScrollYForce(scrollPositions.get(currentCategory, 0));
                    Core.app.post(() -> {
                        blockPane.setScrollYForce(scrollPositions.get(currentCategory, 0));
                        blockPane.act(0f);
                        blockPane.layout();
                    });
                };

                //top table with hover info
                frame.table(Tex.buttonEdge2,top -> {
                    topTable = top;
                    top.add(new Table()).growX().update(topTable -> {

                        //find current hovered thing
                        Displayable hovered = hover;
                        Block displayBlock = menuHoverBlock != null ? menuHoverBlock : control.input.block;
                        Object displayState = displayBlock != null ? displayBlock : hovered;
                        boolean isHovered = displayBlock == null; //use hovered thing if display block is null

                        //don't refresh unnecessarily
                        //refresh only when the hover state changes, or the displayed block changes
                        if(wasHovered == isHovered && lastDisplayState == displayState && lastTeam == player.team()) return;

                        topTable.clear();
                        topTable.top().left().margin(5);

                        lastDisplayState = displayState;
                        wasHovered = isHovered;
                        lastTeam = player.team();

                        //show details of selected block, with costs
                        if(displayBlock != null){

                            topTable.table(header -> {
                                String keyCombo = "";
                                if(!mobile){
                                    Seq<Block> blocks = getByCategory(currentCategory);
                                    for(int i = 0; i < blocks.size; i++){
                                        if(blocks.get(i) == displayBlock && (i + 1) / 10 - 1 < blockSelect.length){
                                            keyCombo = Core.bundle.format("placement.blockselectkeys",
                                                    Core.keybinds.get(blockSelect[currentCategory.id()]).key.toString())
                                                    + (i < 10 ? "" : Core.keybinds.get(blockSelect[(i + 1) / 10 - 1]).key.toString() + ",")
                                                    + Core.keybinds.get(blockSelect[i % 10]).key.toString() + "]";
                                            break;
                                        }
                                    }
                                }
                                final String keyComboFinal = keyCombo;
                                header.left();
                                header.add(new Image(displayBlock.uiIcon)).size(8 * 4);
                                header.labelWrap(() -> !unlocked(displayBlock) ? Core.bundle.get("block.unknown") : displayBlock.localizedName + keyComboFinal)
                                        .left().width(190f).padLeft(5);
                                header.add().growX();
                                if(unlocked(displayBlock)){
                                    header.button("?", Styles.flatBordert, () -> {
                                        ui.content.show(displayBlock);
                                        Events.fire(new EventType.BlockInfoEvent());
                                    }).size(8 * 5).padTop(-5).padRight(-5).right().grow().name("blockinfo");
                                }
                            }).growX().left();
                            topTable.row();
                            //add requirement table
                            topTable.table(req -> {
                                req.top().left();

                                for(ItemStack stack : displayBlock.requirements){
                                    req.table(line -> {
                                        line.left();
                                        line.image(stack.item.uiIcon).size(8 * 2);
                                        line.add(stack.item.localizedName).maxWidth(140f).fillX(
                                        ).color(Color.lightGray).padLeft(2).left().get().setEllipsis(true);
                                        line.labelWrap(() -> {
                                            Building core = player.core();
                                            int stackamount = Math.round(stack.amount * state.rules.buildCostMultiplier);
                                            if(core == null || state.rules.infiniteResources) return "*/" + stackamount;

                                            int amount = core.items.get(stack.item);
                                            String color = (amount < stackamount / 2f ? "[scarlet]" : amount <
                                                    stackamount ? "[accent]" : "[white]");

                                            return color + UI.formatAmount(amount) + "[white]/" + stackamount;
                                        }).padLeft(5);
                                    }).left();
                                    req.row();
                                }
                            }).growX().left().margin(3);

                            if(!displayBlock.isPlaceable() || !player.isBuilder()){
                                topTable.row();
                                topTable.table(b -> {
                                    b.image(Icon.cancel).padRight(2).color(Color.scarlet);
                                    b.add(!player.isBuilder() ? "@unit.nobuild" : !displayBlock.supportsEnv(state.rules.env) ? "@unsupported.environment" : "@banned").width(190f).wrap();
                                    b.left();
                                }).padTop(2).left();
                            }

                        }else if(hovered != null){
                            //show hovered item, whatever that may be
                            hovered.display(topTable);
                        }
                    });
                }).colspan(3).fillX().visible(this::hasInfoBox).touchable(Touchable.enabled).row();

                frame.image().color(Pal.gray).colspan(3).height(4).growX().row();

                blockCatTable = new Table();
                commandTable = new Table(Tex.pane2);
                mainStack = new Stack();

                mainStack.update(() -> {
                    if(control.input.commandMode != wasCommandMode){
                        mainStack.clearChildren();
                        mainStack.addChild(control.input.commandMode ? commandTable : blockCatTable);

                        //hacky, but forces command table to be same width as blocks
                        if(control.input.commandMode){
                            commandTable.getCells().peek().width(blockCatTable.getWidth() / Scl.scl(1f));
                        }

                        wasCommandMode = control.input.commandMode;
                    }
                });

                frame.add(mainStack).colspan(3).fill();

                frame.row();

                //for better inset visuals at the bottom
                frame.rect((x, y, w, h) -> {
                    if(Core.scene.marginBottom > 0){
                        Tex.paneLeft.draw(x, 0, w, y);
                    }
                }).colspan(3).fillX().row();

                //commandTable: commanded units
                {
                    commandTable.touchable = Touchable.enabled;
                    commandTable.add(Core.bundle.get("commandmode.name")).fill().center().labelAlign(Align.center).row();
                    commandTable.image().color(Pal.accent).growX().pad(20f).padTop(0f).padBottom(4f).row();
                    commandTable.table(u -> {
                        u.left();
                        int[] curCount = {0};
                        UnitCommand[] currentCommand = {null};
                        var commands = new Seq<UnitCommand>();

                        rebuildCommand = () -> {
                            u.clearChildren();
                            var units = control.input.selectedUnits;
                            if(units.size > 0){
                                int[] counts = new int[content.units().size];
                                for(var unit : units){
                                    counts[unit.type.id] ++;
                                }
                                commands.clear();
                                boolean firstCommand = false;
                                Table unitlist = u.table().growX().left().get();
                                unitlist.left();

                                int col = 0;
                                for(int i = 0; i < counts.length; i++){
                                    if(counts[i] > 0){
                                        var type = content.unit(i);
                                        unitlist.add(new ItemImage(type.uiIcon, counts[i])).tooltip(type.localizedName).pad(4).with(b -> {
                                            var listener = new ClickListener();

                                            //left click -> select
                                            b.clicked(KeyCode.mouseLeft, () -> control.input.selectedUnits.removeAll(unit -> unit.type != type));
                                            //right click -> remove
                                            b.clicked(KeyCode.mouseRight, () -> control.input.selectedUnits.removeAll(unit -> unit.type == type));

                                            b.addListener(listener);
                                            b.addListener(new HandCursorListener());
                                            //gray on hover
                                            b.update(() -> ((Group)b.getChildren().first()).getChildren().first().setColor(listener.isOver() ? Color.lightGray : Color.white));
                                        });

                                        if(++col % 7 == 0){
                                            unitlist.row();
                                        }

                                        if(!firstCommand){
                                            commands.add(type.commands);
                                            firstCommand = true;
                                        }else{
                                            //remove commands that this next unit type doesn't have
                                            commands.removeAll(com -> !Structs.contains(type.commands, com));
                                        }
                                    }
                                }

                                if(commands.size > 1){
                                    u.row();

                                    u.table(coms -> {
                                        for(var command : commands){
                                            coms.button(Icon.icons.get(command.icon, Icon.cancel), Styles.clearNoneTogglei, () -> {
                                                IntSeq ids = new IntSeq();
                                                for(var unit : units){
                                                    ids.add(unit.id);
                                                }

                                                Call.setUnitCommand(Vars.player, ids.toArray(), command);
                                            }).checked(i -> currentCommand[0] == command).size(50f).tooltip(command.localized());
                                        }
                                    }).fillX().padTop(4f).left();
                                }
                            }else{
                                u.add(Core.bundle.get("commandmode.nounits")).color(Color.lightGray).growX().center().labelAlign(Align.center).pad(6);
                            }
                        };

                        u.update(() -> {
                            boolean hadCommand = false;
                            UnitCommand shareCommand = null;

                            //find the command that all units have, or null if they do not share one
                            for(var unit : control.input.selectedUnits){
                                if(unit.isCommandable()){
                                    var nextCommand = unit.command().currentCommand();

                                    if(hadCommand){
                                        if(shareCommand != nextCommand){
                                            shareCommand = null;
                                        }
                                    }else{
                                        shareCommand = nextCommand;
                                        hadCommand = true;
                                    }
                                }
                            }

                            currentCommand[0] = shareCommand;

                            int size = control.input.selectedUnits.size;
                            if(curCount[0] != size){
                                curCount[0] = size;
                                rebuildCommand.run();
                            }
                        });
                        rebuildCommand.run();
                    }).grow();
                }

                //blockCatTable: all blocks | all categories
                {
                    blockCatTable.table(Tex.pane2, blocksSelect -> {
                        blocksSelect.margin(4).marginTop(0);
                        blockPane = blocksSelect.pane(blocks -> blockTable = blocks).height(194f).update(pane -> {
                            if(pane.hasScroll()){
                                Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                                if(result == null || !result.isDescendantOf(pane)){
                                    Core.scene.setScrollFocus(null);
                                }
                            }
                        }).grow().get();
                        blockPane.setStyle(Styles.smallPane);
                        blocksSelect.row();
                        blocksSelect.table(control.input::buildPlacementUI).name("inputTable").growX();
                    }).fillY().bottom().touchable(Touchable.enabled);
                    blockCatTable.table(categories -> {
                        categories.bottom();
                        categories.add(new Image(Styles.black6){
                            @Override
                            public void draw(){
                                if(height <= Scl.scl(3f)) return;
                                getDrawable().draw(x, y, width, height - Scl.scl(3f));
                            }
                        }).colspan(2).growX().growY().padTop(-3f).row();
                        categories.defaults().size(50f);

                        ButtonGroup<ImageButton> group = new ButtonGroup<>();

                        //update category empty values
                        for(Category cat : Category.all){
                            Seq<Block> blocks = getUnlockedByCategory(cat);
                            categoryEmpty[cat.id()] = blocks.isEmpty();
                        }

                        boolean needsAssign = categoryEmpty[currentCategory.id()];

                        int f = 0;
                        for(Category cat : getCategories()){
                            if(f++ % 2 == 0) categories.row();

                            if(categoryEmpty[cat.id()]){
                                categories.image(Styles.black6);
                                continue;
                            }

                            if(needsAssign){
                                currentCategory = cat;
                                needsAssign = false;
                            }

                            categories.button(cat.icon(), Styles.clearTogglei, () -> {
                                        currentCategory = cat;
                                        if(control.input.block != null){
                                            control.input.block = getSelectedBlock(currentCategory);
                                        }
                                        rebuildCategory.run();
                                    }).group(group).update(i -> i.setChecked(currentCategory == cat))
                                    .name("category-" + cat.name()).tooltip(cat.locale());
                        }
                    }).fillY().bottom().touchable(Touchable.enabled);
                }

                mainStack.add(blockCatTable);

                rebuildCategory.run();
                frame.update(() -> {
                    if(gridUpdate(control.input)) rebuildCategory.run();
                });
            });
        });
    }
}