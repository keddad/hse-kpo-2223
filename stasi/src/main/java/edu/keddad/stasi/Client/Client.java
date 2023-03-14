package edu.keddad.stasi.Client;

import jade.core.Agent;

public class Client extends Agent {
    @Override
    protected void setup() {
        // This method should recive path to .json in args
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }

}
