package com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.Statistics;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Buff;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Degrade;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Hex;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.PotionFreeze;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.RegenBlock;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.HeroClass;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.CellEmitter;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.particles.ShadowParticle;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Gold;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.PotionOfHealing;
import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.JoonSprite;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Joon extends Mob {

    {
        spriteClass = JoonSprite.class;

        HP = HT = 75;
        defenseSkill = 20;
        EXP = 12;
        maxLvl = 27;

        properties.add(Property.GOD);

        loot = Gold.class;
        lootChance = 0.1f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(8, 14);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc( Char hero, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (enemy == Dungeon.hero && enemy.alignment != this.alignment && Random.Int(5) == 0) {
            if (Dungeon.gold > 2000) {
                Dungeon.gold -= 2000;
                PotionOfHealing poh = new PotionOfHealing();
                poh.collect();
                Sample.INSTANCE.play(Assets.Sounds.GOLD);
                CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
                GLog.w(Messages.get(this, "losemoney"));
            }
            if (Statistics.difficulty > 2) {
                Buff.prolong(enemy, PotionFreeze.class, PotionFreeze.DURATION);
            }
            if (Statistics.difficulty > 4) {
                Buff.prolong(enemy, Degrade.class, Degrade.DURATION);
            }
        }
        return damage;
    }
}