package edu.keddad.stasi.Messaging;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class YellowBooks {
    // Get an agent of certain type to interact with
    // Wait for its startup if required
    public static DFAgentDescription findRecipient(Agent agent, String recipientType) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(recipientType);
        template.addServices(sd);

        DFAgentDescription[] result = new DFAgentDescription[]{};

        while (result.length == 0) {
            try {
                result = DFService.search(agent, template);
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

        return result[0];
    }

    /**
     * @param agent Agent to register
     * @param recipientType Agent type
     * @param recipientName Agent name (Optional, nullable)
     */
    public static void registerRecipient(Agent agent, String recipientType, String recipientName) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(recipientType);
        sd.setName(recipientName != null ? recipientName : recipientType);

        dfd.addServices(sd);
        try {
            DFService.register(agent, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public static void registerRecipient(Agent agent, String recipientType) {
        registerRecipient(agent, recipientType, null);
    }
}
