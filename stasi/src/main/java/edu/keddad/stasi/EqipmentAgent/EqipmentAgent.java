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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EqipmentAgent extends Agent {
    private MenuEqipment eqipment;
    private Map<Integer, List<MenuEqipment.MenuEquipments>> deleteObjects = new HashMap<Integer, List<MenuEqipment.MenuEquipments>>();

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
                                int deleteId = Integer.parseInt(msg.getReplyWith());
                                reply.setContent(Long.toString(checkReserve(rd, deleteId)));
                                send(reply);

                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (contents.startsWith("delete")) {
                            String deleteId = msg.getReplyWith();
                            ACLMessage reply = msg.createReply();
                            if ((deleteEquipment(Integer.parseInt(deleteId)))) {
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

    private long checkReserve(EqipmentRequest rd, int SpecialId) {

        List<MenuEqipment.MenuEquipments> addEquipment = new ArrayList<MenuEqipment.MenuEquipments>();
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

    private boolean deleteEquipment(int deleteId) {
        List<MenuEqipment.MenuEquipments> deleteEquip = deleteObjects.get(deleteId);

        for (MenuEqipment.MenuEquipments item : deleteEquip) {
            item.ReserveTime = 0;
        }
        return true;
    }

}
