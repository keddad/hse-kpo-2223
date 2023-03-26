package edu.keddad.stasi.Dish;

import edu.keddad.stasi.Messaging.AgentRuntime;
import edu.keddad.stasi.Messaging.OrderEntry;
import jade.core.AID;

import java.util.ArrayList;
import java.util.Arrays;

public class DishUtils {
    public static ArrayList<String> enqueueDishes(OrderEntry[] items, String logPath, AID reportTo, String conversationId) {
        ArrayList<String> names = new ArrayList<>();

        Arrays.stream(items).forEach(it -> {
            names.add(AgentRuntime.createAgent(
                    "edu.keddad.stasi.Dish.Dish",
                    "dish_" + conversationId + "_" + it.OrderDishId + "_" + it.MenuDishId,
                    new Object[]{logPath, reportTo, conversationId, it.MenuDishId}));
        });

        return names;
    }
}
