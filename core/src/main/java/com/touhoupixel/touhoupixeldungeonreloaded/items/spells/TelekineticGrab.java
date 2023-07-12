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

package com.touhoupixel.touhoupixeldungeonreloaded.items.spells;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Actor;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.PinCushion;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.MagicMissile;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Heap;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Item;
import com.touhoupixel.touhoupixeldungeonreloaded.items.LiquidMetal;
import com.touhoupixel.touhoupixeldungeonreloaded.mechanics.Ballistica;
import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.ItemSpriteSheet;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class TelekineticGrab extends TargetedSpell {

	{
		image = ItemSpriteSheet.TELE_GRAB;
	}

	@Override
	protected void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.BEACON,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	protected void affectTarget(Ballistica bolt, Hero heroine) {
		Char ch = Actor.findChar(bolt.collisionPos);

		//special logic for DK when he is on his throne
		if (ch == null && bolt.path.size() > bolt.dist+1){
			ch = Actor.findChar(bolt.path.get(bolt.dist+1));
			if (Dungeon.level.solid[ch.pos]){
				ch = null;
			}
		}

		if (ch != null && ch.buff(PinCushion.class) != null){

			Item item = ch.buff(PinCushion.class).grabOne();

			if (item.doPickUp(heroine, ch.pos)){
				heroine.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn

			} else {
				GLog.w(Messages.get(this, "cant_grab"));
				Dungeon.level.drop(item, ch.pos).sprite.drop();
				return;
			}

		} else if (Dungeon.level.heaps.get(bolt.collisionPos) != null){

			Heap h = Dungeon.level.heaps.get(bolt.collisionPos);

			if (h.type != Heap.Type.HEAP){
				GLog.w(Messages.get(this, "cant_grab"));
				h.sprite.drop();
				return;
			}

			Item item = h.peek();

			if (item.doPickUp(heroine, h.pos)){
				h.pickUp();
				heroine.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn

			} else {
				GLog.w(Messages.get(this, "cant_grab"));
				h.sprite.drop();
				return;
			}

		} else {
			GLog.w(Messages.get(this, "no_target"));
		}

	}

	@Override
	public int value() {
		//prices of ingredients, divided by output quantity (rounded up slightly)
		return Math.round(quantity * ((5 + 40) / 6f));
	}

	public static class Recipe extends com.touhoupixel.touhoupixeldungeonreloaded.items.Recipe.SimpleRecipe {

		{
			inputs =  new Class[]{LiquidMetal.class, ArcaneCatalyst.class};
			inQuantity = new int[]{10, 1};

			cost = 2;

			output = TelekineticGrab.class;
			outQuantity = 6;
		}

	}

}