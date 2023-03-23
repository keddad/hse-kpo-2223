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
        YellowBooks.registerRecipient(this, "humans");

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
                            String str = Long.toString(checkReserve(rd));
                            reply.setContent(str);
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

    private long checkReserve(CookerRequest rd) {
        long workTime = 0;
        for (CookerRequest.CookEntry request : rd.cookers) {
            long minTime = Long.MAX_VALUE;
            TeamCookers.Cooker betterCooker = null;
            for (TeamCookers.Cooker cooker : TeamCookers.humans) {
                if (cooker.ReserveTime < minTime && cooker.active) {
                    minTime = cooker.ReserveTime;
                    betterCooker = cooker;
                }
            }
            assert betterCooker != null;
            betterCooker.ReserveTime = System.currentTimeMillis() + request.CookTime;
            if (workTime < betterCooker.ReserveTime) {
                workTime = betterCooker.ReserveTime;
            }
        }
        return workTime;
    }
}
