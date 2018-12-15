package ch.idsia.zelda.engine.level;

import ch.idsia.zelda.engine.LevelScene;
import ch.idsia.zelda.engine.sprites.Enemy;
import ch.idsia.zelda.engine.sprites.Sprite;

public class SpriteTemplate implements Cloneable
{
    public int lastVisibleTick = -1;
    public Sprite sprite;
    public boolean isDead = false;
    private boolean winged;

    public int getType() {
        return type;
    }

    private int type;
    
    public SpriteTemplate(int type, boolean winged)
    {
        this.type = type;
        this.winged = winged;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
    	return super.clone();
    	
    }
    
    public void spawn(LevelScene world, int x, int y, int dir)
    {
        if (isDead) return;
        else
        {
//            sprite = new Enemy(world, x*16+8, y*16+15, dir, type, winged);
            sprite = new Enemy(world, x*16+8, y*16+15, dir, type, winged, x, y);
        }
        sprite.spriteTemplate = this;
        world.addSprite(sprite);
    }
}