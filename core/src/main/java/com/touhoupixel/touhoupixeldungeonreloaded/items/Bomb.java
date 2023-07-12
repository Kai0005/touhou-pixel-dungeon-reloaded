/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.touhoupixel.touhoupixeldungeonreloaded.items;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.Badges;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.SPDSettings;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Actor;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Zen;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs.Mob;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.CellEmitter;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.particles.BlastParticle;
import com.touhoupixel.touhoupixeldungeonreloaded.effects.particles.SmokeParticle;
import com.touhoupixel.touhoupixeldungeonreloaded.messages.Messages;
import com.touhoupixel.touhoupixeldungeonreloaded.scenes.GameScene;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.ItemSprite;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.ItemSpriteSheet;
import com.touhoupixel.touhoupixeldungeonreloaded.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Bomb extends Item {

    {
        image = ItemSpriteSheet.YOKAI_VIAL;

        defaultAction = AC_LIGHTTHROW;
        usesTargeting = true;

        stackable = true;
    }

    public Fuse fuse;

    //FIXME using a static variable for this is kinda gross, should be a better way
    private static boolean lightingFuse = false;

    private static final String AC_LIGHTTHROW = "LIGHTTHROW";

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && this.fuse == ((Bomb) item).fuse;
    }

    public boolean explodesDestructively(){
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        actions.add ( AC_LIGHTTHROW );
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        if (action.equals(AC_LIGHTTHROW)) {
            lightingFuse = true;
            action = AC_THROW;
        } else
            lightingFuse = false;

        super.execute(hero, action);
    }

    @Override
    protected void onThrow( int cell ) {
        if (!Dungeon.level.pit[ cell ] && lightingFuse) {
            Actor.addDelayed(fuse = new Fuse().ignite(this), 2);
        }
        if (Actor.findChar( cell ) != null && !(Actor.findChar( cell ) instanceof Hero) ){
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8)
                if (Dungeon.level.passable[cell + i])
                    candidates.add(cell + i);
            int newCell = candidates.isEmpty() ? cell : Random.element(candidates);
            Dungeon.level.drop( this, newCell ).sprite.drop( cell );
        } else
            super.onThrow( cell );
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (fuse != null) {
            GLog.w( Messages.get(this, "snuff_fuse") );
            fuse = null;
        }
        return super.doPickUp(hero, pos);
    }

    public void explode(int cell){
        //We're blowing up, so no need for a fuse anymore.
        this.fuse = null;

        Sample.INSTANCE.play( Assets.Sounds.BLAST );

        if (explodesDestructively()) {

            ArrayList<Char> affected = new ArrayList<>();

            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
            }

            boolean terrainAffected = false;
            for (int n : PathFinder.NEIGHBOURS9) {
                int c = cell + n;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Dungeon.level.heroFOV[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                    }

                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy(c);
                        GameScene.updateMap(c);
                        terrainAffected = true;
                    }

                    //destroys items / triggers bombs caught in the blast.
                    Heap heap = Dungeon.level.heaps.get(c);
                    if (heap != null)
                        heap.explode();

                    Char ch = Actor.findChar(c);
                    if (ch != null) {
                        affected.add(ch);
                    }
                }
            }

            for (Char ch : affected){

                //if they have already been killed by another bomb
                if(!ch.isAlive()){
                    continue;
                }

                int dmg = Random.NormalIntRange(5 + Dungeon.scalingFloor()/2, 10 + Dungeon.scalingFloor());

                //those not at the center of the blast take less damage
                if (ch.pos != cell){
                    dmg = Math.round(dmg*0.67f);
                }

                if (ch instanceof Mob){
                    dmg *= 0.5f;
                }

                if (ch.buff(Zen.class) != null){
                    dmg *= 0;
                }

                dmg -= ch.drRoll();

                if (dmg > 0) {
                    ch.damage(dmg, this);
                }

                if (ch == Dungeon.heroine && !ch.isAlive()) {
                    GLog.n(Messages.get(this, "ondeath"));
                    Dungeon.fail(Bomb.class);
                }
            }

            if (terrainAffected) {
                Dungeon.observe();
            }
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public Item random() {
        return this;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return fuse != null ? new ItemSprite.Glowing( 0xFF0000, 0.6f) : null;
    }

    @Override
    public int value() {
        return 20 * quantity;
    }

    @Override
    public String desc() {
        if (fuse == null)
            return super.desc()+ "\n\n" + Messages.get(this, "desc_fuse");
        else
            return super.desc() + "\n\n" + Messages.get(this, "desc_burning");
    }

    private static final String FUSE = "fuse";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( FUSE, fuse );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains( FUSE ))
            Actor.add( fuse = ((Fuse)bundle.get(FUSE)).ignite(this) );
    }

    //used to track the death from friendly magic badge
    public static class MagicalBomb extends Bomb{};

    public static class Fuse extends Actor{

        {
            actPriority = BLOB_PRIO+1; //after hero, before other actors
        }

        private Bomb bomb;

        public Fuse ignite(Bomb bomb){
            this.bomb = bomb;
            return this;
        }

        @Override
        protected boolean act() {

            //something caused our bomb to explode early, or be defused. Do nothing.
            if (bomb.fuse != this){
                Actor.remove( this );
                return true;
            }

            //look for our bomb, remove it from its heap, and blow it up.
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (heap.items.contains(bomb)) {

                    //FIXME this is a bit hacky, might want to generalize the functionality
                    //of bombs that don't explode instantly when their fuse ends
                    heap.remove(bomb);

                    bomb.explode(heap.pos);

                    diactivate();
                    Actor.remove(this);
                    return true;
                }
            }

            //can't find our bomb, something must have removed it, do nothing.
            bomb.fuse = null;
            Actor.remove( this );
            return true;
        }
    }
}