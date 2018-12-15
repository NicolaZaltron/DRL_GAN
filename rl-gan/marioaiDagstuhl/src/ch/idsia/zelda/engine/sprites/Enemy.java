package ch.idsia.zelda.engine.sprites;

import ch.idsia.zelda.engine.Art;
import ch.idsia.zelda.engine.LevelScene;

import java.awt.*;


public class Enemy extends Sprite implements Cloneable
{
    public static final int ENEMY_SLOW = 0;
    public static final int ENEMY_NORMAL = 1;
    public static final int ENEMY_FAST = 2;

    private float runTime;
    private boolean onGround = false;
    private boolean mayJump = false;
    private int jumpTime = 0;
    private float xJumpSpeed;
    private float yJumpSpeed;

    int width = 4;
    int height = 24;

    //private LevelScene world;
    public int facing;
    public int deadTime = 0;
    public boolean flyDeath = false;

    private int type;

    public boolean winged = true;
    private int wingTime = 0;
    
    public boolean noFireballDeath;

    public Enemy(LevelScene world, int x, int y, int dir, int type, boolean winged, int mapX, int mapY)
    {
        byte k = KIND_UNDEF;
        switch (type)
        {
            case ENEMY_SLOW:
                k = (byte) (4 + ((winged) ? 1 : 0));
                break;
            case ENEMY_NORMAL:
                k = (byte) (6 + ((winged) ? 1 : 0));
                break;
            case ENEMY_FAST:
                k = (byte) (2 + ((winged) ? 1 : 0));
                break;
        }
        kind = k;
        this.type = type;
        sheet = Art.enemies;
        this.winged = winged;

        this.x = x;
        this.y = y;
        this.mapX = mapX;
        this.mapY = mapY;
        
        this.world = world;
        xPicO = 8;
        yPicO = 31;

        yPic = type;
        if (yPic > 1) height = 12;
        facing = dir;
        if (facing == 0) facing = 1;
        this.wPic = 16;
    }

    @Override
    public void collideCheck()
    {
        if (deadTime != 0)
        {
            return;
        }

        float xZeldaD = world.zelda.x - x;
        float yZeldaD = world.zelda.y - y;
        float w = 16;
        if (xZeldaD > -width*2-4 && xZeldaD < width*2+4)
        {
            if (yZeldaD > -height && yZeldaD < world.zelda.height)
            {
                if (world.zelda.usingSword)
                {
                    world.zelda.KillEnemy(this);
                    
                        this.yPicO = 31 - (32 - 8);
                        hPic = 8;
                        if (spriteTemplate != null) spriteTemplate.isDead = true;
                        deadTime = 10;
                        winged = false;

//                        System.out.println("collideCheck and stomp");
                        ++world.killedCreaturesTotal;
                }
                else
                {
                    world.zelda.getHurt();
                }
            }
        }
    }

    public void move()
    {
        wingTime++;
        if (deadTime > 0)
        {
            deadTime--;

            if (deadTime == 0)
            {
                deadTime = 1;
                this.world.removeSprite(this);
            }

            if (flyDeath)
            {
                x += xa;
                y += ya;
                ya *= 0.95;
                ya += 1;
            }
            return;
        }


        float sideWaysSpeed = 1.75f;
        //        float sideWaysSpeed = onGround ? 2.5f : 1.2f;

        if (xa > 2)
        {
            facing = 1;
        }
        if (xa < -2)
        {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        mayJump = (onGround);

        xFlipPic = facing == -1;

        runTime += (Math.abs(xa)) + 5;

        int runFrame = ((int) (runTime / 20)) % 2;

        if (!onGround)
        {
            runFrame = 1;
        }


        if (!move(xa, 0)) facing = -facing;
        onGround = false;
        move(0, ya);

        ya *= winged ? 0.95f : 0.85f;

        if (winged) runFrame = wingTime / 4 % 2;

        xPic = runFrame;
    }

    private boolean move(float xa, float ya)
    {
        while (xa > 8)
        {
            if (!move(8, 0)) return false;
            xa -= 8;
        }
        while (xa < -8)
        {
            if (!move(-8, 0)) return false;
            xa += 8;
        }
        while (ya > 8)
        {
            if (!move(0, 8)) return false;
            ya -= 8;
        }
        while (ya < -8)
        {
            if (!move(0, -8)) return false;
            ya += 8;
        }

        boolean collide = false;
        if (ya > 0)
        {
            if (isBlocking(x + xa - width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, xa, ya)) collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, xa, ya)) collide = true;
        }
        if (ya < 0)
        {
            if (isBlocking(x + xa, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
        }
        if (xa > 0)
        {
            if (isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
            if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya)) collide = true;
            if (isBlocking(x + xa + width, y + ya, xa, ya)) collide = true;

            //if (avoidCliffs && onGround && !world.level.isBlocking((int) ((x + xa + width) / 16), (int) ((y) / 16 + 1), xa, 1)) collide = true;
        }
        if (xa < 0)
        {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya)) collide = true;
            if (isBlocking(x + xa - width, y + ya, xa, ya)) collide = true;

            //if (avoidCliffs && onGround && !world.level.isBlocking((int) ((x + xa - width) / 16), (int) ((y) / 16 + 1), xa, 1)) collide = true;
        }

        if (collide)
        {
            if (xa < 0)
            {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            if (xa > 0)
            {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            if (ya < 0)
            {
                y = (int) ((y - height) / 16) * 16 + height;
                jumpTime = 0;
                this.ya = 0;
            }
            if (ya > 0)
            {
                y = (int) (y / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        }
        else
        {
            x += xa;
            y += ya;
            return true;
        }
    }

    private boolean isBlocking(float _x, float _y, float xa, float ya)
    {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) return false;

        boolean blocking = world.level.isBlocking(x, y, xa, ya);

        byte block = world.level.getBlock(x, y);

        return blocking;
    }

    public void bumpCheck(int xTile, int yTile)
    {
        if (deadTime != 0) return;

        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16))
        {
            xa = -world.zelda.facing * 2;
            ya = -5;
            flyDeath = true;
            if (spriteTemplate != null) spriteTemplate.isDead = true;
            deadTime = 100;
            winged = false;
            hPic = -hPic;
            yPicO = -yPicO + 16;
            System.out.println("bumpCheck");
        }
    }

    public void render(Graphics og, float alpha)
    {
        if (winged)
        {
            int xPixel = (int) (xOld + (x - xOld) * alpha) - xPicO;
            int yPixel = (int) (yOld + (y - yOld) * alpha) - yPicO;

            if (type == Enemy.ENEMY_NORMAL || type == Enemy.ENEMY_SLOW)
            {
            }
            else
            {
                xFlipPic = !xFlipPic;
                og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 8, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
                xFlipPic = !xFlipPic;
            }
        }

        super.render(og, alpha);

        if (winged)
        {
            int xPixel = (int) (xOld + (x - xOld) * alpha) - xPicO;
            int yPixel = (int) (yOld + (y - yOld) * alpha) - yPicO;

            if (type == Enemy.ENEMY_NORMAL || type == Enemy.ENEMY_SLOW)
            {
                og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 10, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
            }
            else
            {
                og.drawImage(sheet[wingTime / 4 % 2][4], xPixel + (xFlipPic ? wPic : 0) + (xFlipPic ? 10 : -10), yPixel + (yFlipPic ? hPic : 0) - 8, xFlipPic ? -wPic : wPic, yFlipPic ? -hPic : hPic, null);
            }
        }
    }
}