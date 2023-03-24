package edu.keddad.stasi.Order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Manager.MenuDishes;
import edu.keddad.stasi.Manager.OrderRequest;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class Order extends Agent{
    @Override
    protected void setup() {
        // yada yada order magic

        try {
            OrderRequest rd = new ObjectMapper().readValue((String) getArguments()[2], OrderRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        DFAgentDescription manager = YellowBooks.findRecipient(this, "manager"); // if you make a request in class body, everybody dies a horrible death

        String managerConversationId =  (String) getArguments()[1];

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);

                msg.addReceiver(manager.getName());
                msg.setInReplyTo(managerConversationId);

                if (ThreadLocalRandom.current().nextInt(1, 3) == 1) {
                    msg.setPerformative(ACLMessage.CONFIRM);
                } else {
                    msg.setPerformative(ACLMessage.FAILURE);
                }

                System.out.println("Sending message to Manager from Order!");
                send(msg);
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }
}
