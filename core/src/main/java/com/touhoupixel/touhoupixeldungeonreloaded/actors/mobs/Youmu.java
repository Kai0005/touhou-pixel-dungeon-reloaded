package com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs;

import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.Statistics;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Buff;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Burning;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.DeSlaying;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.exotic.PotionOfPhilosopher;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.YoumuSprite;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.GLog;
import com.watabou.utils.Random;

public class Youmu extends Mob {

    {
        spriteClass = YoumuSprite.class;

        HP = HT = 370;
        defenseSkill = 32;
        EXP = 15;
        maxLvl = 45;

        properties.add(Property.WARP);
        properties.add(Property.POPULAR);

        properties.add(Property.FUMO);
        //used for fumo lover buff

        loot = new PotionOfPhilosopher();
        lootChance = 0.1f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(103, 157);
    }

    @Override
    public int attackSkill(Char target) {
        return 42;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(30, 44);
    }
    @Override
    public float attackDelay() {
        float delay = super.attackDelay();
        if (enemy instanceof Hero) delay /= 2;
        return delay;
    }
    @Override
    public int attackProc(Char hero, int damage) {
        damage = super.attackProc(enemy, damage);
        if (enemy == Dungeon.heroine && enemy.alignment != this.alignment) {
            if (Statistics.difficulty > 2) {
                Buff.affect(enemy, Burning.class).reignite(enemy, 15f);
            }
            if (Statistics.difficulty > 4) {
                Buff.prolong(enemy, DeSlaying.class, DeSlaying.DURATION);
            }
        }
        return damage;
    }
}