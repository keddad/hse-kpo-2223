package edu.keddad.stasi.EqipmentAgent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Paths;

public class EqipmentAgent extends Agent {
    private MenuEqipment eqipment;

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "eqipment");

        try {
            eqipment = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), MenuEqipment.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                ACLMessage msg = receive();
                if (msg != null) {
                    String contents = msg.getContent();
                    if (contents.startsWith("reserve")) {
                        try {
                            EqipmentRequest rd = new ObjectMapper().readValue(
                                    contents,
                                    EqipmentRequest.class

                            );
                            ACLMessage reply = msg.createReply();
                            if (checkReserve(rd)) {
                                reply.setPerformative(ACLMessage.CONFIRM);
                            } else {
                                reply.setPerformative(ACLMessage.FAILURE);
                            }
                            send(reply);

                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        block();
                    }
                }
            }
        });
    }


    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }

    private boolean checkReserve(EqipmentRequest rd) {

        for (MenuEqipment.MenuEquipments struct : eqipment.equipment) {

            if (rd.items[0].OrderDishType == struct.type) {
                if (struct.ReserveTime < System.currentTimeMillis()) {
                    struct.ReserveTime = System.currentTimeMillis() + rd.items[0].CookTime;
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

}
