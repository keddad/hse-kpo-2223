package edu.keddad.stasi.InstructionStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.EqipmentAgent.EqipmentRequest;
import edu.keddad.stasi.EqipmentAgent.MenuEqipment;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Paths;

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

//                        if (getInstruct(rd)) {
//                            reply.setContent("true");
//                        } else {
//                            reply.setContent("");
//                        }
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

    private void getInstruct(InstructionRequest request) {

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
