package ch.idsia.zelda.simulation;

import ch.idsia.ai.agents.AgentZelda;
import ch.idsia.zelda.engine.GlobalOptions;
import ch.idsia.zelda.engine.ZeldaComponent;
import ch.idsia.tools.EvaluationInfoZelda;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 7, 2009
 * Time: 2:27:48 PM
 * Package: .Simulation
 */

public class BasicSimulator implements Simulation
{
    SimulationOptions simulationOptions = null;
    private ZeldaComponent zeldaComponent;

    public BasicSimulator(SimulationOptions simulationOptions)
    {
        GlobalOptions.VisualizationOn = simulationOptions.isVisualization();
        this.zeldaComponent = GlobalOptions.getZeldaComponent();
        this.setSimulationOptions(simulationOptions);
    }

    private ZeldaComponent prepareZeldaComponent()
    {
        AgentZelda agent = simulationOptions.getAgent();
        agent.reset();
        zeldaComponent.setAgent(agent);
        return zeldaComponent;
    }

    public void setSimulationOptions(SimulationOptions simulationOptions)
    {
        this.simulationOptions = simulationOptions;
    }

    public EvaluationInfoZelda simulateOneLevel()
    {
        //Zelda.resetStatic(simulationOptions.getZeldaMode());      
        prepareZeldaComponent();
        zeldaComponent.setZLevelScene(simulationOptions.getZLevelMap());
        zeldaComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
        if(simulationOptions.getLevel() != null) { // Added by us: means a Level instance was directly bundled in the simulation options
        	zeldaComponent.startLevel(simulationOptions.getLevelRandSeed(), simulationOptions.getLevelDifficulty()
        			, simulationOptions.getLevelType(), simulationOptions.getLevelLength(),
        			simulationOptions.getTimeLimit(), simulationOptions.getLevel());
        } else if(simulationOptions.getLevelFile().equals("null")){ // The original default behavior: Randomly generates a level
        	zeldaComponent.startLevel(simulationOptions.getLevelRandSeed(), simulationOptions.getLevelDifficulty()
        			, simulationOptions.getLevelType(), simulationOptions.getLevelLength(),
        			simulationOptions.getTimeLimit());
        } else { // Added by us: A json file containing levels has been included in the simulation options
        	zeldaComponent.startLevel(simulationOptions.getLevelRandSeed(), simulationOptions.getLevelDifficulty()
        			, simulationOptions.getLevelType(), simulationOptions.getLevelLength(),
        			simulationOptions.getTimeLimit(), simulationOptions.getLevelFile(), simulationOptions.getLevelIndex());
        }
        zeldaComponent.setPaused(simulationOptions.isPauseWorld());
        zeldaComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
        zeldaComponent.setZLevelScene(simulationOptions.getZLevelMap());
        //zeldaComponent.setZeldaInvulnerable(simulationOptions.isZeldaInvulnerable());
        return zeldaComponent.run1(simulationOptions.currentTrial++,
                simulationOptions.getNumberOfTrials()
        );
    }
}
