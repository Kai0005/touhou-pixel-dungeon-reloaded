/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.touhoupixel.touhoupixeldungeonreloaded;

import com.touhoupixel.touhoupixeldungeonreloaded.actors.Actor;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Amok;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Awareness;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Light;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.MagicalSight;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.MindVision;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.RevealedArea;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs.Mob;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Amulet;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Generator;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Heap;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Item;
import com.touhoupixel.touhoupixeldungeonreloaded.items.artifacts.TalismanOfForesight;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.Potion;
import com.touhoupixel.touhoupixeldungeonreloaded.items.rings.Ring;
import com.touhoupixel.touhoupixeldungeonreloaded.items.scrolls.Scroll;
import com.touhoupixel.touhoupixeldungeonreloaded.items.wands.WandOfRegrowth;
import com.touhoupixel.touhoupixeldungeonreloaded.items.wands.WandOfWarding;
import com.touhoupixel.touhoupixeldungeonreloaded.journal.Notes;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.BambooForestLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.DeadEndLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.ForestofMagicLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.GardenoftheSunLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.HakugyokurouLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.HakureiShrineLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.HeavenLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.HellLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.HumanVillageLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.LandoftheBackDoorLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.LastLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.Level;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.LunarCapitalLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.PrimateSpiritGardenLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.MistyLakeLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.MoriyaShrineLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.MyourenTempleLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.RainbowDragonCaveLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.RegularLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.SanzuRiverLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.ScarletDevilMansionLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.ShiningNeedleCastleLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.TutorialLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.YokaiMountainLevel;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.features.LevelTransition;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.rooms.secret.SecretRoom;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.rooms.special.SpecialRoom;
import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.touhoupixel.touhoupixeldungeonreloaded.scenes.GameScene;
import com.touhoupixel.touhoupixeldungeonreloaded.ui.QuickSlotButton;
import com.touhoupixel.touhoupixeldungeonreloaded.ui.Toolbar;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.BArray;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.DungeonSeed;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

public class Dungeon {

	//enum of items which have limited spawns, records how many have spawned
	//could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
	public static enum LimitedDrops {
		//limited world drops
		STRENGTH_CARDS,
		UPGRADE_CARDS,
		PATCHOULI_CARD,
		WEAPON,
		ARMOR,
		HAKKERO,
		DANMAKU,

		//Health potion sources
		//enemies
		SWARM_HP,
		NECRO_HP,
		BAT_HP,
		WARLOCK_HP,
		//Demon spawners are already limited in their spawnrate, no need to limit their health drops
		//alchemy
		COOKING_HP,
		BLANDFRUIT_SEED,

		//Other limited enemy drops
		SLIME_WEP,
		SKELE_WEP,
		THEIF_MISC,
		GUARD_ARM,
		SHAMAN_WAND,
		DM200_EQUIP,
		GOLEM_EQUIP;

		public int count = 0;

		//for items which can only be dropped once, should directly access count otherwise.
		public boolean dropped(){
			return count != 0;
		}
		public void drop(){
			count = 1;
		}

		public static void reset(){
			for (LimitedDrops lim : values()){
				lim.count = 0;
			}
		}

		public static void store( Bundle bundle ){
			for (LimitedDrops lim : values()){
				bundle.put(lim.name(), lim.count);
			}
		}

		public static void restore( Bundle bundle ){
			for (LimitedDrops lim : values()){
				if (bundle.contains(lim.name())){
					lim.count = bundle.getInt(lim.name());
				} else {
					lim.count = 0;
				}

			}
		}

	}

	public static int challenges;
	public static int mobsToChampion;

	public static Hero hero;
	public static Level level;

	public static QuickSlot quickslot = new QuickSlot();

	public static int depth;
	//determines path the hero is on. Current uses:
	// 0 is the default path
	// Other numbers are currently unused
	public static int branch;

	public static int gold;
	public static int energy;

	public static HashSet<Integer> chapters;

	public static SparseArray<ArrayList<Item>> droppedItems;
	public static SparseArray<ArrayList<Item>> portedItems;

	//first variable is only assigned when game is started, second is updated every time game is saved
	public static int initialVersion;
	public static int version;

	public static boolean daily;
	public static String customSeedText = "";
	public static long seed;

	public static void init() {

		initialVersion = version = Game.versionCode;
		challenges = SPDSettings.challenges();
		mobsToChampion = -1;

		if (daily) {
			seed = SPDSettings.lastDaily();
			DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ROOT);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			customSeedText = format.format(new Date(seed));
		} else if (!SPDSettings.customSeed().isEmpty()){
			customSeedText = SPDSettings.customSeed();
			seed = DungeonSeed.convertFromText(customSeedText);
		} else {
			customSeedText = "";
			seed = DungeonSeed.randomSeed();
		}

		Actor.clear();
		Actor.resetNextID();

		//offset seed slightly to avoid output patterns
		Random.pushGenerator( seed+1 );

		Scroll.initLabels();
		Potion.initColors();
		Ring.initGems();

		SpecialRoom.initForRun();
		SecretRoom.initForRun();

		Generator.fullReset();

		Random.resetGenerators();

		Statistics.reset();
		Notes.reset();

		quickslot.reset();
		QuickSlotButton.reset();
		Toolbar.swappedQuickslots = false;

		depth = 1;
		branch = 0;

		gold = 0;
		energy = 0;

		droppedItems = new SparseArray<>();
		portedItems = new SparseArray<>();

		LimitedDrops.reset();

		chapters = new HashSet<>();

		hero = new Hero();
		hero.live();

		Badges.reset();

		GamesInProgress.selectedClass.initHero( hero );
	}

	public static boolean isChallenged( int mask ) {
		return (challenges & mask) != 0;
	}

	public static Level newLevel() {

		Dungeon.level = null;
		Actor.clear();

		if (depth > Statistics.deepestFloor) {
			Statistics.deepestFloor = depth;

			if (Statistics.qualifiedForNoKilling) {
				Statistics.completedWithNoKilling = true;
			} else {
				Statistics.completedWithNoKilling = false;
			}
		}

		Level level;
		switch (depth) {
			case 1:
				if (!Dungeon.isChallenged(Challenges.TUTORIAL)) {
					level = new HakureiShrineLevel();
					break;
				} else {
					level = new TutorialLevel();
					break;
				}
			case 2:
				level = new HakureiShrineLevel();
				break;
			case 3:
			case 4:
			case 5:
				level = new HumanVillageLevel();
				break;
			case 6:
			case 7:
				level = new MistyLakeLevel();
				break;
			case 8:
			case 9:
			case 10:
				level = new ForestofMagicLevel();
				break;
			case 11:
			case 12:
				level = new BambooForestLevel();
				break;
			case 13:
			case 14:
			case 15:
				level = new ScarletDevilMansionLevel();
				break;
			case 16:
			case 17:
				level = new YokaiMountainLevel();
				break;
			case 18:
			case 19:
			case 20:
				level = new GardenoftheSunLevel();
				break;
			case 21:
			case 22:
				level = new MyourenTempleLevel();
				break;
			case 23:
			case 24:
			case 25:
				level = new SanzuRiverLevel();
				break;
			case 26:
			case 27:
				level = new MoriyaShrineLevel();
				break;
			case 28:
			case 29:
			case 30:
				level = new ShiningNeedleCastleLevel();
				break;
			case 31:
			case 32:
				level = new RainbowDragonCaveLevel();
				break;
			case 33:
			case 34:
			case 35:
				level = new LandoftheBackDoorLevel();
				break;
			case 36:
			case 37:
				level = new HakugyokurouLevel();
				break;
			case 38:
			case 39:
			case 40:
				level = new HeavenLevel();
				break;
			case 41:
			case 42:
				level = new LunarCapitalLevel();
				break;
			case 43:
			case 44:
			case 45:
				level = new PrimateSpiritGardenLevel();
				break;
			case 46:
			case 47:
			case 48:
			case 49:
			case 50:
				level = new HellLevel();
				break;
			case 51:
				level = new LastLevel();
				break;
			default:
				level = new DeadEndLevel();
				Statistics.deepestFloor--;
		}

		level.create();

		Statistics.qualifiedForNoKilling = !bossLevel();

		return level;
	}

	public static void resetLevel() {

		Actor.clear();

		level.reset();
		switchLevel( level, level.entrance() );
	}

	public static long seedCurDepth(){
		return seedForDepth(depth, branch);
	}

	public static long seedForDepth(int depth, int branch){
		int lookAhead = depth;
		lookAhead += 30*branch; //Assumes depth is always 1-30, and branch is always 0 or higher

		Random.pushGenerator( seed );

		for (int i = 0; i < lookAhead; i ++) {
			Random.Long(); //we don't care about these values, just need to go through them
		}
		long result = Random.Long();

		Random.popGenerator();
		return result;
	}

	public static boolean shopOnLevel() {
		return depth == 6 || depth == 16 || depth == 26 || depth == 36 || depth == 46;
	}

	public static boolean cardshopOnLevel() {
		return depth == 11 || depth == 21 || depth == 31 || depth == 41;
	}

	public static boolean bossLevel() {
		return bossLevel( depth );
	}

	public static boolean bossLevel( int depth ) {
		return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25 || depth == 30 || depth == 35 || depth == 40 || depth == 45 || depth == 50;
	}

	public static int scalingDepth(){
		return depth;
	}

	public static boolean interfloorTeleportAllowed(){
		if (Dungeon.level.locked || (Dungeon.hero != null && Dungeon.hero.belongings.getItem(Amulet.class) != null)){
			return false;
		}
		return true;
	}

	public static void switchLevel( final Level level, int pos ) {

		if (pos == -2){
			LevelTransition t = level.getTransition(LevelTransition.Type.REGULAR_ENTRANCE);
			if (t != null) pos = t.cell();
		}

		if (pos < 0 || pos >= level.length() || (!level.passable[pos] && !level.avoid[pos])){
			pos = level.getTransition(null).cell();
		}

		PathFinder.setMapSize(level.width(), level.height());

		Dungeon.level = level;
		hero.pos = pos;

		Mob.restoreAllies( level, pos );

		Actor.init();

		level.addRespawner();

		for(Mob m : level.mobs){
			if (m.pos == hero.pos){
				//displace mob
				for(int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(m.pos+i) == null && level.passable[m.pos + i]){
						m.pos += i;
						break;
					}
				}
			}
		}

		Light light = hero.buff( Light.class );
		hero.viewDistance = light == null ? level.viewDistance : Math.max( Light.DISTANCE, level.viewDistance );

		hero.curAction = hero.lastAction = null;

		observe();
		try {
			saveAll();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
			/*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
			But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
		}
	}

	public static void dropToChasm( Item item ) {
		int depth = Dungeon.depth + 1;
		ArrayList<Item> dropped = Dungeon.droppedItems.get( depth );
		if (dropped == null) {
			Dungeon.droppedItems.put( depth, dropped = new ArrayList<>() );
		}
		dropped.add( item );
	}

	public static boolean strengthNeeded() {
		//2 POS each floor set
		int posLeftThisSet = 2 - (LimitedDrops.STRENGTH_CARDS.count - (depth / 5) * 2);
		if (posLeftThisSet <= 0 || Dungeon.depth > 50) return false;

		int floorThisSet = (depth % 5);

		//pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
		int targetPOSLeft = 2 - floorThisSet/2;
		if (floorThisSet % 2 == 1 && Random.Int(2) == 0) targetPOSLeft --;

		if (targetPOSLeft < posLeftThisSet) return true;
		else return false;

	}

	public static boolean upgradeNeeded() {
		int souLeftThisSet;
		souLeftThisSet = 3 - (LimitedDrops.UPGRADE_CARDS.count - (depth / 5) * 3);
		if (souLeftThisSet <= 0 || Dungeon.depth > 50) return false;

		int floorThisSet = (depth % 5);
		//chance is floors left / scrolls left
		return Random.Int(5 - floorThisSet) < souLeftThisSet;
	}

	public static boolean patchouliNeeded() {
		int asLeftThisSet = 1 - (LimitedDrops.PATCHOULI_CARD.count - (depth / 5));
		if (asLeftThisSet <= 0 || Dungeon.depth > 50) return false;

		int floorThisSet = (depth % 5);
		return Random.Int(5 - floorThisSet) < asLeftThisSet;
	}

	private static final String INIT_VER	= "init_ver";
	private static final String VERSION		= "version";
	private static final String SEED		= "seed";
	private static final String CUSTOM_SEED	= "custom_seed";
	private static final String DAILY	    = "daily";
	private static final String CHALLENGES	= "challenges";
	private static final String MOBS_TO_CHAMPION	= "mobs_to_champion";
	private static final String HERO		= "hero";
	private static final String DEPTH		= "depth";
	private static final String BRANCH		= "branch";
	private static final String GOLD		= "gold";
	private static final String ENERGY		= "energy";
	private static final String DROPPED     = "dropped%d";
	private static final String PORTED      = "ported%d";
	private static final String LEVEL		= "level";
	private static final String LIMDROPS    = "limited_drops";
	private static final String CHAPTERS	= "chapters";
	private static final String QUESTS		= "quests";
	private static final String BADGES		= "badges";

	public static void saveGame( int save ) {
		try {
			Bundle bundle = new Bundle();

			bundle.put( INIT_VER, initialVersion );
			bundle.put( VERSION, version = Game.versionCode );
			bundle.put( SEED, seed );
			bundle.put( CUSTOM_SEED, customSeedText );
			bundle.put( DAILY, daily );
			bundle.put( CHALLENGES, challenges );
			bundle.put( MOBS_TO_CHAMPION, mobsToChampion );
			bundle.put( HERO, hero );
			bundle.put( DEPTH, depth );
			bundle.put( BRANCH, branch );

			bundle.put( GOLD, gold );
			bundle.put( ENERGY, energy );

			for (int d : droppedItems.keyArray()) {
				bundle.put(Messages.format(DROPPED, d), droppedItems.get(d));
			}

			for (int p : portedItems.keyArray()){
				bundle.put(Messages.format(PORTED, p), portedItems.get(p));
			}

			quickslot.storePlaceholders( bundle );

			Bundle limDrops = new Bundle();
			LimitedDrops.store( limDrops );
			bundle.put ( LIMDROPS, limDrops );

			int count = 0;
			int ids[] = new int[chapters.size()];
			for (Integer id : chapters) {
				ids[count++] = id;
			}
			bundle.put( CHAPTERS, ids );

			SpecialRoom.storeRoomsInBundle( bundle );
			SecretRoom.storeRoomsInBundle( bundle );

			Statistics.storeInBundle( bundle );
			Notes.storeInBundle( bundle );
			Generator.storeInBundle( bundle );

			Scroll.save( bundle );
			Potion.save( bundle );
			Ring.save( bundle );

			Actor.storeNextID( bundle );

			Bundle badges = new Bundle();
			Badges.saveLocal( badges );
			bundle.put( BADGES, badges );

			FileUtils.bundleToFile( GamesInProgress.gameFile(save), bundle);

		} catch (IOException e) {
			GamesInProgress.setUnknown( save );
			ShatteredPixelDungeon.reportException(e);
		}
	}

	public static void saveLevel( int save ) throws IOException {
		Bundle bundle = new Bundle();
		bundle.put( LEVEL, level );

		FileUtils.bundleToFile(GamesInProgress.depthFile( save, depth, branch ), bundle);
	}

	public static void saveAll() throws IOException {
		if (hero != null && (hero.isAlive())) {

			Actor.fixTime();
			updateLevelExplored();
			saveGame( GamesInProgress.curSlot );
			saveLevel( GamesInProgress.curSlot );

			GamesInProgress.set( GamesInProgress.curSlot, depth, challenges, seed, customSeedText, daily, hero );

		}
	}

	public static void loadGame( int save ) throws IOException {
		loadGame( save, true );
	}

	public static void loadGame( int save, boolean fullLoad ) throws IOException {

		Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.gameFile( save ) );

		//pre-1.3.0 saves
		if (bundle.contains(INIT_VER)){
			initialVersion = bundle.getInt( INIT_VER );
		} else {
			initialVersion = bundle.getInt( VERSION );
		}

		version = bundle.getInt( VERSION );

		seed = bundle.contains( SEED ) ? bundle.getLong( SEED ) : DungeonSeed.randomSeed();
		customSeedText = bundle.getString( CUSTOM_SEED );
		daily = bundle.getBoolean( DAILY );

		Actor.clear();
		Actor.restoreNextID( bundle );

		quickslot.reset();
		QuickSlotButton.reset();
		Toolbar.swappedQuickslots = false;

		Dungeon.challenges = bundle.getInt( CHALLENGES );
		Dungeon.mobsToChampion = bundle.getInt( MOBS_TO_CHAMPION );

		Dungeon.level = null;
		Dungeon.depth = -1;

		Scroll.restore( bundle );
		Potion.restore( bundle );
		Ring.restore( bundle );

		quickslot.restorePlaceholders( bundle );

		if (fullLoad) {

			LimitedDrops.restore( bundle.getBundle(LIMDROPS) );

			chapters = new HashSet<>();
			int ids[] = bundle.getIntArray( CHAPTERS );
			if (ids != null) {
				for (int id : ids) {
					chapters.add( id );
				}
			}

			//Bundle quests = bundle.getBundle( QUESTS );
			//if (!quests.isNull()) {
				//Wandmaker.Quest.restoreFromBundle( quests );
				//Imp.Quest.restoreFromBundle( quests );
			//} else {
				//Wandmaker.Quest.reset();
				//Imp.Quest.reset();
			//}

			SpecialRoom.restoreRoomsFromBundle(bundle);
			SecretRoom.restoreRoomsFromBundle(bundle);
		}

		Bundle badges = bundle.getBundle(BADGES);
		if (!badges.isNull()) {
			Badges.loadLocal( badges );
		} else {
			Badges.reset();
		}

		Notes.restoreFromBundle( bundle );

		hero = null;
		hero = (Hero)bundle.get( HERO );

		depth = bundle.getInt( DEPTH );
		branch = bundle.getInt( BRANCH );

		gold = bundle.getInt( GOLD );
		energy = bundle.getInt( ENERGY );

		Statistics.restoreFromBundle( bundle );
		Generator.restoreFromBundle( bundle );

		droppedItems = new SparseArray<>();
		portedItems = new SparseArray<>();
		for (int i=1; i <= 99; i++) {

			//dropped items
			ArrayList<Item> items = new ArrayList<>();
			if (bundle.contains(Messages.format( DROPPED, i )))
				for (Bundlable b : bundle.getCollection( Messages.format( DROPPED, i ) ) ) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				droppedItems.put( i, items );
			}

			//ported items
			items = new ArrayList<>();
			if (bundle.contains(Messages.format( PORTED, i )))
				for (Bundlable b : bundle.getCollection( Messages.format( PORTED, i ) ) ) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				portedItems.put( i, items );
			}
		}
	}

	public static Level loadLevel( int save ) throws IOException {

		Dungeon.level = null;
		Actor.clear();

		Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.depthFile( save, depth, branch ));

		Level level = (Level)bundle.get( LEVEL );

		if (level == null){
			throw new IOException();
		} else {
			return level;
		}
	}

	public static void deleteGame( int save, boolean deleteLevels ) {

		if (deleteLevels) {
			String folder = GamesInProgress.gameFolder(save);
			for (String file : FileUtils.filesInDir(folder)){
				if (file.contains("depth")){
					FileUtils.deleteFile(folder + "/" + file);
				}
			}
		}

		FileUtils.overwriteFile(GamesInProgress.gameFile(save), 1);

		GamesInProgress.delete( save );
	}

	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.depth = bundle.getInt( DEPTH );
		info.version = bundle.getInt( VERSION );
		info.challenges = bundle.getInt( CHALLENGES );
		info.seed = bundle.getLong( SEED );
		info.customSeed = bundle.getString( CUSTOM_SEED );
		info.daily = bundle.getBoolean( DAILY );

		Hero.preview( info, bundle.getBundle( HERO ) );
		Statistics.preview( info, bundle );
	}

	public static void fail( Class cause ) {
		updateLevelExplored();
		Rankings.INSTANCE.submit( false, cause );
	}

	public static void win( Class cause ) {
		updateLevelExplored();

		hero.belongings.identify();

		if (!Dungeon.isChallenged(Challenges.TUTORIAL)) {
			Rankings.INSTANCE.submit(true, cause);
		}
	}

	public static void updateLevelExplored(){
		if (branch == 0 && level instanceof RegularLevel && !Dungeon.bossLevel()){
			Statistics.floorsExplored.put( depth, level.isLevelExplored(depth));
		}
	}

	//default to recomputing based on max hero vision, in case vision just shrank/grew
	public static void observe(){
		int dist = Math.max(Dungeon.hero.viewDistance, 8);
		dist *= 1f;

		if (Dungeon.hero.buff(MagicalSight.class) != null){
			dist = Math.max( dist, MagicalSight.DISTANCE );
		}

		observe( dist+1 );
	}

	public static void observe( int dist ) {

		if (level == null) {
			return;
		}

		level.updateFieldOfView(hero, level.heroFOV);

		int x = hero.pos % level.width();
		int y = hero.pos / level.width();

		//left, right, top, bottom
		int l = Math.max( 0, x - dist );
		int r = Math.min( x + dist, level.width() - 1 );
		int t = Math.max( 0, y - dist );
		int b = Math.min( y + dist, level.height() - 1 );

		int width = r - l + 1;
		int height = b - t + 1;

		int pos = l + t * level.width();

		for (int i = t; i <= b; i++) {
			BArray.or( level.visited, level.heroFOV, pos, width, level.visited );
			pos+=level.width();
		}

		GameScene.updateFog(l, t, width, height);

		if (hero.buff(MindVision.class) != null){
			for (Mob m : level.mobs.toArray(new Mob[0])){
				BArray.or( level.visited, level.heroFOV, m.pos - 1 - level.width(), 3, level.visited );
				BArray.or( level.visited, level.heroFOV, m.pos - 1, 3, level.visited );
				BArray.or( level.visited, level.heroFOV, m.pos - 1 + level.width(), 3, level.visited );
				//updates adjacent cells too
				GameScene.updateFog(m.pos, 2);
			}
		}

		if (hero.buff(Awareness.class) != null){
			for (Heap h : level.heaps.valueList()){
				BArray.or( level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited );
				BArray.or( level.visited, level.heroFOV, h.pos - 1, 3, level.visited );
				BArray.or( level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited );
				GameScene.updateFog(h.pos, 2);
			}
		}

		for (TalismanOfForesight.CharAwareness c : hero.buffs(TalismanOfForesight.CharAwareness.class)){
			Char ch = (Char) Actor.findById(c.charID);
			if (ch == null || !ch.isAlive()) continue;
			BArray.or( level.visited, level.heroFOV, ch.pos - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, ch.pos - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, ch.pos - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(ch.pos, 2);
		}

		for (TalismanOfForesight.HeapAwareness h : hero.buffs(TalismanOfForesight.HeapAwareness.class)){
			if (Dungeon.depth != h.depth) continue;
			BArray.or( level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, h.pos - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(h.pos, 2);
		}

		for (RevealedArea a : hero.buffs(RevealedArea.class)){
			if (Dungeon.depth != a.depth) continue;
			BArray.or( level.visited, level.heroFOV, a.pos - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, a.pos - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, a.pos - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(a.pos, 2);
		}

		for (Char ch : Actor.chars()){
			if (ch instanceof WandOfWarding.Ward
					|| ch instanceof WandOfRegrowth.Lotus){
				x = ch.pos % level.width();
				y = ch.pos / level.width();

				//left, right, top, bottom
				dist = ch.viewDistance+1;
				l = Math.max( 0, x - dist );
				r = Math.min( x + dist, level.width() - 1 );
				t = Math.max( 0, y - dist );
				b = Math.min( y + dist, level.height() - 1 );

				width = r - l + 1;
				height = b - t + 1;

				pos = l + t * level.width();

				for (int i = t; i <= b; i++) {
					BArray.or( level.visited, level.heroFOV, pos, width, level.visited );
					pos+=level.width();
				}
				GameScene.updateFog(ch.pos, dist);
			}
		}

		GameScene.afterObserve();
	}

	//we store this to avoid having to re-allocate the array with each pathfind
	private static boolean[] passable;

	private static void setupPassable(){
		if (passable == null || passable.length != Dungeon.level.length())
			passable = new boolean[Dungeon.level.length()];
		else
			BArray.setFalse(passable);
	}

	public static PathFinder.Path findPath(Char ch, int to, boolean[] pass, boolean[] vis, boolean chars) {

		setupPassable();
		if (ch.flying || ch.buff( Amok.class ) != null) {
			BArray.or( pass, Dungeon.level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		if (chars) {
			for (Char c : Actor.chars()) {
				if (vis[c.pos]) {
					passable[c.pos] = false;
				}
			}
		}

		return PathFinder.find( ch.pos, to, passable );

	}

	public static int findStep(Char ch, int to, boolean[] pass, boolean[] visible, boolean chars ) {

		if (Dungeon.level.adjacent( ch.pos, to )) {
			return Actor.findChar( to ) == null && (pass[to] || Dungeon.level.avoid[to]) ? to : -1;
		}

		setupPassable();
		if (ch.flying || ch.buff( Amok.class ) != null) {
			BArray.or( pass, Dungeon.level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		if (chars){
			for (Char c : Actor.chars()) {
				if (visible[c.pos]) {
					passable[c.pos] = false;
				}
			}
		}

		return PathFinder.getStep( ch.pos, to, passable );

	}

	public static int flee( Char ch, int from, boolean[] pass, boolean[] visible, boolean chars ) {

		setupPassable();
		if (ch.flying) {
			BArray.or( pass, Dungeon.level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		passable[ch.pos] = true;

		//only consider chars impassable if our retreat path runs into them
		int step = PathFinder.getStepBack( ch.pos, from, passable );
		while (step != -1 && Actor.findChar(step) != null){
			passable[step] = false;
			step = PathFinder.getStepBack( ch.pos, from, passable );
		}
		return step;

	}

}