package edu.keddad.stasi.Storage;

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

public class Storage extends Agent {
    private MenuStorage stock;
    private final Map<String, List<MenuStorage.Product>> invProducts = new HashMap<>();

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
                                ACLMessage reply = msg.createReply(checkReserve(rd, msg.getReplyWith()) ? ACLMessage.CONFIRM : ACLMessage.FAILURE);
                                send(reply);

                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (contents.startsWith("delete")) {
                            ACLMessage reply = msg.createReply(deleteProducts(msg.getReplyWith()) ? ACLMessage.CONFIRM : ACLMessage.FAILURE);
                            send(reply);
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

    private boolean checkReserve(StorageRequest rd, String deleteId) {
        for (StorageRequest.Product item : rd.products) {
            for (MenuStorage.Product prod : stock.products) {
                if (item.id == prod.type && item.quantity > prod.quantity) {
                    return false;
                }
            }
        }
        List<MenuStorage.Product> delProducts = new ArrayList<>();
        for (StorageRequest.Product item : rd.products) {
            for (MenuStorage.Product prod : stock.products) {
                if (item.id == prod.type && item.quantity <= prod.quantity) {
                    prod.quantity -= item.quantity;
                    delProducts.add(new MenuStorage.Product(prod.id, item.quantity));
                }
            }
        }
        invProducts.put(deleteId, delProducts);
        return true;
    }

    private boolean deleteProducts(String deleteId) {
        List<MenuStorage.Product> delProducts = invProducts.get(deleteId);
        for (MenuStorage.Product adder : delProducts) {
            for (MenuStorage.Product item : stock.products) {
                if (adder.id == item.id) {
                    item.quantity += adder.quantity;
                }
            }
        }
        return true;
    }
}
