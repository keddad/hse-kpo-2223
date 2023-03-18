package edu.keddad.stasi.Messaging;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class RecipientFinder {
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
}
