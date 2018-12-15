package ch.idsia.zelda.engine.sprites;


import ch.idsia.zelda.engine.Art;
import ch.idsia.zelda.engine.GlobalOptions;
import ch.idsia.zelda.engine.LevelScene;
import ch.idsia.zelda.engine.Scene;
import ch.idsia.zelda.engine.level.Level;


public class Zelda extends Sprite implements Cloneable
{
	//constants for keys
	public static final int KEY_LEFT = 0;
    public static final int KEY_RIGHT = 1;
    public static final int KEY_DOWN = 2;
    public static final int KEY_JUMP = 3;
    public static final int KEY_SPEED = 4;
    public static final int KEY_UP = 5;
    public static final int KEY_PAUSE = 6;
    public static final int KEY_DUMP_CURRENT_WORLD = 7;
    public static final int KEY_LIFE_UP = 8;
    public static final int KEY_WIN = 9;

    //constants for game status
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_WIN = 1;
    public static final int STATUS_DEAD = 0;
    
    //variables for game
    public boolean[] keys;
    public boolean[] cheatKeys;
    private int status = STATUS_RUNNING;
    
    public boolean hasSword = false;
    public boolean usingSword = false;
    public boolean keyCollected = false;
    public int killedEnemies;
    private float swordTime;
    private int moveTime = 0;
    private int moveTimeDelay = 5;
    private float xStepLength = 16;
    private float yStepLength = 16;
    
    public boolean isZeldaInvulnerable;
    
    public int coins = 0;
    public int lives = 1024;
    private final int FractionalPowerUpTime = 0;

    int width = 4;
    int height = 24;

    //public LevelScene world;
    public int facing;
    private int powerUpTime = 0; // exclude pause for rendering changes

    public int xDeathPos, yDeathPos;

    public int deathTime = 0;
    public int winTime = 0;
    private int invulnerableTime = 0;

    public Sprite carried = null;
    private static Zelda instance;

    //TODO: reset zeldaMode possible
    public void resetStatic(int zeldaMode)
    {
        this.usingSword = false;
        this.keyCollected = false;
        this.coins = 0;
        this.killedEnemies = 0;
    }

    public void resetCoins()
    {
        coins = 0;
//        ++numberOfAttempts;
    }


    public Zelda(LevelScene world)
    {
        Zelda.instance = this;
        this.world = world;
        keys = Scene.keys;      // SK: in fact, this is already redundant due to using Agent
        cheatKeys = Scene.keys; // SK: in fact, this is already redundant due to using Agent
        
        //set start coordinates
        //TODO: use levels coord
        x = 32;
        y = 0;
        
        //set sprite properties
        sheet = Art.smallZelda;
        xPicO = 8;
        yPicO = 15;
        wPic = hPic = 16;

        facing = 1;
    }

    public void move()
	{
		xa = 0;
		ya = 0;

		if (cheatKeys[KEY_LIFE_UP]) {
			this.lives++;
		}
		
        world.paused = GlobalOptions.pauseWorld;
        
        if (cheatKeys[KEY_WIN]) {
            win();
        }
        
		if (winTime > 0) {
			winTime++;
			return;
		}

		if (deathTime > 0) {
			deathTime++;
			return;
		}

		if (moveTime > 0) {
			moveTime--;
		}
		if (swordTime > 0) {
			swordTime--;
		}

		if (moveTime < 0) {
			moveTime = 0;
		}
		if (swordTime < 0) {
			swordTime = 0;
		}

		if (invulnerableTime > 0) {
			invulnerableTime--;
		}
        visible = ((invulnerableTime / 2) & 1) == 0;

        if (keys[KEY_JUMP] )
        {
            if (swordTime <= 0)
            {
                swordTime = 1; 	//TODO define constant here
            }
        }

        if (keys[KEY_LEFT] && moveTime <= 0)
        {
            xa -= 10; 			//TODO define constant
        }

        if (keys[KEY_RIGHT] && moveTime <= 0)
        {   
            xa += 10; 			//TODO define constant
        }
        
        if (keys[KEY_UP] && moveTime <= 0)
        {
            ya -= 10; 			//TODO define constant
        }

        if (keys[KEY_DOWN] && moveTime <= 0)
        {   
            ya += 10; 			//TODO define constant
        }
        
        if (xa > 1)
        {
            facing = 1;
        }
        if (xa < 1)
        {
            facing = -1;
        }

        xFlipPic = facing == -1;

        //calculate frame of the sheet
        calcPic();
        
        if(xa > 1) { xa = xStepLength;}
        else if(xa < -1) { xa = -xStepLength;}
        else {xa = 0;}
        if(ya > 1) { ya = xStepLength;}
        else if(ya < -1) { ya = -xStepLength;}
        else {ya = 0;}
       
        System.out.println(xa + " " + ya);
        move(xa, 0);
        move(0, ya);
        xa = 0;
        ya = 0;

        /*if (y > world.level.height * 16 + 16)
        {
            die();
        }*/

        /*if (x > world.level.xExit * 16)
        {
            x = world.level.xExit * 16;
            win();
        }*/

       /* if (x > world.level.width * 16)
        {
            x = world.level.width * 16;
            xa = 0;
        }*/
    }

    private void calcPic()
    {
        int runFrame = 0;
        xPic = runFrame;
    }

    private boolean move(float xa, float ya)
    {
        boolean collide = false;
        if (ya > 0) {
		}
		if (ya < 0) {
		}
		if (xa > 0) {
		}
		if (xa < 0) {
		}
        
        if(!collide && (xa != 0 || ya != 0))
        {
            x += xa;
            y += ya;
            moveTime = moveTimeDelay;
            return true;
        } return false;
    }

    public void KillEnemy(Enemy enemy)
    {
        if (deathTime > 0 || world.paused) return;

        invulnerableTime = 1;
    }

    public void getHurt()
    {
        if (deathTime > 0 || invulnerableTime > 0 || world.paused || isZeldaInvulnerable) return;
            die();
    }

    private void win()
    {
        xDeathPos = (int) x;
        yDeathPos = (int) y;
        world.paused = true;
        winTime = 1;
        status = Zelda.STATUS_WIN;
    }

    public void die()
    {
        xDeathPos = (int) x;
        yDeathPos = (int) y;
        world.paused = true;
        deathTime = 25;
        status = Zelda.STATUS_DEAD;
    }

    public void collectKey()
    {
        if (deathTime > 0 || world.paused || keyCollected) return;

        this.keyCollected = true;
    }

    public byte getKeyMask()
    {
        int mask = 0;
        for (int i = 0; i < 7; i++)
        {
            if (keys[i]) mask |= (1 << i);
        }
        return (byte) mask;
    }

    public void setKeys(byte mask)
    {
        for (int i = 0; i < 7; i++)
        {
            keys[i] = (mask & (1 << i)) > 0;
        }
    }

    public void get1Up()
    {
        lives++;
    }
    
    public void getCoin()
    {
        coins++;
        if (coins % 100 == 0)
            get1Up();
    }

    public int getStatus() {
        return status;
    }
    
    //Added from Baumgarten AStar
    @Override
    public Object clone() throws CloneNotSupportedException
    {
    	Zelda m = (Zelda) super.clone();
    	boolean[] k = new boolean[keys.length];
    	for (int i = 0; i < keys.length; i++)
    		k[i] = keys[i];
    	m.keys = k;
    	return m;    	
    }
}