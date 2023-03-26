package edu.keddad.stasi.Storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.EqipmentAgent.EqipmentRequest;
import edu.keddad.stasi.InstructionStorage.DishInstuctions;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Paths;

public class Storage extends Agent {
    private MenuStorage stock;

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "storage");

        try {
            stock = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), MenuStorage.class);
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
                            StorageRequest rd = new ObjectMapper().readValue(contents.substring(contents.indexOf(' ')), StorageRequest.class);
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

    private boolean checkReserve(StorageRequest rd) {
        for (StorageRequest.Product item : rd.products) {
            for (MenuStorage.Product prod : stock.products) {
                if (item.id == prod.id && item.quantity > prod.quantity) {
                    System.out.println("Конец");
                    return false;
                }
            }
        }

        for (StorageRequest.Product item : rd.products) {
            for (MenuStorage.Product prod : stock.products) {
                if (item.id == prod.id) {
                    System.out.println("Продкт:"+ item.id);
                    System.out.println("количество:"+ prod.quantity);
                    prod.quantity -= item.quantity;
                    System.out.println("Стало:"+ prod.quantity);
                }
            }
        }
        return false;
    }
}
