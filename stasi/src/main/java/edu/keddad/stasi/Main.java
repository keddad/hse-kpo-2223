package edu.keddad.stasi;

import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller(args[0], args[1]);

        try {
            controller.initAgents();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }
}