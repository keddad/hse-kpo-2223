package edu.keddad.stasi.Manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.concurrent.ThreadLocalRandom;

public class Manager extends Agent{
    @Override
    protected void setup() {
        registerManager();
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String contents = msg.getContent();

                    if (contents.startsWith(OrderRequest.mnemonic)) {
                        try {
                            OrderRequest rd = new ObjectMapper().readValue(
                                    contents.substring(contents.indexOf(' ')),
                                    OrderRequest.class
                            );

                            handleOrderRequest(rd, msg);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
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

    private void registerManager() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("manager");
        sd.setName("manager");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void handleOrderRequest(OrderRequest rd, ACLMessage msg) {
        System.out.printf("Got order request with %d items!\n", rd.items.length);

        ACLMessage reply = msg.createReply();

        if (ThreadLocalRandom.current().nextInt(0, 2) == 1) {
            reply.setPerformative(ACLMessage.CONFIRM);
        } else {
            reply.setPerformative(ACLMessage.FAILURE);
        }

        send(reply);
        System.out.println("Sending order response message!");
    }

}
