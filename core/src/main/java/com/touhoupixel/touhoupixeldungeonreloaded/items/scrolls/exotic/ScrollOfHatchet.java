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

package com.touhoupixel.touhoupixeldungeonreloaded.items.scrolls.exotic;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Buff;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.CritChanceUp;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.Flare;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ScrollOfHatchet extends ExoticScroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_CRIT;
	}

	@Override
	public void doRead() {
		new Flare(5, 32).color(0xFF0000, true).show(curUser.sprite, 2f);
		Sample.INSTANCE.play(Assets.Sounds.READ);

		Buff.prolong(curUser, CritChanceUp.class, CritChanceUp.DURATION);

		identify();

		readAnimation();
	}
}