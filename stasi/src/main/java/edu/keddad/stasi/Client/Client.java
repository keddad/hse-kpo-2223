package edu.keddad.stasi.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Manager.OrderRequest;
import edu.keddad.stasi.Messaging.OrderEntry;
import edu.keddad.stasi.Messaging.RecipientFinder;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.UUID;

public class Client extends Agent {
    @Override
    protected void setup() {
        // This method should recive json in args (?)

        DFAgentDescription manager = RecipientFinder.findRecipient(this, "manager");

        addBehaviour(new TickerBehaviour(this, 15000) {
            @Override
            protected void onTick() {
                OrderRequest rq = new OrderRequest(new OrderEntry[]{
                        new OrderEntry(1, 2),
                        new OrderEntry(2, 2),
                        new OrderEntry(3, 4),
                });

                try {
                    String message = OrderRequest.mnemonic + " " + new ObjectMapper().writeValueAsString(rq);

                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

                    msg.addReceiver(manager.getName());
                    msg.setContent(message);
                    msg.setReplyWith(UUID.randomUUID().toString());

                    send(msg);

                    System.out.println("Sent a new order to Manager!");

                    myAgent.addBehaviour(new OneShotBehaviour() {
                        @Override
                        public void action() {
                            MessageTemplate mt = MessageTemplate.MatchInReplyTo(msg.getReplyWith());

                            ACLMessage reply = null;

                            while (reply == null) {
                                block();
                                reply = myAgent.receive(mt);
                            }

                            if (reply.getPerformative() == ACLMessage.CONFIRM) {
                                System.out.println("We made an order!");
                            } else if (reply.getPerformative() == ACLMessage.FAILURE) {
                                System.out.println("No order was placed.");
                            } else {
                                throw new RuntimeException("Broken invariant");
                            }
                        }
                    });

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }

}
