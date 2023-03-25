package edu.keddad.stasi.Dish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Messaging.YellowBooks;
import edu.keddad.stasi.ResourceReserver.DishCancelRequest;
import edu.keddad.stasi.ResourceReserver.DishReserveRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.UUID;

public class Dish extends Agent {
    private AID parentAgent;
    private String parentConversationId;

    private String logPath;

    private boolean kurtShotgun;

    private int dishId;

    DFAgentDescription resourceReserver;

    private void tryReserve() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

                msg.addReceiver(resourceReserver.getName());
                msg.setReplyWith(UUID.randomUUID().toString());
                try {
                    msg.setContent(DishReserveRequest.mnemonic + " " + new ObjectMapper().writeValueAsString(new DishReserveRequest(dishId)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                send(msg);

                myAgent.addBehaviour(new Behaviour() {
                    private boolean isDone = false;
                    @Override
                    public void action() {
                        MessageTemplate mt = MessageTemplate.MatchInReplyTo(msg.getReplyWith());

                        ACLMessage reply = myAgent.receive(mt);

                        if (reply == null) {
                            block();
                            return;
                        }

                        ACLMessage replyMsg = new ACLMessage(reply.getPerformative());
                        replyMsg.setContent(reply.getContent());
                        replyMsg.addReceiver(parentAgent);
                        replyMsg.setInReplyTo(parentConversationId);
                        send(replyMsg);

                        if (reply.getPerformative() == ACLMessage.FAILURE || kurtShotgun) {
                            doDelete();
                        }

                        isDone = true;
                    }

                    @Override
                    public boolean done() {
                        return isDone;
                    }
                });
            }
        });
    }

    private void tryCancel() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

        msg.addReceiver(resourceReserver.getName());

        msg.setContent(DishCancelRequest.mnemonic);
        send(msg);
        doDelete();
    }

    @Override
    protected void setup() {
        resourceReserver = YellowBooks.findRecipient(this, "resourcereserver");
        logPath = (String) getArguments()[0]; // Idet Object Object Object, vidit Object - Object v Object.
        parentAgent = (AID) getArguments()[1]; // For some reason my code should be US-ASCII, otherwise everyone dies
        parentConversationId = (String) getArguments()[2];
        dishId = (int) getArguments()[3];

        tryReserve();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.MatchSender(parentAgent)
                );

                ACLMessage reply = myAgent.receive(mt);
                if (reply == null) {
                    block();
                    return;
                }

                tryCancel();
            }
        });
    }
}
