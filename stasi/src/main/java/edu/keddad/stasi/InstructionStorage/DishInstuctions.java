package edu.keddad.stasi.InstructionStorage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class DishInstuctions {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class DishInstruction {
        public int id;

        public String dish_name;

        public long card_time;

        public int equipment_required;

        @NoArgsConstructor
        @AllArgsConstructor
        public static class Operation {
            public int type;
            public int async_point;

            @NoArgsConstructor
            @AllArgsConstructor
            public static class Product {
                public int id;
                public double quantity;
            }

            public Product[] products;
        }

        public Operation[] operations;

    }

    public DishInstruction[] dish_cards;
}
