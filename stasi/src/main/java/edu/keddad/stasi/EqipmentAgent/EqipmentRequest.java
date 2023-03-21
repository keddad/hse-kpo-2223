package edu.keddad.stasi.EqipmentAgent;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EqipmentRequest {
    static String mnemonic = "reserve";
    @NoArgsConstructor
    @AllArgsConstructor
    public class EqipmentEntry {
        public int OrderDishId; // Unique in order?
    }

    public EqipmentEntry[] items;

}
