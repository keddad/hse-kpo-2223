package edu.keddad.stasi.InstructionStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Manager.OrderRequest;
import edu.keddad.stasi.Messaging.OrderEntry;
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

public class InstructionStorage extends Agent {
    private DishInstuctions instructions;

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "instruction");

        try {
            instructions = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), DishInstuctions.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                ACLMessage msg = receive();
                if (msg != null) {
                    InstructionRequest rd;
                    String contents = msg.getContent();
                    if (contents.startsWith("instruct")) {
                        try {
                            rd = new ObjectMapper().readValue(contents.substring(contents.indexOf(' ')), InstructionRequest.class);

                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        ACLMessage reply = msg.createReply();
                        try {
                            reply.setContent(getInstruct(rd));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        send(reply);
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

    private String getInstruct(InstructionRequest request) throws JsonProcessingException {
        DishInstuctions.DishInstruction instr = null;
        for (DishInstuctions.DishInstruction item : instructions.dish_cards) {
            if (item.id == request.id) {
                instr = item;
                break;
            }
        }
        InstructionAnswer result = new InstructionAnswer();
        assert instr != null;
        result.equipmentId = instr.equipment_required;
        result.time = instr.card_time;
        List<InstructionAnswer.OrderType> saveTypes = new ArrayList<InstructionAnswer.OrderType>();
        for (DishInstuctions.DishInstruction.Operation item : instr.operations) {
            saveTypes.add(new InstructionAnswer.OrderType(item.type, item.async_point));
        }
        Map<Integer, DishInstuctions.DishInstruction.Operation.Product> saveProducts = new HashMap<Integer, DishInstuctions.DishInstruction.Operation.Product>();

        for (DishInstuctions.DishInstruction.Operation item : instr.operations) {
            for (DishInstuctions.DishInstruction.Operation.Product prod : item.products) {
                if (saveProducts.containsKey(prod.id)) {

                    // проверить данное выражение
                    saveProducts.get(prod.id).quantity +=
                            prod.quantity;
                } else {
                    saveProducts.put(prod.id, prod);
                }
            }
        }

        result.types = saveTypes.toArray(InstructionAnswer.OrderType[]::new);
        result.products = saveProducts.values().toArray(InstructionAnswer.OrderProduct[]::new);

        return (new ObjectMapper().writeValueAsString(result));
    }

    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
