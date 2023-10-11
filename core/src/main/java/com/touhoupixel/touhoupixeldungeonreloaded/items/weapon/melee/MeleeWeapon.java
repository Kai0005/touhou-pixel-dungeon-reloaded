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

package com.touhoupixel.touhoupixeldungeonreloaded.items.weapon.melee;

import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Actor;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.RemiliaFate;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.HeroClass;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs.MitamaAra;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs.MitamaKusi;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs.MitamaNigi;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs.MitamaSaki;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Heap;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Item;
import com.touhoupixel.touhoupixeldungeonreloaded.items.weapon.Weapon;
import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.watabou.utils.Random;

public class MeleeWeapon extends Weapon {

	public int tier;

	@Override
	public int min(int lvl) {
		return tier +  //base
				lvl;    //level scaling
	}

	@Override
	public int max(int lvl) {
		return 5 * (tier + 1) +    //base
				lvl * (tier + 1);   //level scaling
	}

	public int STRReq(int lvl) {
		return STRReq(tier, lvl);
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll(owner));
		if (owner.buff(RemiliaFate.class) != null) {
			damage = min() * 2;
		} else if (owner instanceof Hero) {
			int exStr = ((Hero) owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange(0, exStr);
			}
		}

		return damage;
	}

	public int critDamageRoll(Char owner) {
		int damage = augment.damageFactor(Random.IntRange((int) (max()), max()));
		if (owner.buff(RemiliaFate.class) != null) {
			damage = min() * 2;
		} else if (owner instanceof Hero) {
			int exStr = ((Hero) owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange(0, exStr);
			}
		}
		return damage;
	}

	@Override
	public void onThrow(int cell) {
		Heap heap = Dungeon.level.drop(this, cell);
		Char ch = (Char) Actor.findChar(cell);
		if (!heap.isEmpty() && ch != null && ch != Dungeon.heroine) {
			MeleeWeapon meleeWeapon = (MeleeWeapon) curItem;
			if (ch instanceof MitamaAra || ch instanceof MitamaKusi || ch instanceof MitamaNigi || ch instanceof MitamaSaki) {
				ch.damage(0, curUser);
				//zero damage
			} else {
				ch.damage(Random.NormalIntRange(Dungeon.heroine.STR + (meleeWeapon.min() * (meleeWeapon.level() + 1)) + 8 * meleeWeapon.tier,
						Dungeon.heroine.STR + (meleeWeapon.max() * (meleeWeapon.level() + 1)) + 8 * meleeWeapon.tier), curUser);
				//high damage
			}
			Heap[] equipHeaps = new Heap[1];
			equipHeaps[0] = Dungeon.level.heaps.get(ch.pos);
			heap.remove(curItem);
		} else {
			heap.sprite.drop(cell);
		}
	}
	
	@Override
	public String info() {

		String info = desc();
		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (STRReq() > Dungeon.heroine.STR()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (Dungeon.heroine.STR() > STRReq()){
				info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.heroine.STR() - STRReq());
			}
			if (Dungeon.heroine.heroClass == HeroClass.PLAYERYOUMU){
				info += "\n\n" + Messages.get(MeleeWeapon.class, "crit_chance", (int)(Dungeon.heroine.getCritChance()*100));
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
			if (STRReq(0) > Dungeon.heroine.STR()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + enchantment.desc();
		}

		if (cursed && isEquipped( Dungeon.heroine)) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}
	
	@Override
	public int value() {
		int price = 20 * tier;
		if (hasGoodEnchant()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

}
