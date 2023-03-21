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
import java.util.HashMap;
import java.util.Map;

public class EqipmentAgent extends Agent {
    private MenuEqipment eqipment;
    private Map<Integer, MenuEqipment.MenuEquipments> eqt;

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "eqipment");

        try {
            eqipment = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), MenuEqipment.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        eqt = new HashMap<Integer, MenuEqipment.MenuEquipments>();
        for (MenuEqipment.MenuEquipments struct : eqipment.equipment) {
            eqt.put(struct.id, struct);
        }
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                ACLMessage msg = receive();
                if (msg != null) {
                    String contents = msg.getContent();
                    if (contents.startsWith(EqipmentRequest.mnemonic)) {
                        try {
                            EqipmentRequest rd = new ObjectMapper().readValue(
                                    contents,
                                    EqipmentRequest.class

                            );
                            toReserve(rd);
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

    private void toReserve(EqipmentRequest rd) {
        for (EqipmentRequest.EqipmentEntry id : rd.items) {
            MenuEqipment.MenuEquipments struct = eqt.get(id.OrderDishId);
            if (!struct.reserve) {
                struct.reserve = true;
            } else {
                // оборудование занято


            }
        }
    }
}
