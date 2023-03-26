package edu.keddad.stasi.EqipmentAgent;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EqipmentRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class   EqipmentEntry {
        public long type; // Unique in order?
        public long CookTime;
    }

    public EqipmentEntry[] equipment;

}
