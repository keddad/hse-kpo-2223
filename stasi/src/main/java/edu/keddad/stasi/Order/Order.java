package edu.keddad.stasi.Order;

import jade.core.Agent;

public class Order extends Agent{
    @Override
    protected void setup() {
        // This method should recive dish info and order info in args
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }
}
