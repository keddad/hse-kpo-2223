package edu.keddad.stasi.InstructionStorage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class InstructionAnswer {
    public double time;
    public int equipmentId;

    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderType {
        public long operationTime;
        public int type;
        public int async_point;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProduct {
        public int id;
        public double quantity;
    }

    OrderType[] types;
    OrderProduct[] products;

}
