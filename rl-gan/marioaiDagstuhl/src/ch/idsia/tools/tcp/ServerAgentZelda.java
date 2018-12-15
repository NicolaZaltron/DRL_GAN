package ch.idsia.tools.tcp;

import java.io.IOException;

import ch.idsia.ai.agents.AgentZelda;
import ch.idsia.ai.agents.ai.BasicAIAgentZelda;
import ch.idsia.zelda.environments.EnvironmentZelda;

import ch.idsia.tools.EvaluationInfoZelda;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 30, 2009
 * Time: 9:43:27 PM
 * Package: ch.idsia.tools.Network
 */

public class ServerAgentZelda extends BasicAIAgentZelda implements AgentZelda
{
    Server server = null;
    private int port;
    private TCP_MODE tcpMode = TCP_MODE.SIMPLE_TCP;

    public ServerAgentZelda(int port, boolean enable)
    {
        super("ServerAgent");
        this.port = port;
        if (enable)
        {
            createServer(port);
        }
    }

    public ServerAgentZelda(Server server, boolean isFastTCP)
    {
        super("ServerAgent");
        this.server = server;
        this.tcpMode = (isFastTCP) ? TCP_MODE.FAST_TCP : TCP_MODE.SIMPLE_TCP;
    }

    public String getName()
    {
        return this.name + ((server == null) ? "" : server.getClientName());
    }

    public void setFastTCP(boolean isFastTCP)
    {
        this.tcpMode = (isFastTCP) ? TCP_MODE.FAST_TCP : TCP_MODE.SIMPLE_TCP;
    }

    // A tiny bit of singletone-like concept. Server is created ones for each egent. Basically we are not going
    // To create more than one ServerAgent at a run, but this flexibility allows to add this feature with certain ease.
    private void createServer(int port) {
        this.server = new Server(port, EnvironmentZelda.numberOfObservationElements, EnvironmentZelda.numberOfButtons);
//        this.name += server.getClientName();
    }

    public boolean isAvailable()
    {
        return (server != null) && server.isClientConnected();
    }

    public void reset()
    {
        action = new boolean[EnvironmentZelda.numberOfButtons];
        if (server == null)
            this.createServer(port);
    }

    private void sendRawObservation(EnvironmentZelda observation)
    {
//        byte[][] levelScene = observation.getLevelSceneObservation();
        // MERGED
        byte[][] mergedObs = observation.getCompleteObservation(/*1, 0*/);

        String tmpData = "O " +
                observation.mayZeldaJump() + " " + observation.isZeldaOnGround();
        for (int x = 0; x < mergedObs.length; ++x)
        {
            for (int y = 0; y < mergedObs.length; ++y)
            {
                tmpData += " " + (mergedObs[x][y]);
            }
        }
        tmpData += " " + observation.getZeldaFloatPos()[0]
                 + " " + observation.getZeldaFloatPos()[1];
        
        float[] enemiesFloatPoses = observation.getEnemiesFloatPos();
        for (int i = 0; i < enemiesFloatPoses.length; ++i)
            tmpData += " " + enemiesFloatPoses[i];

        server.sendSafe(tmpData);
        // TODO: StateEncoderDecoder.Encode.Decode.  zip, gzip do not send mario position. zero instead for better compression.
    }

    private void sendObservation(EnvironmentZelda observation)
    {
        if (this.tcpMode == TCP_MODE.SIMPLE_TCP)
        {
            this.sendRawObservation(observation);
        }
        else if (this.tcpMode == TCP_MODE.FAST_TCP)
        {
            this.sendBitmapObservation(observation);
        }
    }

    private void sendBitmapObservation(EnvironmentZelda observation)
    {
        
        String tmpData =  "E" +
                          (observation.mayZeldaJump() ? "1" : "0")  +
                          (observation.isZeldaOnGround() ? "1" : "0") +
                          observation.getBitmapLevelObservation();
//                          observation.getBitmapEnemiesObservation();
        int check_sum = 0;
        for (int i = 3; i < tmpData.length(); ++i)
        {
            char cur_char = tmpData.charAt(i);
            if (cur_char != 0)
            {
//                System.out.print(i + " ");
//                MathX.show(cur_char);
                check_sum += Integer.valueOf(cur_char);
            }
        }
        if (tmpData.length() != /*125 - 61*/34)
            try {
                throw new Exception("Pipetz!");
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.err.println(e.getMessage());
            }
        tmpData += " " + check_sum;
//        System.out.println("tmpData size = " + tmpData.length());
        server.sendSafe(tmpData);
    }

    public void integrateEvaluationInfo(EvaluationInfoZelda evaluationInfo)
    {
        String fitnessStr = "FIT " +
                evaluationInfo.zeldaStatus + " " +
                evaluationInfo.computeDistancePassed() + " " +
                evaluationInfo.timeLeft + " " +
                evaluationInfo.zeldaMode + " " +
                evaluationInfo.numberOfGainedCoins + " ";
        server.sendSafe(fitnessStr);
    }

    private boolean[] receiveAction() throws IOException, NullPointerException
    {
        String data = server.recvSafe();
        if (data == null || data.startsWith("reset"))
            return null;
        boolean[] ret = new boolean[EnvironmentZelda.numberOfButtons];
//        String s = "[";
        for (int i = 0; i < EnvironmentZelda.numberOfButtons; ++i)
        {
            ret[i] = (data.charAt(i) == '1');
//            s += data.charAt(i);
        }
//        s += "]";

//        System.out.println("ServerAgent: action received :" + s);
        return ret;
    }

    public boolean[] getAction(EnvironmentZelda observation)
    {
        try
        {
//            System.out.println("ServerAgent: sending observation...");
//            sendRawObservation(observation);
            sendObservation(observation);
            action = receiveAction();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("I/O Communication Error");
            reset();
        }
        return action;
    }

    public AGENT_TYPE getType()
    {
        return AgentZelda.AGENT_TYPE.TCP_SERVER;
    }
}
