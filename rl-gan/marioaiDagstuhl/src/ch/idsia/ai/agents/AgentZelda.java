package ch.idsia.ai.agents;

import ch.idsia.zelda.environments.EnvironmentZelda;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 8:46:42 PM
 package ch.idsia.ai.agents;
 */
public interface AgentZelda
{
    public enum AGENT_TYPE {AI, HUMAN, TCP_SERVER }

    // clears all dynamic data, such as hidden layers in recurrent networks
    // just implement an empty method for a reactive controller
    
    public void reset();
    
    public boolean[] getAction(EnvironmentZelda observation);

    public AGENT_TYPE getType();

    public String getName();

    public void setName(String name);
}
