package ch.idsia.ai.agents.human;

import ch.idsia.ai.agents.AgentZelda;
import ch.idsia.zelda.engine.sprites.Zelda;
import ch.idsia.zelda.environments.EnvironmentZelda;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 12:19:49 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class HumanKeyboardAgentZelda extends KeyAdapter implements AgentZelda
{
    List<boolean[]> history = new ArrayList<boolean[]>();
    private boolean[] Action = null;
    private String Name = "HumanKeyboardAgent";

    public HumanKeyboardAgentZelda()
    {
        this.reset ();
//        RegisterableAgent.registerAgent(this);
    }

    public void reset()
    {
        // Just check you keyboard. Especially arrow buttons and 'A' and 'S'!
        System.out.println(EnvironmentZelda.numberOfButtons); Action = new boolean[EnvironmentZelda.numberOfButtons];
    }

    public boolean[] getAction(EnvironmentZelda observation)
    {
        float[] enemiesPos = observation.getEnemiesFloatPos();
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
            case KeyEvent.VK_LEFT:
                Action[Zelda.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                Action[Zelda.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                Action[Zelda.KEY_DOWN] = isPressed;
                break;
            case KeyEvent.VK_UP:
                Action[Zelda.KEY_UP] = isPressed;
                break;

            case KeyEvent.VK_S:
                Action[Zelda.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Zelda.KEY_SPEED] = isPressed;
                break;
        }
    }

   public List<boolean[]> getHistory () {
       return history;
   }
}
