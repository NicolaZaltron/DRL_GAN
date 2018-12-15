package ch.idsia.ai.agents;

import wox.serial.Easy;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 9, 2009
 * Time: 8:28:06 PM
 * Package: ch.idsia.ai.agents
 */

public final class AgentsPoolZelda
{
    private static AgentZelda currentAgent = null;

    public static void addAgent(AgentZelda agent) {
        agentsHashMap.put(agent.getName(), agent);
    }

	public static void addAgent(String agentWOXName) throws IllegalFormatException
    {
		System.out.println("add Agent");
    	System.out.println(agentWOXName);
        addAgent(load(agentWOXName));
    }

    public static AgentZelda load (String name) {
        AgentZelda agent;
        try {
            agent = (AgentZelda) Class.forName (name).newInstance ();
        }
        catch (ClassNotFoundException e) {
            System.out.println (name + " is not a class name; trying to load a wox definition with that name.");
            agent = (AgentZelda) Easy.load (name);
        }
        catch (Exception e) {
            e.printStackTrace ();
            agent = null;
            System.exit (1);
        }
        return agent;
    }

    public static Collection<AgentZelda> getAgentsCollection()
    {
        return agentsHashMap.values();
    }

    public static Set<String> getAgentsNames()
    {
        return AgentsPool.agentsHashMap.keySet();
    }

    public static AgentZelda getAgentByName(String agentName)
    {
        // There is only one case possible;
        AgentZelda ret = AgentsPoolZelda.agentsHashMap.get(agentName);
        if (ret == null)
            ret = AgentsPoolZelda.agentsHashMap.get(agentName.split(":")[0]);
        return ret;
    }

    public static AgentZelda getCurrentAgent()
    {
        if (currentAgent == null)
        	if(getAgentsCollection().toArray().length > 0) {
        		currentAgent = (AgentZelda) getAgentsCollection().toArray()[0];
        	}
        return currentAgent;
    }

    public static void setCurrentAgent(AgentZelda agent) {
        currentAgent = agent;
    }

    public static void setCurrentAgent(String agentWOXName)
    {
        setCurrentAgent(AgentsPoolZelda.load(agentWOXName));
    }

    static HashMap<String, AgentZelda> agentsHashMap = new LinkedHashMap<String, AgentZelda>();
}
