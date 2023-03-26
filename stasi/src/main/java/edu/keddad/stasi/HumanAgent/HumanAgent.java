package edu.keddad.stasi.HumanAgent;

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

public class HumanAgent extends Agent {
    private TeamCooker team;
    private Map<String, List<TeamCooker.Cookers>> delCookers = new HashMap<>();

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "cookers");

        try {
            team = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), TeamCooker.class);
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
                                CookerRequest rd = new ObjectMapper().readValue(
                                        contents.substring(contents.indexOf(' ')),
                                        CookerRequest.class

                                );
                                ACLMessage reply = msg.createReply();
                                String str = Long.toString(checkReserve(rd, msg.getReplyWith()));
                                reply.setContent(str);
                                send(reply);

                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (contents.startsWith("delete")) {
                            ACLMessage reply = msg.createReply();
                            if (deleteCook(msg.getReplyWith())) {
                                reply.setContent("true");
                            } else {
                                reply.setContent("false");
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

    private long checkReserve(CookerRequest rd, String deleteId) {
        List<TeamCooker.Cookers> invCookers = new ArrayList<>();
        long workTime = 0;
        for (CookerRequest.CookEntry request : rd.cookers) {
            long minTime = Long.MAX_VALUE;
            TeamCooker.Cookers betterCooker = null;
            for (TeamCooker.Cookers cooker : team.cookers) {
                if (cooker.ReserveTime < minTime && cooker.active) {
                    minTime = cooker.ReserveTime;
                    betterCooker = cooker;
                }
            }
            assert betterCooker != null;
            betterCooker.ReserveTime = System.currentTimeMillis() + request.CookTime;
            invCookers.add(betterCooker);
            if (workTime < betterCooker.ReserveTime) {
                workTime = betterCooker.ReserveTime;
            }
        }
        delCookers.put(deleteId, invCookers);
        return workTime;
    }

    private boolean deleteCook(String deleteId) {
        List<TeamCooker.Cookers> invCookers = delCookers.get(deleteId);
        for (TeamCooker.Cookers human : invCookers) {
            human.ReserveTime = 0;
        }
        return true;
    }

}
