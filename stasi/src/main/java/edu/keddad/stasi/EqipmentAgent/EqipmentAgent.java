package edu.keddad.stasi.EqipmentAgent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EqipmentAgent extends Agent {
    private MenuEqipment eqipment;
    private final Map<String, List<MenuEqipment.MenuEquipments>> deleteObjects = new HashMap<>();

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
                    if (contents.startsWith("reserve") || contents.startsWith("delete")) {
                        if (contents.startsWith("reserve")) {
                            try {
                                EqipmentRequest rd = new ObjectMapper().readValue(contents.substring(contents.indexOf(' ')), EqipmentRequest.class);
                                ACLMessage reply = msg.createReply();
                                reply.setContent(Long.toString(checkReserve(rd, msg.getReplyWith())));
                                send(reply);

                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (contents.startsWith("delete")) {
                            ACLMessage reply = msg.createReply();
                            if ((deleteEquipment(msg.getReplyWith()))) {
                                reply.setContent("true");
                            } else {
                                reply.setContent("");
                            }
                            send(reply);
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

    private long checkReserve(EqipmentRequest rd, String SpecialId) {

        List<MenuEqipment.MenuEquipments> addEquipment = new ArrayList<>();
        long workTime = 0;
        for (EqipmentRequest.EqipmentEntry req : rd.equipment) {
            MenuEqipment.MenuEquipments betterEquipment = null;
            long minTime = Long.MAX_VALUE;
            for (MenuEqipment.MenuEquipments eq : eqipment.equipment) {
                if (req.type == eq.type && eq.active) {
                    if (eq.ReserveTime < minTime) {
                        minTime = eq.ReserveTime;
                        betterEquipment = eq;

                    }
                }
            }
            assert betterEquipment != null;
            if (betterEquipment.ReserveTime < System.currentTimeMillis()) {
                betterEquipment.ReserveTime = req.CookTime + System.currentTimeMillis();
            } else {
                betterEquipment.ReserveTime += req.CookTime;
            }
            addEquipment.add(betterEquipment);


            if (betterEquipment.ReserveTime > workTime) {
                workTime = betterEquipment.ReserveTime;
            }
        }
        deleteObjects.put(SpecialId, addEquipment);
        return workTime;
    }

    private boolean deleteEquipment(String deleteId) {
        List<MenuEqipment.MenuEquipments> deleteEquip = deleteObjects.get(deleteId);

        for (MenuEqipment.MenuEquipments item : deleteEquip) {
            item.ReserveTime = 0;
        }
        return true;
    }

}
