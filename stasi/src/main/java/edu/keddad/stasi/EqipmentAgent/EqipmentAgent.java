package edu.keddad.stasi.EqipmentAgent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Manager.OrderRequest;
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
                            EqipmentRequest rd = new ObjectMapper().readValue(contents.substring(contents.indexOf(' ')), EqipmentRequest.class);
                            ACLMessage reply = msg.createReply();
                            if (checkReserve(rd)) {
                                reply.setContent("true");
                            } else {
                                reply.setContent("");
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

        for (EqipmentRequest.EqipmentEntry req : rd.equipment) {
            for (MenuEqipment.MenuEquipments eq : eqipment.equipment) {
                if (req.OrderDishType == eq.type && eq.active) {
                    if (eq.ReserveTime < System.currentTimeMillis()) {
                        eq.ReserveTime = System.currentTimeMillis() + req.CookTime;
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        return false;
    }

}
