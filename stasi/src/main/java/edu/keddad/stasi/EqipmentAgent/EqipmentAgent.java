package edu.keddad.stasi.EqipmentAgent;

import jade.core.Agent;

public class EqipmentAgent extends Agent{
    @Override
    protected void setup() {
        // This method should recive dish info and order info in args
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }
}
