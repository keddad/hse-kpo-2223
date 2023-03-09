package edu.keddad.stasi;

import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();

        try {
            controller.initAgents();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
            // TODO learn what StaleProxy is
        }
    }
}