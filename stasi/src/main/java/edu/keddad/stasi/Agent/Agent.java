package edu.keddad.stasi.Agent;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class Agent extends jade.core.Agent {
    @Override
    protected void setup() {
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("manager");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);

                    if (result.length == 0) {
                        System.out.println("Can't send message!");
                        return;
                    }

                    ACLMessage msg = new ACLMessage();
                    msg.addReceiver(result[0].getName());
                    msg.setContent("Contentful message");
                    send(msg);
                    System.out.println("Sent message!");
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String content = msg.getContent();

                    System.out.println("Got message echoed " + content);
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
