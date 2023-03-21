package edu.keddad.stasi.Manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class Manager extends Agent{
    private MenuDishes menu;
    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "manager");

        try {
            menu = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), MenuDishes.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String contents = msg.getContent();

                    if (contents.startsWith(OrderRequest.mnemonic)) {
                        try {
                            OrderRequest rd = new ObjectMapper().readValue(
                                    contents.substring(contents.indexOf(' ')),
                                    OrderRequest.class
                            );

                            handleOrderRequest(rd, msg);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }


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

    private void handleOrderRequest(OrderRequest rd, ACLMessage msg) {
        System.out.printf("Got order request with %d items!\n", rd.items.length);

        ACLMessage reply = msg.createReply();

        if (ThreadLocalRandom.current().nextInt(0, 2) == 1) {
            reply.setPerformative(ACLMessage.CONFIRM);
        } else {
            reply.setPerformative(ACLMessage.FAILURE);
        }

        send(reply);
        System.out.println("Sending order response message!");
    }

}
