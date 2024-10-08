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

import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.touhoupixel.touhoupixeldungeonreloaded.scenes.PixelScene;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Badges {

	public enum Badge {
		TALISMAN_1                  ( 0 ),
		TALISMAN_2                  ( 1 ),
		TALISMAN_3                  ( 2 ),
		TALISMAN_4                  ( 3 ),
		TALISMAN_5                  ( 4 ),
		NO_MISS                     ( 5 ),
		NO_BOMB                     ( 6 ),
		NO_ABILITY_CARD             ( 7 );

		public boolean meta;

		public int image;

		Badge( int image ) {
			this( image, false );
		}

		Badge( int image, boolean meta ) {
			this.image = image;
			this.meta = meta;
		}

		public String title(){
			return Messages.get(this, name()+".title");
		}

		public String desc(){
			return Messages.get(this, name()+".desc");
		}

		Badge() {
			this( -1 );
		}
	}

	private static HashSet<Badge> global;
	private static HashSet<Badge> local = new HashSet<>();

	private static boolean saveNeeded = false;

	public static void reset() {
		local.clear();
		loadGlobal();
	}

	public static final String BADGES_FILE	= "badges.dat";
	private static final String BADGES		= "badges";

	private static final HashSet<String> removedBadges = new HashSet<>();
	static{
		//no recently removed badges
	}

	private static final HashMap<String, String> renamedBadges = new HashMap<>();
	static{
		//no recently changed badges
	}

	public static HashSet<Badge> restore( Bundle bundle ) {
		HashSet<Badge> badges = new HashSet<>();
		if (bundle == null) return badges;

		String[] names = bundle.getStringArray( BADGES );
		for (int i=0; i < names.length; i++) {
			try {
				if (renamedBadges.containsKey(names[i])){
					names[i] = renamedBadges.get(names[i]);
				}
				if (!removedBadges.contains(names[i])){
					badges.add( Badge.valueOf( names[i] ) );
				}
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}

		addReplacedBadges(badges);

		return badges;
	}

	public static void store( Bundle bundle, HashSet<Badge> badges ) {
		addReplacedBadges(badges);

		int count = 0;
		String names[] = new String[badges.size()];

		for (Badge badge:badges) {
			names[count++] = badge.toString();
		}
		bundle.put( BADGES, names );
	}

	public static void loadLocal( Bundle bundle ) {
		local = restore( bundle );
	}

	public static void saveLocal( Bundle bundle ) {
		store( bundle, local );
	}

	public static void loadGlobal() {
		if (global == null) {
			try {
				Bundle bundle = FileUtils.bundleFromFile( BADGES_FILE );
				global = restore( bundle );

			} catch (IOException e) {
				global = new HashSet<>();
			}
		}
	}

	public static void saveGlobal() {
		if (saveNeeded) {

			Bundle bundle = new Bundle();
			store( bundle, global );

			try {
				FileUtils.bundleToFile(BADGES_FILE, bundle);
				saveNeeded = false;
			} catch (IOException e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}
	}

	public static int totalUnlocked(boolean global){
		if (global) return Badges.global.size();
		else        return Badges.local.size();
	}

	public static void lifeBind(){
		Badge badge = null;
		if (!Statistics.lifelose && Dungeon.isChallenged(Challenges.URA_MODE)){
			badge = Badge.NO_MISS;
			local.add(badge);
		}
		displayBadge( badge );
	}

	public static void bombBind(){
		Badge badge = null;
		if (!Statistics.spellcarduse && Dungeon.isChallenged(Challenges.URA_MODE)){
			badge = Badge.NO_BOMB;
			local.add(badge);
		}
		displayBadge( badge );
	}

	public static void cardBind(){
		Badge badge = null;
		if (!Statistics.abcarduse && Dungeon.isChallenged(Challenges.URA_MODE)){
			badge = Badge.NO_ABILITY_CARD;
			local.add(badge);
		}
		displayBadge( badge );
	}

	public static void talismanUsed( int talismans ) {
		if (Statistics.talismanUsed > 50) return;
		Badge badge = null;
		if (Statistics.talismanUsed < 50 && Dungeon.isChallenged(Challenges.URA_MODE)){
			badge = Badge.TALISMAN_1;
		}
		if (Statistics.talismanUsed < 40 && Dungeon.isChallenged(Challenges.URA_MODE)){
			unlock(badge);
			badge = Badge.TALISMAN_2;
		}
		if (Statistics.talismanUsed < 30 && Dungeon.isChallenged(Challenges.URA_MODE)){
			unlock(badge);
			badge = Badge.TALISMAN_3;
		}
		if (Statistics.talismanUsed < 20 && Dungeon.isChallenged(Challenges.URA_MODE)){
			unlock(badge);
			badge = Badge.TALISMAN_4;
		}
		if (Statistics.talismanUsed < 10 && Dungeon.isChallenged(Challenges.URA_MODE)){
			unlock(badge);
			badge = Badge.TALISMAN_5;
		}
		local.add(badge);
		displayBadge( badge );
	}

	private static void displayBadge( Badge badge ) {

		if (badge == null || !Dungeon.customSeedText.isEmpty()) {
			return;
		}

		if (isUnlocked( badge )) {

			if (!badge.meta) {
				GLog.h( Messages.get(Badges.class, "endorsed", badge.title()) );
				GLog.newLine();
			}

		} else {

			unlock(badge);

			GLog.h( Messages.get(Badges.class, "new", badge.title() + " (" + badge.desc() + ")") );
			GLog.newLine();
			PixelScene.showBadge( badge );
		}
	}

	public static boolean isUnlocked( Badge badge ) {
		return global.contains( badge );
	}

	public static HashSet<Badge> allUnlocked(){
		loadGlobal();
		return new HashSet<>(global);
	}

	public static void disown( Badge badge ) {
		loadGlobal();
		global.remove( badge );
		saveNeeded = true;
	}

	public static void unlock( Badge badge ){
		if (!isUnlocked(badge) && Dungeon.customSeedText.isEmpty()){
			global.add( badge );
			saveNeeded = true;
		}
	}

	public static List<Badge> filterReplacedBadges( boolean global ) {

		ArrayList<Badge> badges = new ArrayList<>(global ? Badges.global : Badges.local);

		Iterator<Badge> iterator = badges.iterator();
		while (iterator.hasNext()) {
			Badge badge = iterator.next();
			if ((!global && badge.meta) || badge.image == -1) {
				iterator.remove();
			}
		}

		Collections.sort(badges);

		return filterReplacedBadges(badges);

	}

	private static final Badge[][] tierTalismanReplacements = new Badge[][]{
			{Badge.TALISMAN_1, Badge.TALISMAN_2, Badge.TALISMAN_3, Badge.TALISMAN_4, Badge.TALISMAN_5}
	};

	public static List<Badge> filterReplacedBadges( List<Badge> badges ) {

		for (Badge[] tierReplace : tierTalismanReplacements){
			leaveBest( badges, tierReplace );
		}

		return badges;
	}

	private static void leaveBest( Collection<Badge> list, Badge...badges ) {
		for (int i=badges.length-1; i > 0; i--) {
			if (list.contains( badges[i])) {
				for (int j=0; j < i; j++) {
					list.remove( badges[j] );
				}
				break;
			}
		}
	}

	public static List<Badge> filterHigherIncrementalBadges(List<Badges.Badge> badges ) {

		for (Badge[] tierReplace : tierTalismanReplacements){
			leaveWorst( badges, tierReplace );
		}

		Collections.sort( badges );

		return badges;
	}

	private static void leaveWorst( Collection<Badge> list, Badge...badges ) {
		for (int i=0; i < badges.length; i++) {
			if (list.contains( badges[i])) {
				for (int j=i+1; j < badges.length; j++) {
					list.remove( badges[j] );
				}
				break;
			}
		}
	}

	public static Collection<Badge> addReplacedBadges(Collection<Badges.Badge> badges ) {

		for (Badge[] tierReplace : tierTalismanReplacements){
			addLower( badges, tierReplace );
		}

		return badges;
	}

	private static void addLower( Collection<Badge> list, Badge...badges ) {
		for (int i=badges.length-1; i > 0; i--) {
			if (list.contains( badges[i])) {
				for (int j=0; j < i; j++) {
					list.add( badges[j] );
				}
				break;
			}
		}
	}
}