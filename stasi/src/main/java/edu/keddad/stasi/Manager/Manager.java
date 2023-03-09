package edu.keddad.stasi.Manager;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class Manager extends Agent{
    @Override
    protected void setup() {
        System.out.println("Starting Manager agent");
        System.out.println("Hello world! I'm an agent!");
        System.out.println("My local name is " + getAID().getLocalName());
        System.out.println("My GUID is " + getAID().getName());
        System.out.println("My addresses are " + String.join(",", getAID().getAddressesArray()));

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

        addBehaviour(new TickerBehaviour(this, 1000) {
            private int counter = 0;
            @Override
            protected void onTick() {
                System.out.println("Manager Agent Ticking Behaviour " + counter);
                counter += 1;
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String content = msg.getContent();

                    ACLMessage reply = msg.createReply();
                    reply.setContent(content);
                    send(reply);
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

}
