package edu.keddad.stasi.EqipmentAgent;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EqipmentRequest {

    @NoArgsConstructor
    @AllArgsConstructor

    public static class   EqipmentEntry {
        public int type; // Unique in order?
        public int CookTime;
    }

    public EqipmentEntry[] equipment;

}
