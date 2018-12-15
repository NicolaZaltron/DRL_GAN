package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.AgentZelda;
import ch.idsia.zelda.environments.EnvironmentZelda;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 12:30:41 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class BasicAIAgentZelda implements AgentZelda
{
    protected boolean action[] = new boolean[EnvironmentZelda.numberOfButtons];
    protected String name = "Instance_of_BasicAIAgent._Change_this_name";

    public BasicAIAgentZelda(String s)
    {
        setName(s);
    }

    public void reset()
    {
        action = new boolean[EnvironmentZelda.numberOfButtons];// Empty action
    }

    public boolean[] getAction(EnvironmentZelda observation)
    {
        return new boolean[EnvironmentZelda.numberOfButtons]; // Empty action
    }

    public AGENT_TYPE getType()
    {
        return AgentZelda.AGENT_TYPE.AI;
    }

    public String getName() {        return name;    }

    public void setName(String Name) { this.name = Name;    }

}
