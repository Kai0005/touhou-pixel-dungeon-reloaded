package com.touhoupixel.touhoupixeldungeonreloaded.items.tailsmans;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Actor;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Buff;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Drowsy;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Hex;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Poison;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Pure;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Slow;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Vertigo;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Vulnerable;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Weakness;
import com.touhoupixel.touhoupixeldungeonreloaded.items.keys.SkeletonKey;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.PotionOfCirno;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.PotionOfLightHealing;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.PotionOfStrength;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.exotic.PotionOfInvulnerability;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.exotic.PotionOfJunko;
import com.touhoupixel.touhoupixeldungeonreloaded.items.scrolls.ScrollOfTeleportation;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.ExplosiveTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.scenes.GameScene;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class SevenDaysTailsman extends Tailsman {
    {
        image = ItemSpriteSheet.CHAOS;
    }

    @Override
    protected void onThrow(int cell) {

        Char ch = Actor.findChar(cell);

        if (ch != null && !ch.properties().contains(Char.Property.MINIBOSS) && !ch.properties().contains(Char.Property.BOSS)) {
            if (Dungeon.hero.buff(Pure.class) == null) {
                switch (Random.Int(7)) {
                    case 0:
                    default:
                        Buff.affect(ch, Poison.class).set(Dungeon.depth);
                        break;
                    case 1:
                        Buff.prolong(ch, Vertigo.class, Vertigo.DURATION);
                        break;
                    case 2:
                        Buff.prolong(ch, Slow.class, Slow.DURATION);
                        break;
                    case 3:
                        Buff.affect(ch, Drowsy.class);
                        break;
                    case 4:
                        new ExplosiveTrap().set(cell).activate();
                        break;
                    case 5:
                        ScrollOfTeleportation.teleportChar(ch);
                        break;
                    case 6:
                        GameScene.flash(0x80FFFFFF);
                        Sample.INSTANCE.play(Assets.Sounds.BLAST);
                        ch.die(null);
                        if (ch.properties().contains(Char.Property.ELIXIR)) {
                            Dungeon.level.drop(new PotionOfJunko(), ch.pos).sprite.drop();
                        } else Dungeon.level.drop(new PotionOfCirno(), ch.pos).sprite.drop();
                }
            }
        }
    }
}