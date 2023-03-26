package edu.keddad.stasi.Manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Dish.DishUtils;
import edu.keddad.stasi.Messaging.AgentRuntime;
import edu.keddad.stasi.Messaging.OrderEntry;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Manager extends Agent {
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
                MessageTemplate mt = MessageTemplate.MatchInReplyTo("");

                ACLMessage msg = receive(mt);

                if (msg != null) {
                    String contents = msg.getContent();

                    if (contents.startsWith(OrderRequest.mnemonic)) {
                        try {
                            OrderRequest rd = new ObjectMapper().readValue(contents.substring(contents.indexOf(' ')), OrderRequest.class);

                            handleOrderRequest(rd, msg);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (contents.startsWith(MenuRequest.mnemonic)) {
                        menuRequest(msg);
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

        try {
            AgentRuntime.createAgent("edu.keddad.stasi.Order.Order", "order_" + msg.getReplyWith(), new String[]{(String) getArguments()[0], msg.getReplyWith(), new ObjectMapper().writeValueAsString(rd)});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchInReplyTo(msg.getReplyWith());

                ACLMessage replyFromOrder = null;

                while (replyFromOrder == null) {
                    block();
                    replyFromOrder = myAgent.receive(mt);
                }

                ACLMessage replyToClient = msg.createReply();
                replyToClient.setPerformative(replyFromOrder.getPerformative());
                replyToClient.setContent(replyFromOrder.getContent());
                send(replyToClient);
            }
        });
    }

    private void menuRequest(ACLMessage clientMsg) {
        System.out.println("Manager got menuRequest");

        List<MenuDishes.MenuDish> activeDishes = Arrays.stream(menu.dishes).toList().stream().filter(it -> {
            return it.active;
        }).toList();

        OrderEntry[] orderEntries = activeDishes.stream().map(it -> {
            return new OrderEntry(it.id, it.id);
        }).toArray(OrderEntry[]::new);

        String conversation = UUID.randomUUID().toString();
        DishUtils.enqueueDishes(orderEntries, (String) getArguments()[0], getAID(), conversation);

        List<Integer> succeededReservations = new ArrayList<>();

        addBehaviour(new Behaviour() {
            int processedResponses = 0;

            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchInReplyTo(conversation);

                ACLMessage msg = receive(mt);

                if (msg != null) {
                    processedResponses += 1;

                    if (msg.getPerformative() == ACLMessage.CONFIRM) {
                        send(msg.createReply(ACLMessage.REQUEST));

                        String[] splitName = msg.getSender().getName().split("_"); // this whole thing is the worst code i wrote in years
                        succeededReservations.add(Integer.parseInt(splitName[splitName.length - 1].split("@")[0]));
                    }

                    if (done()) {
                        ACLMessage clientResponse = clientMsg.createReply();
                        MenuResponse rp = new MenuResponse(succeededReservations.stream().map(it -> {
                            return new MenuResponse.Dish(it, Arrays.stream(menu.dishes).filter(d -> {
                                return d.id == it;
                            }).findFirst().get().price);
                        }).toArray(MenuResponse.Dish[]::new));

                        try {
                            clientResponse.setContent(new ObjectMapper().writeValueAsString(rp));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        send(clientResponse);
                    }
                } else {
                    block();
                }
            }

            @Override
            public boolean done() {
                return processedResponses == orderEntries.length;
            }
        });
    }

}
