package edu.keddad.stasi.ResourceReserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Manager.OrderRequest;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.concurrent.ThreadLocalRandom;

public class ResourceReserver extends Agent {
    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "resourcereserver");
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    ACLMessage response = msg.createReply();

                    if (ThreadLocalRandom.current().nextInt(1, 3) == 1) {
                        response.setPerformative(ACLMessage.FAILURE);
                    } else {
                        response.setPerformative(ACLMessage.CONFIRM);
                        try {
                            response.setContent(new ObjectMapper().writeValueAsString(new DishReserveResponse(123)));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    send(response);

                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }
}
