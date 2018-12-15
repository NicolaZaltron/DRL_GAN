package ch.idsia.ai.agents.human;

import ch.idsia.ai.agents.AgentZelda;
import ch.idsia.zelda.engine.GlobalOptions;
import ch.idsia.zelda.engine.sprites.Zelda;
import ch.idsia.zelda.environments.EnvironmentZelda;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 3:36:16 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class CheaterKeyboardAgentZelda extends KeyAdapter implements AgentZelda {
    private boolean Action[] = null;

    private String Name = "Instance of CheaterKeyboardAgent";
    private Integer prevFPS = 24;

    public CheaterKeyboardAgentZelda()
    {
        reset();
    }

    public void reset()
    {
        // Just check you keyboard.
        Action = new boolean[16];
    }

    public boolean[] getAction(EnvironmentZelda observation)
    {
        return Action;
    }

    public AGENT_TYPE getType() {        return AGENT_TYPE.HUMAN;    }

    public String getName() {   return Name; }

    public void setName(String name) {        Name = name;    }
    

    public void keyPressed (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), true);
    }

    public void keyReleased (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), false);
    }

    private void toggleKey(int keyCode, boolean isPressed)
    {
        switch (keyCode) {
            //Cheats;
            case KeyEvent.VK_D:
                if (isPressed)
                    GlobalOptions.gameViewerTick();
                break;
            case KeyEvent.VK_V:
                if (isPressed)
                    GlobalOptions.VisualizationOn = !GlobalOptions.VisualizationOn;
                break;                        
            case KeyEvent.VK_U:
                Action[Zelda.KEY_LIFE_UP] = isPressed;
                break;
            case KeyEvent.VK_W:
                Action[Zelda.KEY_WIN] = isPressed;
                break;
            case KeyEvent.VK_P:
                if (isPressed)
                {
//                    LOGGER.println("Pause On/Off", LOGGER.VERBOSE_MODE.INFO);
                    GlobalOptions.pauseWorld = !GlobalOptions.pauseWorld;
                    Action[Zelda.KEY_PAUSE] = GlobalOptions.pauseWorld;
                }
                break;
            case KeyEvent.VK_L:
                if (isPressed)
                {
//                    LOGGER.println("Labels On/Off", LOGGER.VERBOSE_MODE.INFO);
                    GlobalOptions.Labels = !GlobalOptions.Labels;
                }
                break;
            case KeyEvent.VK_C:
                if (isPressed)
                {
//                    LOGGER.println("Center On/Off", LOGGER.VERBOSE_MODE.ALL);
                    GlobalOptions.ZeldaAlwaysInCenter = !GlobalOptions.ZeldaAlwaysInCenter;
                }
                break;
            case 61:
                if (isPressed)
                {
//                    LOGGER.println("FPS increase by 1. Current FPS is " + ++GlobalOptions.FPS, LOGGER.VERBOSE_MODE.INFO);
                    GlobalOptions.AdjustZeldaComponentFPS();
                }
                break;
            case 45:
                if (isPressed)
                {
//                    LOGGER.println("FPS decrease . Current FPS is " + --GlobalOptions.FPS, LOGGER.VERBOSE_MODE.INFO);
                    GlobalOptions.AdjustZeldaComponentFPS();
                }
                break;
            case 56:  // chr(56) = 8
                if (isPressed)
                {
                    int temp = prevFPS;
                    prevFPS = GlobalOptions.FPS;
                    GlobalOptions.FPS = (GlobalOptions.FPS == GlobalOptions.InfiniteFPS) ? temp : GlobalOptions.InfiniteFPS ;
//                    LOGGER.println("FPS has been changed. Current FPS is " +
//                            ((GlobalOptions.FPS == GlobalOptions.InfiniteFPS) ? "\\infty" : GlobalOptions.FPS), LOGGER.VERBOSE_MODE.INFO);
                    GlobalOptions.AdjustZeldaComponentFPS();
                }
                break;
        }
    }
}
