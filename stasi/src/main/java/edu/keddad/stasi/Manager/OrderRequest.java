package edu.keddad.stasi.Manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// Used for simpler code and serialization support
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    public static final class OrderEntity {
        public int OrderDishId; // Unique in order?
        public int MenuDishId;
    }

    public static String mnemonic = "OrderRequest";

    public OrderEntity[] items;
}
