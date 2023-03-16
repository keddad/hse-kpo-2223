package edu.keddad.stasi;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.List;

public class Controller {
    private final ContainerController containerController;

    // Agents which manage resources and spawn on startup
    private static final List<String> singletonAgents = List.of("edu.keddad.stasi.Manager.Manager", "edu.keddad.stasi.Client.Client");
    public Controller() {
        final Runtime rt = Runtime.instance();
        final Profile p = new ProfileImpl();

        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "8080");
        p.setParameter(Profile.GUI, "true");

        containerController = rt.createMainContainer(p);
    }

    public void initAgents() throws jade.wrapper.StaleProxyException {

        for (String agent : singletonAgents) {
            String[] splitted = agent.split("\\.");
            String agentName = splitted[splitted.length - 1].toLowerCase();

            AgentController ac = containerController.createNewAgent(
                    agentName,
                    agent,
                    null
            );
            ac.start();
        }
    }


}
