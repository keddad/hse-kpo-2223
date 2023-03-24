package edu.keddad.stasi.Messaging;

import edu.keddad.stasi.Controller;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class AgentRuntime {

    // oh god
    public static void createAgent(String className, String agentName, Object[] args ) {
        try {
            AgentController ac = Controller.containerController.createNewAgent(
                    agentName,
                    className,
                    args
            );

            ac.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createAgent(String className, String agentName) {
        createAgent(className, agentName, new Object[]{});
    }
}
