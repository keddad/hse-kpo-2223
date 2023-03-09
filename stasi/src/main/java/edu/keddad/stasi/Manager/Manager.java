package edu.keddad.stasi.Manager;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class Manager extends Agent{
    @Override
    protected void setup() {
        System.out.println("Starting Manager agent");
        System.out.println("Hello world! I'm an agent!");
        System.out.println("My local name is " + getAID().getLocalName());
        System.out.println("My GUID is " + getAID().getName());
        System.out.println("My addresses are " + String.join(",", getAID().getAddressesArray()));

        addBehaviour(new TickerBehaviour(this, 1000) {
            private int counter = 0;
            @Override
            protected void onTick() {
                System.out.println("Manager Agent Ticking Behaviour");
                counter += 1;

                if (counter == 10) {
                    doDelete();
                }
            }
        });
    }
    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }

}
