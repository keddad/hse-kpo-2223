package edu.keddad.stasi.Storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.InstructionStorage.DishInstuctions;
import edu.keddad.stasi.Messaging.YellowBooks;
import jade.core.Agent;

import java.io.IOException;
import java.nio.file.Paths;

public class Storage extends Agent {
    private MenuStorage stock;

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "storage");

        try {
            stock = new ObjectMapper().readValue(Paths.get((String) getArguments()[1]).toFile(), MenuStorage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating");
    }
}
