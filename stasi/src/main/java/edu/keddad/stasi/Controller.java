package edu.keddad.stasi;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public final class Controller {
    public static ContainerController containerController = null;

    // Agents which manage resources and spawn on startup
    private final static Map<String, String[]> singletonAgents = Map.of(
            "edu.keddad.stasi.Manager.Manager", new String[]{"menu_dishes.json"},
            "edu.keddad.stasi.EqipmentAgent.EqipmentAgent", new String[]{"equipment.json", "equipment_type.json"},
            "edu.keddad.stasi.HumanAgent.HumanAgent", new String[]{"cookers.json"},
            "edu.keddad.stasi.InstructionStorage.InstructionStorage", new String[]{"dish_cards.json", "product_types.json"},
            "edu.keddad.stasi.Storage.Storage", new String[]{"products.json", "product_types.json"},
            "edu.keddad.stasi.ResourceReserver.ResourceReserver", new String[]{"operation_types.json"}
    );
    private final static String clientAgent = "edu.keddad.stasi.Client.Client";
    private final static String clientConfigFolder = "Clients";
    private String configPath;
    private String logPath = "log";

    public Controller(String configPath, String logPath) {
        final Runtime rt = Runtime.instance();
        final Profile p = new ProfileImpl();

        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "8080");
        p.setParameter(Profile.GUI, "true");

        containerController = rt.createMainContainer(p);

        if (!configPath.endsWith("/")) { // unix rulez
            configPath = configPath + "/";
        }

        if (!logPath.endsWith("/")) { // unix rulez
            logPath = logPath + "/";
        }

        this.configPath = configPath;
        this.logPath = logPath;
    }

    public void initAgents() throws jade.wrapper.StaleProxyException {

        for (String agent : singletonAgents.keySet()) {
            String[] splitted = agent.split("\\.");
            String agentName = splitted[splitted.length - 1].toLowerCase();

            AgentController ac = containerController.createNewAgent(
                    agentName,
                    agent,
                    Stream.concat(Stream.of(logPath), Arrays.stream(singletonAgents.get(agent)).map((f) -> configPath + f)).toArray()
            );

            ac.start();
        }

        File dir = new File(configPath + clientConfigFolder);

        //noinspection DataFlowIssue
        for (File clientConfig : dir.listFiles()) {
            AgentController ac = containerController.createNewAgent(
                    "agent_" + clientConfig.getName(),
                    clientAgent,
                    new String[]{logPath, clientConfig.toString()}
            );

            ac.start();
        }
    }


}
