package edu.keddad.stasi.ResourceReserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.keddad.stasi.EqipmentAgent.EqipmentRequest;
import edu.keddad.stasi.HumanAgent.CookerRequest;
import edu.keddad.stasi.InstructionStorage.InstructionAnswer;
import edu.keddad.stasi.InstructionStorage.InstructionRequest;
import edu.keddad.stasi.Manager.OrderRequest;
import edu.keddad.stasi.Messaging.YellowBooks;
import edu.keddad.stasi.Storage.Storage;
import edu.keddad.stasi.Storage.StorageRequest;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// https://youtube.com/watch?v=4RHg0f5Nq4c
public class ResourceReserver extends Agent {
    DFAgentDescription instructionAgent;
    DFAgentDescription storageAgent;
    DFAgentDescription cookAgent;
    DFAgentDescription equipmentAgent;

    @Override
    protected void setup() {
        YellowBooks.registerRecipient(this, "resourcereserver");

        instructionAgent = YellowBooks.findRecipient(this, "instruction");
        storageAgent = YellowBooks.findRecipient(this, "storage");
        cookAgent = YellowBooks.findRecipient(this, "cookers");
        equipmentAgent = YellowBooks.findRecipient(this, "eqipment");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchInReplyTo("");
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    if (msg.getContent().startsWith(DishReserveRequest.mnemonic)) {
                        try {
                            DishReserveRequest rd = new ObjectMapper().readValue(msg.getContent().substring(msg.getContent().indexOf(' ')), DishReserveRequest.class);
                            reserveHandler(rd, msg);
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

    private void reserveHandler(DishReserveRequest ds, ACLMessage clientMsg) {
        addBehaviour(new Behaviour() {

            int state = 0; // state? sorry, i'm an anarchist
            InstructionAnswer instructions = null;
            String conversation = UUID.randomUUID().toString();

            long maxtime = 0;

            void abort() {
                state = 9;

                ACLMessage response = clientMsg.createReply(ACLMessage.FAILURE);
                send(response);

                return;
            }

            @Override
            public void action() {
                switch (state) {
                    case 0: // Request instructions
                        ACLMessage instructionsRequest = new ACLMessage(ACLMessage.REQUEST);
                        instructionsRequest.setReplyWith(conversation);
                        instructionsRequest.addReceiver(instructionAgent.getName());
                        try {
                            instructionsRequest.setContent("instruct " + new ObjectMapper().writeValueAsString(new InstructionRequest(ds.dishId)));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        send(instructionsRequest);
                        state++;
                        break;
                    case 1: // Get instructions
                        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchSender(instructionAgent.getName()), MessageTemplate.MatchInReplyTo(conversation));

                        ACLMessage instructionsMessage = receive(mt);

                        if (instructionsMessage != null) {
                            try {
                                instructions = new ObjectMapper().readValue(instructionsMessage.getContent(), InstructionAnswer.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            state++;
                            break;
                        } else {
                            block();
                            break;
                        }
                    case 2:
                        ACLMessage produceRequest = new ACLMessage(ACLMessage.REQUEST);
                        produceRequest.setReplyWith(conversation);
                        produceRequest.addReceiver(storageAgent.getName());
                        try { // We really should have had proper classes
                            produceRequest.setContent("reserve " + new ObjectMapper().writeValueAsString(new StorageRequest(Arrays.stream(instructions.products).map(it -> new StorageRequest.Product(it.id, it.quantity)).toArray(StorageRequest.Product[]::new))));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        send(produceRequest);
                        state++;
                        break;
                    case 3:
                        mt = MessageTemplate.and(MessageTemplate.MatchSender(storageAgent.getName()), MessageTemplate.MatchInReplyTo(conversation));

                        ACLMessage produceMessage = receive(mt);

                        if (produceMessage != null) {
                            if (produceMessage.getPerformative() == ACLMessage.FAILURE) {
                                abort();
                                break;
                            }

                            state++;
                            break;
                        } else {
                            block();
                            break;
                        }
                    case 4:
                        ACLMessage humanRequest = new ACLMessage(ACLMessage.REQUEST);
                        humanRequest.setReplyWith(conversation);
                        humanRequest.addReceiver(cookAgent.getName());
                        try { // We really should have had proper classes
                            humanRequest.setContent("reserve " + new ObjectMapper().writeValueAsString(new CookerRequest(Arrays.stream(instructions.types).map(it -> new CookerRequest.CookEntry(it.operationTime)).toArray(CookerRequest.CookEntry[]::new))));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        send(humanRequest);
                        state++;
                        break;
                    case 5:
                        mt = MessageTemplate.and(MessageTemplate.MatchSender(cookAgent.getName()), MessageTemplate.MatchInReplyTo(conversation));

                        ACLMessage cookMessage = receive(mt);

                        if (cookMessage != null) {
                            if (cookMessage.getPerformative() == ACLMessage.FAILURE) {
                                abort();
                                break;
                            }

                            maxtime = Long.parseLong(cookMessage.getContent());

                            state++;
                            break;
                        } else {
                            block();
                            break;
                        }
                    case 6:
                        ACLMessage equipmentRequest = new ACLMessage(ACLMessage.REQUEST);
                        equipmentRequest.setReplyWith(conversation);
                        equipmentRequest.addReceiver(equipmentAgent.getName());
                        try { // We really should have had proper classes
                            equipmentRequest.setContent("reserve " + new ObjectMapper().writeValueAsString(new EqipmentRequest(new EqipmentRequest.EqipmentEntry[]{new EqipmentRequest.EqipmentEntry(instructions.equipmentId, maxtime)})));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        send(equipmentRequest);
                        state++;
                        break;
                    case 7:
                        mt = MessageTemplate.and(MessageTemplate.MatchSender(equipmentAgent.getName()), MessageTemplate.MatchInReplyTo(conversation));

                        ACLMessage equipmentMessage = receive(mt);

                        if (equipmentMessage != null) {
                            if (equipmentMessage.getPerformative() == ACLMessage.FAILURE) {
                                abort();
                                break;
                            }

                            maxtime = Long.parseLong(equipmentMessage.getContent());

                            state++;
                            break;
                        } else {
                            block();
                            break;
                        }
                    case 8:
                        ACLMessage response = clientMsg.createReply(ACLMessage.CONFIRM);
                        try {
                            response.setContent(new ObjectMapper().writeValueAsString(new DishReserveResponse(maxtime)));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        send(response);
                        state += 1;
                }


            }

            @Override
            public boolean done() {
                return state == 9;
            }
        });
    }
}
