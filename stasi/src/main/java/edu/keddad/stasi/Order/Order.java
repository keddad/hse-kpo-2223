package edu.keddad.stasi.Order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Dish.DishUtils;
import edu.keddad.stasi.Manager.MenuDishes;
import edu.keddad.stasi.Manager.OrderRequest;
import edu.keddad.stasi.Messaging.YellowBooks;
import edu.keddad.stasi.ResourceReserver.DishReserveRequest;
import edu.keddad.stasi.ResourceReserver.DishReserveResponse;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Order extends Agent {

    void cancelAll(List<String> dishes) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); // no body needed

        dishes.forEach(it -> {
            msg.addReceiver(new AID(it));
        });

        send(msg);
    }

    @Override
    protected void setup() {
        // yada yada order magic

        OrderRequest rd;

        try {
            rd = new ObjectMapper().readValue((String) getArguments()[2], OrderRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        DFAgentDescription manager = YellowBooks.findRecipient(this, "manager"); // if you make a request in class body, everybody dies a horrible death

        String managerConversationId = (String) getArguments()[1];

        String conversation = UUID.randomUUID().toString();

        List<String> dishes = DishUtils.enqueueDishes(rd.items, (String) getArguments()[0], getAID(), conversation);

        // Ensure all dishes are reserved
        addBehaviour(new Behaviour() {

            long endTime = 0;
            int processed = 0;
            boolean ok = true;

            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchInReplyTo(conversation);

                ACLMessage reply = myAgent.receive(mt);

                if (reply == null) {
                    block();
                    return;
                }

                if (reply.getPerformative() == ACLMessage.FAILURE) {
                    ok = false;
                } else {
                    try {
                        DishReserveResponse drp = new ObjectMapper().readValue(reply.getContent(), DishReserveResponse.class);

                        if (drp.endTime > endTime) {
                            endTime = drp.endTime;
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }

                processed += 1;

                if (processed != dishes.size()) {
                    return;
                }

                ACLMessage msg = new ACLMessage(ok ? ACLMessage.CONFIRM : ACLMessage.FAILURE);
                try {
                    msg.setContent(new ObjectMapper().writeValueAsString(new DishReserveResponse(endTime)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                msg.addReceiver(manager.getName());
                msg.setInReplyTo(managerConversationId);

                send(msg);

                if (!ok) {
                    cancelAll(dishes);
                    doDelete();
                }

            }

            @Override
            public boolean done() {
                return processed == dishes.size();
            }
        });

    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }
}
