package edu.keddad.stasi.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Manager.MenuRequest;
import edu.keddad.stasi.Manager.MenuResponse;
import edu.keddad.stasi.Manager.OrderRequest;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

public class Client extends Agent {
    private ClientPull client;

    @Override
    protected void setup() {

        DFAgentDescription manager = YellowBooks.findRecipient(this, "manager");
        try {
            client = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), ClientPull.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OrderRequest rq = new OrderRequest(false, client.ord_dishes);
        try {
            String message = OrderRequest.mnemonic + " " + new ObjectMapper().writeValueAsString(rq);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

            msg.addReceiver(manager.getName());
            msg.setContent(message);
            msg.setReplyWith(UUID.randomUUID().toString());
            msg.setInReplyTo(""); // workaround for message filtering

            send(msg);

            System.out.println("Sent a new order to Manager!");

            addBehaviour(new Behaviour() {

                boolean processed = false;

                @Override
                public void action() {
                    MessageTemplate mt = MessageTemplate.MatchInReplyTo(msg.getReplyWith());

                    ACLMessage reply = myAgent.receive(mt);


                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.CONFIRM) {

                            System.out.println("We made an order!");
                        } else if (reply.getPerformative() == ACLMessage.FAILURE) {
                            System.out.println("No order was placed.");
                        } else {
                            throw new RuntimeException("Broken invariant");
                        }

                        processed = true;
                    } else {
                        block();
                    }
                }

                @Override
                public boolean done() {
                    return processed;
                }
            });

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        // We ask for menu to demonstrate that we can.
        // Yes, we can.
        addBehaviour(new TickerBehaviour(this, 120000) {
            @Override
            protected void onTick() {
                String message = MenuRequest.mnemonic;

                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

                msg.addReceiver(manager.getName());
                msg.setContent(message);
                msg.setReplyWith(UUID.randomUUID().toString());
                msg.setInReplyTo(""); // workaround for message filtering

                send(msg);

                System.out.println("Sent a new MenuRequest to Manager!");

                myAgent.addBehaviour(new Behaviour() {

                    boolean processed = false;

                    @Override
                    public void action() {
                        MessageTemplate mt = MessageTemplate.MatchInReplyTo(msg.getReplyWith());

                        ACLMessage reply = myAgent.receive(mt);

                        if (reply != null) {
                            try {
                                MenuResponse mrp = new ObjectMapper().readValue(reply.getContent(), MenuResponse.class);
                                System.out.println("Currently have this menu items available: " + Arrays.stream(mrp.items).map(it -> ((Integer) it.id).toString()).toList());
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }

                            processed = true;
                        } else {
                            block();
                        }

                    }

                    @Override
                    public boolean done() {
                        return processed;
                    }
                });
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }

}
