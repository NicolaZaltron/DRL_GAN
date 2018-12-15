package ch.idsia.zelda.engine.sprites;

import ch.idsia.zelda.engine.Art;
import ch.idsia.zelda.engine.LevelScene;


public class CoinAnim extends Sprite implements Cloneable
{
    private int life = 16;

    public CoinAnim(LevelScene world, int xTile, int yTile)
    {
        kind = KIND_COIN_ANIM;
        sheet = Art.level;
        wPic = hPic = 16;

        x = xTile * 16;
        y = yTile * 16 - 16;
        xa = 0;
        ya = -6f;
        xPic = 0;
        yPic = 2;
        this.world = world;
    }

    public void move()
    {
        if (life-- < 0)
        {
            this.world.removeSprite(this);
        }

        xPic = life & 3;

        x += xa;
        y += ya;
        ya += 1;
    }
}