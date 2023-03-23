package edu.keddad.stasi.HumanAgent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.Paths;

public class HumanAgent extends Agent {
    private TeamCookers team;

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "eqipment");

        try {
            team = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), TeamCookers.class);
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
                            CookerRequest rd = new ObjectMapper().readValue(
                                    contents,
                                    CookerRequest.class

                            );
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

    private boolean checkReserve(CookerRequest rd) {

        for (TeamCookers.Cooker struct : TeamCookers.humans) {
            if (struct.ReserveTime < System.currentTimeMillis() && struct.active) {
                struct.ReserveTime = System.currentTimeMillis() + rd.cookers[0].CookTime;
                return true;
            } else {
                return false;
            }

        }

        return false;
    }
}
