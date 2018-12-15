package communication;

import ch.idsia.ai.agents.AgentsPoolZelda;
import ch.idsia.ai.agents.human.HumanKeyboardAgentZelda;
import ch.idsia.tools.CmdLineOptionsZelda;
import ch.idsia.tools.EvaluationInfoZelda;
import ch.idsia.tools.EvaluationOptionsZelda;
import ch.idsia.tools.ToolsConfigurator;
import ch.idsia.tools.ToolsConfiguratorZelda;
import ch.idsia.zelda.engine.level.Level;
import ch.idsia.zelda.simulation.BasicSimulator;
import ch.idsia.zelda.simulation.Simulation;
import competition.icegic.robin.AStarAgent;

public class ZeldaProcess extends Comm {
    private EvaluationOptionsZelda evaluationOptions;
    private Simulation simulator;

    public ZeldaProcess() {
        super();
        this.threadName = "ZeldaProcess";
    }

    /**
     * Default mario launcher does not have any command line parameters
     */
    public void launchZelda() {
    	String[] options = new String[] {""};
    	launchZelda(options, false);
    }
 
    /**
     * This version of launching Zelda allows for several parameters
     * @param options General command line options (currently not really used)
     * @param humanPlayer Whether a human is playing rather than a bot
     */
    public void launchZelda(String[] options, boolean humanPlayer) {
        this.evaluationOptions = new CmdLineOptionsZelda(options);  // if none options mentioned, all defaults are used.
        // set agents
        createAgentsPool(humanPlayer);
        // Short time for evolution, but more for human
        if(!humanPlayer) evaluationOptions.setTimeLimit(20);
        // TODO: Make these configurable from commandline?
        evaluationOptions.setMaxFPS(!humanPlayer); // Slow for human players, fast otherwise
        evaluationOptions.setVisualization(true); // Set true to watch evaluations
        // Create Zelda Component
        ToolsConfiguratorZelda.CreateZeldaComponentFrame(evaluationOptions);
        evaluationOptions.setAgent(AgentsPoolZelda.getCurrentAgent());
        System.out.println(evaluationOptions.getAgent().getClass().getName());
        // set simulator
        this.simulator = new BasicSimulator(evaluationOptions.getSimulationOptionsCopy());
    }

    /**
     * Set the agent that is evaluated in the evolved levels
     */
    public static void createAgentsPool(boolean humanPlayer)
    {
    	// Could still generalize this more
    	if(humanPlayer) {
    		AgentsPoolZelda.setCurrentAgent(new HumanKeyboardAgentZelda());
        } else {
        	AgentsPoolZelda.setCurrentAgent(new HumanKeyboardAgentZelda());
        	//TODO create agent for zelda
        	//AgentsPoolZelda.setCurrentAgent(new AStarAgent());
        }
    }

    public void setLevel(Level level) {
        evaluationOptions.setLevel(level);
        this.simulator.setSimulationOptions(evaluationOptions);
    }

    /**
     * Simulate a given level
     * @return
     */
    public EvaluationInfoZelda simulateOneLevel(Level level) {
        setLevel(level);
        EvaluationInfoZelda info = this.simulator.simulateOneLevel();
        return info;
    }

    public EvaluationInfoZelda simulateOneLevel() {
        evaluationOptions.setLevelFile("sample_1.json");
        EvaluationInfoZelda info = this.simulator.simulateOneLevel();
        return info;
    }

    @Override
    public void start() {
        this.launchZelda();
    }

    @Override
    public void initBuffers() {

    }
}
