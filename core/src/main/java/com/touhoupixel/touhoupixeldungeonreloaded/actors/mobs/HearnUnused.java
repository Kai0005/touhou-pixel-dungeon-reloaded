package com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.CellEmitter;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.particles.ShadowParticle;
import com.touhoupixel.touhoupixeldungeonreloaded.items.itemstats.LifeFragment;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.AkyuuSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class HearnUnused extends Mob {

    {
        spriteClass = AkyuuSprite.class;

        HP = HT = 200;
        defenseSkill = 30;
        EXP = 14;
        maxLvl = 30;

        properties.add(Property.HUMAN);

        properties.add(Property.FUMO);
        //used for fumo lover buff

        loot = new LifeFragment();
        lootChance = 0.04f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(34, 39);
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc(Char hero, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(0) == 0) {
            if (HP > 3) {
                HP = HP / 2;
                Sample.INSTANCE.play(Assets.Sounds.CURSED);
                CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
            }
        }
        return damage;
    }
}