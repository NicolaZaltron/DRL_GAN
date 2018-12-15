package ch.idsia.zelda.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import ch.idsia.ai.agents.AgentZelda;
import ch.idsia.ai.agents.human.CheaterKeyboardAgentZelda;
import ch.idsia.tools.EvaluationInfoZelda;
import ch.idsia.tools.GameViewer;
import ch.idsia.tools.GameViewerZelda;
import ch.idsia.tools.tcp.ServerAgentZelda;
import ch.idsia.zelda.engine.level.LevelParser;
import ch.idsia.zelda.engine.sprites.Zelda;
import ch.idsia.zelda.environments.EnvironmentZelda;
import reader.JsonReader;


public class ZeldaComponent extends JComponent implements Runnable, /*KeyListener,*/ FocusListener, EnvironmentZelda {
    private static final long serialVersionUID = 790878775993203817L;
    public static final int TICKS_PER_SECOND = 24;

    private boolean running = false;
    private int width, height;
    private GraphicsConfiguration graphicsConfiguration;
    private Scene scene;
    private boolean focused = false;

    int frame;
    int delay;
    Thread animator;

    private int ZLevelEnemies = 1;
    private int ZLevelScene = 1;

    public void setGameViewer(GameViewerZelda gameViewer) {
        this.gameViewer = gameViewer;
    }

    private GameViewerZelda gameViewer = null;

    private AgentZelda agent = null;
    private CheaterKeyboardAgentZelda cheatAgent = null;

    private KeyAdapter prevHumanKeyBoardAgent;
    private LevelScene levelScene = null;

    public ZeldaComponent(int width, int height) {
        adjustFPS();

        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = height;

        Dimension size = new Dimension(width, height);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        setFocusable(true);

        if (this.cheatAgent == null)
        {
            this.cheatAgent = new CheaterKeyboardAgentZelda();
            this.addKeyListener(cheatAgent);
        }        

        GlobalOptions.registerZeldaComponent(this);
    }

    public void adjustFPS() {
        int fps = GlobalOptions.FPS;
        delay = (fps > 0) ? (fps >= GlobalOptions.InfiniteFPS) ? 0 : (1000 / fps) : 100;
//        System.out.println("Delay: " + delay);
    }

    public void paint(Graphics g) {
    }

    public void update(Graphics g) {
    }

    public void init() {
        graphicsConfiguration = getGraphicsConfiguration();
//        if (graphicsConfiguration != null) {
            Art.init(graphicsConfiguration);
//        }
    }

    public void start() {
        if (!running) {
            running = true;
            animator = new Thread(this, "Game Thread");
            animator.start();
        }
    }

    public void stop() {
        running = false;
    }

    public void run() {

    }

    public EvaluationInfoZelda run1(int currentTrial, int totalNumberOfTrials) {
        running = true;
        adjustFPS();
        EvaluationInfoZelda evaluationInfoZelda = new EvaluationInfoZelda();

        VolatileImage image = null;
        Graphics g = null;
        Graphics og = null;

        image = createVolatileImage(320, 240);
        g = getGraphics();
        og = image.getGraphics();

        if (!GlobalOptions.VisualizationOn) {
            String msgClick = "Vizualization is not available";
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
        }

        addFocusListener(this);

        // Remember the starting time
        long tm = System.currentTimeMillis();
        long tick = tm;
        int zeldaStatus = Zelda.STATUS_RUNNING;

        int totalActionsPerfomed = 0;
        int jumpActionsPerformed = 0;
// TODO: Manage better place for this:
        levelScene.zelda.resetCoins();
        LevelScene backup = null;

        while (/*Thread.currentThread() == animator*/ running) {
            // Display the next frame of animation.
//                repaint();
            scene.tick();
            if (gameViewer != null && gameViewer.getContinuousUpdatesState())
                gameViewer.tick();

            float alpha = 0;

//            og.setColor(Color.RED);
            if (GlobalOptions.VisualizationOn) {
                og.fillRect(0, 0, 320, 240);
                scene.render(og, alpha);
            }

            if (agent instanceof ServerAgentZelda && !((ServerAgentZelda) agent).isAvailable()) {
                System.err.println("Agent became unavailable. Simulation Stopped");
                running = false;
                break;
            }

            boolean[] action = agent.getAction(this/*DummyEnvironmentZelda*/);
            System.out.println(agent + " " + action.length);
            if (action != null)
            {
                for (int i = 0; i < EnvironmentZelda.numberOfButtons; ++i){
                    if (action[i])
                    {
                        if(i==Zelda.KEY_JUMP){
                            jumpActionsPerformed++;
                        }
                        ++totalActionsPerfomed;
                        break;
                    }
                }
            }
            else
            {
                System.err.println("Null Action received. Skipping simulation...");
                stop();
            }


            //Apply action;
//            scene.keys = action;
            ((LevelScene) scene).zelda.keys = action;
            ((LevelScene) scene).zelda.cheatKeys = cheatAgent.getAction(this);
            
            //Measure Metrics here:
            /*float diePerc = 0;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                continue;
            }
            for(int i=0; i<=GlobalMetricOptions.numRollouts; i++){
                try{
                    backup = (LevelScene) ((LevelScene) scene).clone();
                }catch (CloneNotSupportedException e)
		{
                    e.printStackTrace();
		}
                            
                for(int j=0; j<=GlobalMetricOptions.rolloutDepth; j++){
                    backup.tick(); 
                    boolean[] action2 = GlobalMetricOptions.roller.getAction(this);
                    //System.out.println("---");
                    backup.zelda.keys = action2;
                    System.out.println("Killed Creatures by Fireball: " + backup.killedCreaturesByFireBall);
                    System.out.println("Killed Creatures by Shell: "+ backup.killedCreaturesByShell);
                    System.out.println("Killed Creatures by Stomp: " +backup.killedCreaturesByStomp);
                    System.out.println("Killed Creatures Total: "+backup.killedCreaturesTotal);
                    System.out.println("Coins: "+ backup.zelda.coins);
                    //TODO vv: coins collected repeatedly
                    System.out.println("Death time: " +backup.zelda.deathTime);
                    System.out.println("Zelda lives: "+backup.zelda.lives);
                    System.out.println("Win Time: "+backup.zelda.winTime);
                    System.out.println("X Death pos: "+ backup.zelda.xDeathPos);
                    System.out.println("Y Death pos: "+ backup.zelda.yDeathPos);
                    if(backup.zelda.deathTime!=0 || backup.zelda.winTime!=0){
                        break;
                    }
                }
                if(backup.zelda.deathTime!=0){
                    diePerc++;
                }
                backup=null;
            }
            diePerc/=GlobalMetricOptions.numRollouts;
            System.out.println(diePerc);*/
            

            if (GlobalOptions.VisualizationOn) {

                String msg = "Agent: " + agent.getName();
                ((LevelScene) scene).drawStringDropShadow(og, msg, 0, 7, 5);

                msg = "Selected Actions: ";
                ((LevelScene) scene).drawStringDropShadow(og, msg, 0, 8, 6);

                msg = "";
                if (action != null)
                {
                    for (int i = 0; i < EnvironmentZelda.numberOfButtons; ++i)
                        msg += (action[i]) ? scene.keysStr[i] : "      ";
                }
                else
                    msg = "NULL";                    
                drawString(og, msg, 6, 78, 1);

                if (!this.hasFocus() && tick / 4 % 2 == 0) {
                    String msgClick = "CLICK TO PLAY";
//                    og.setColor(Color.YELLOW);
//                    og.drawString(msgClick, 320 + 1, 20 + 1);
                    drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
                    drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
                }
                og.setColor(Color.DARK_GRAY);
                ((LevelScene) scene).drawStringDropShadow(og, "FPS: ", 32, 2, 7);
                ((LevelScene) scene).drawStringDropShadow(og, ((GlobalOptions.FPS > 99) ? "\\infty" : GlobalOptions.FPS.toString()), 32, 3, 7);

                msg = totalNumberOfTrials == -2 ? "" : currentTrial + "(" + ((totalNumberOfTrials == -1) ? "\\infty" : totalNumberOfTrials) + ")";

                ((LevelScene) scene).drawStringDropShadow(og, "Trial:", 33, 4, 7);
                ((LevelScene) scene).drawStringDropShadow(og, msg, 33, 5, 7);

                if (width != 320 || height != 240) {
                        g.drawImage(image, 0, 0, 640 * 2, 480 * 2, null);
                } else {
                    g.drawImage(image, 0, 0, null);
                }
            } else {
                // Win or Die without renderer!! independently.
                zeldaStatus = ((LevelScene) scene).zelda.getStatus();
                if (zeldaStatus != Zelda.STATUS_RUNNING)
                    stop();
            }
            // Delay depending on how far we are behind.
            if (delay > 0)
                try {
                    tm += delay;
                    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }
            // Advance the frame
            frame++;
        }
//=========
        evaluationInfoZelda.agentType = agent.getClass().getSimpleName();
        evaluationInfoZelda.agentName = agent.getName();
        evaluationInfoZelda.zeldaStatus = levelScene.zelda.getStatus();
        evaluationInfoZelda.livesLeft = levelScene.zelda.lives;
        evaluationInfoZelda.lengthOfLevelPassedPhys = levelScene.zelda.x;
        evaluationInfoZelda.lengthOfLevelPassedCells = levelScene.zelda.mapX;
        evaluationInfoZelda.totalLengthOfLevelCells = levelScene.level.getWidthCells();
        evaluationInfoZelda.totalLengthOfLevelPhys = levelScene.level.getWidthPhys();
        evaluationInfoZelda.timeSpentOnLevel = levelScene.getStartTime();
        evaluationInfoZelda.timeLeft = levelScene.getTimeLeft();
        evaluationInfoZelda.totalTimeGiven = levelScene.getTotalTime();
        evaluationInfoZelda.numberOfGainedCoins = levelScene.zelda.coins;
//        evaluationInfo.totalNumberOfCoins   = -1 ; // TODO: total Number of coins.
        evaluationInfoZelda.totalActionsPerfomed = totalActionsPerfomed; // Counted during the play/simulation process
        evaluationInfoZelda.jumpActionsPerformed = jumpActionsPerformed; // Counted during play/simulation
        evaluationInfoZelda.totalFramesPerfomed = frame;
        evaluationInfoZelda.killsTotal = levelScene.zelda.world.killedCreaturesTotal;
//        evaluationInfo.Memo = "Number of attempt: " + Zelda.numberOfAttempts;
        if (agent instanceof ServerAgentZelda && levelScene.zelda.keys != null /*this will happen if client quits unexpectedly in case of Server mode*/)
            ((ServerAgentZelda)agent).integrateEvaluationInfo(evaluationInfoZelda);
        return evaluationInfoZelda;
    }

    private void drawString(Graphics g, String text, int x, int y, int c) {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }

    /**
     * Method we added to directly take a Level instance and create it
     * 
     * Many of these leftover parameters actually seem to be irrelevant when we directly specify the level
     * @param seed
     * @param difficulty
     * @param type
     * @param levelLength
     * @param timeLimit
     * @param level
     */
    public void startLevel(long seed, int difficulty, int type, int levelLength, int timeLimit, ch.idsia.zelda.engine.level.Level level) {
        scene = new LevelScene(graphicsConfiguration, this, seed, difficulty, type, levelLength, timeLimit);
        levelScene = ((LevelScene) scene);
        scene.init(level);
    }
    
    /**
     * Method we added to generate a level based on a json file that has been supplied
     * as a command line parameter
	 *
     * Many of these leftover parameters actually seem to be irrelevant when we directly specify the level
	 *
     * @param seed
     * @param difficulty
     * @param type
     * @param levelLength
     * @param timeLimit
     * @param filename
     * @param index
     */
    public void startLevel(long seed, int difficulty, int type, int levelLength, int timeLimit, String filename, int index) {
        scene = new LevelScene(graphicsConfiguration, this, seed, difficulty, type, levelLength, timeLimit);
        levelScene = ((LevelScene) scene);
        JsonReader reader = new JsonReader(filename);
        List<List<Integer>> input = reader.getLevel(index);
        LevelParser parser = new LevelParser();
        ch.idsia.zelda.engine.level.Level level = parser.createLevelJson(input);
        //ch.idsia.zelda.engine.level.Level level = parser.test();
        scene.init(level);
    }
    
    public void startLevel(long seed, int difficulty, int type, int levelLength, int timeLimit) {
        scene = new LevelScene(graphicsConfiguration, this, seed, difficulty, type, levelLength, timeLimit);
        levelScene = ((LevelScene) scene);
        scene.init();
        
        /**
         * From Jacob Schrum
         *
         * To output a useful text representation of whatever level
         * is generated, uncomment the code below and add the appropriate
         * import statements.
         */
//      try {
//          levelScene.level.saveText(new PrintStream(new FileOutputStream("TestLevel.txt")));
//      } catch (IOException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }

    }

    public void levelFailed() {
//        scene = mapScene;
        levelScene.zelda.lives--;
        stop();
    }

    public void focusGained(FocusEvent arg0) {
        focused = true;
    }

    public void focusLost(FocusEvent arg0) {
        focused = false;
    }

    public void levelWon() {
        stop();
//        scene = mapScene;
//        mapScene.levelWon();
    }

    public void toTitle() {
//        Zelda.resetStatic();
//        scene = new TitleScene(this, graphicsConfiguration);
//        scene.init();
    }

    public List<String> getTextObservation(boolean Enemies, boolean LevelMap, boolean Complete, int ZLevelMap, int ZLevelEnemies) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).LevelSceneAroundZeldaASCII(Enemies, LevelMap, Complete, ZLevelMap, ZLevelEnemies);
        else {
            return new ArrayList<String>();
        }
    }

    public String getBitmapEnemiesObservation()
    {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).bitmapEnemiesObservation(1);
        else {
            //
            return new String();
        }                
    }

    public String getBitmapLevelObservation()
    {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).bitmapLevelObservation(1);
        else {
            //
            return null;
        }
    }

    // Chaning ZLevel during the game on-the-fly;
    public byte[][] getMergedObservationZ(int zLevelScene, int zLevelEnemies) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).mergedObservation(zLevelScene, zLevelEnemies);
        return null;
    }

    public byte[][] getLevelSceneObservationZ(int zLevelScene) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).levelSceneObservation(zLevelScene);
        return null;
    }

    public byte[][] getEnemiesObservationZ(int zLevelEnemies) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).enemiesObservation(zLevelEnemies);
        return null;
    }

    public int getKillsTotal() {
        return levelScene.zelda.world.killedCreaturesTotal;
    }

    public int getKillsByFire() {
        return levelScene.zelda.world.killedCreaturesByFireBall;
    }

    public int getKillsByStomp() {
        return levelScene.zelda.world.killedCreaturesByStomp;
    }

    public int getKillsByShell() {
        return levelScene.zelda.world.killedCreaturesByShell;
    }

    public boolean canShoot() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte[][] getCompleteObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).mergedObservation(this.ZLevelScene, this.ZLevelEnemies);
        return null;
    }

    public byte[][] getEnemiesObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).enemiesObservation(this.ZLevelEnemies);
        return null;
    }

    public byte[][] getLevelSceneObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).levelSceneObservation(this.ZLevelScene);
        return null;
    }

    public void setAgent(AgentZelda agent) {
        this.agent = agent;
        if (agent instanceof KeyAdapter) {
            if (prevHumanKeyBoardAgent != null)
                this.removeKeyListener(prevHumanKeyBoardAgent);
            this.prevHumanKeyBoardAgent = (KeyAdapter) agent;
            this.addKeyListener(prevHumanKeyBoardAgent);
        }
    }

    public void setZeldaInvulnerable(boolean invulnerable)
    {
        levelScene.zelda.isZeldaInvulnerable = invulnerable;
    }

    public void setPaused(boolean paused) {
        levelScene.paused = paused;
    }

    public void setZLevelEnemies(int ZLevelEnemies) {
        this.ZLevelEnemies = ZLevelEnemies;
    }

    public void setZLevelScene(int ZLevelScene) {
        this.ZLevelScene = ZLevelScene;
    }

    public float[] getZeldaFloatPos()
    {
        return new float[]{this.levelScene.zelda.x, this.levelScene.zelda.y};
    }

    public float[] getEnemiesFloatPos()
    {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).enemiesFloatPos();
        return null;
    }

    public boolean isZeldaCarrying()
    {
        return levelScene.zelda.carried != null;
    }

	@Override
	public int getZeldaMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isZeldaOnGround() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mayZeldaJump() {
		// TODO Auto-generated method stub
		return false;
	}
}