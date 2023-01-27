package com.touhoupixel.touhoupixeldungeonreloaded.items.talismans;

import com.touhoupixel.touhoupixeldungeonreloaded.Challenges;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.Statistics;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Actor;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Buff;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Paralysis;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Pure;
import com.touhoupixel.touhoupixeldungeonreloaded.items.NitoChecker;
import com.touhoupixel.touhoupixeldungeonreloaded.items.scrolls.ScrollOfTeleportation;
import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.ItemSpriteSheet;

public class TransientTalisman extends Talisman {
    {
        image = ItemSpriteSheet.TRANSIENT;
    }

    @Override
    protected void onThrow(int cell) {

        Char ch = Actor.findChar(cell);

        if (ch != null && !ch.properties().contains(Char.Property.MINIBOSS) && !ch.properties().contains(Char.Property.BOSS)) {
            if (ch != Dungeon.hero) {
                if (Actor.findChar(Dungeon.level.exit()) == null) {
                    ScrollOfTeleportation.teleportToLocation(ch, Dungeon.level.exit());
                    Buff.prolong(ch, Paralysis.class, Paralysis.DURATION * 10f);
                }
            }
        }
    }

    @Override
    public String info() {

        String info = desc();

        if (Actor.findChar(Dungeon.level.exit()) != null) {
            info += "\n\n" + Messages.get(this, "feel");
        }
        return info;
    }
}