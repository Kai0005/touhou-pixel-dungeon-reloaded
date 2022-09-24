/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.touhoupixel.touhoupixeldungeonreloaded.levels.traps;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.HeroClass;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.CellEmitter;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.Speck;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Heap;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Item;
import com.touhoupixel.touhoupixeldungeonreloaded.items.armor.Armor;
import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.touhoupixel.touhoupixeldungeonreloaded.scenes.GameScene;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class DestroyArmorTrap extends Trap {

	{
		color = RED;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		Heap heap = Dungeon.level.heaps.get(pos);

		if (heap != null) {
			int cell = Dungeon.level.randomRespawnCell(null);

			if (cell != -1) {
				Item item = heap.pickUp();
				Heap dropped = Dungeon.level.drop(item, cell);
				dropped.type = heap.type;
				dropped.sprite.view(dropped);
				dropped.seen = true;
				for (int i : PathFinder.NEIGHBOURS9) Dungeon.level.visited[cell + i] = true;
				GameScene.updateFog();

				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
			}
		}

		if (Dungeon.hero.pos == pos && !Dungeon.hero.flying) {
			Hero hero = Dungeon.hero;
			Armor armor = hero.belongings.armor;

			if (armor != null && !armor.cursed && hero.belongings.armor.level() < 3 && ((Armor) armor).checkSeal() == null) {

				int cell;
				int tries = 20;
				do {
					cell = Dungeon.level.randomRespawnCell(null);
					if (tries-- < 0 && cell != -1) break;

					PathFinder.buildDistanceMap(pos, Dungeon.level.passable);
				} while (cell == -1 || PathFinder.distance[cell] < 10 || PathFinder.distance[cell] > 20);

				hero.belongings.armor = null;
				Dungeon.quickslot.clearItem(armor);
				armor.updateQuickslot();

				Sample.INSTANCE.play(Assets.Sounds.CURSED);
				CellEmitter.get(pos).burst(Speck.factory(Speck.DUST), 4);
				GLog.w(Messages.get(this, "disarm"));
			} else if (armor != null && !armor.cursed && hero.belongings.armor.level() < 5 && Dungeon.depth == 40 && ((Armor) armor).checkSeal() == null) {

				int cell;
				int tries = 20;
				do {
					cell = Dungeon.level.randomRespawnCell(null);
					if (tries-- < 0 && cell != -1) break;

					PathFinder.buildDistanceMap(pos, Dungeon.level.passable);
				} while (cell == -1 || PathFinder.distance[cell] < 10 || PathFinder.distance[cell] > 20);

				hero.belongings.armor = null;
				Dungeon.quickslot.clearItem(armor);
				armor.updateQuickslot();

				Sample.INSTANCE.play(Assets.Sounds.CURSED);
				CellEmitter.get(pos).burst(Speck.factory(Speck.DUST), 4);
				GLog.w(Messages.get(this, "disarm"));
			} else if (armor == null) {
				GLog.w(Messages.get(this, "noarmor"));
			} else {
				GLog.w(Messages.get(this, "disarmfailed"));
			}
		}
	}
}