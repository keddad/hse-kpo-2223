package edu.keddad.stasi.InstructionStorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class DishInstuctions {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class DishInstruction {
        public int id;

        public String dish_name;
        public String card_descr;
        public long card_time;

        public int equipment_required;

        @NoArgsConstructor
        @AllArgsConstructor
        public static class Operation {
            @JsonProperty("oper_time")
            public long operationTime;
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
