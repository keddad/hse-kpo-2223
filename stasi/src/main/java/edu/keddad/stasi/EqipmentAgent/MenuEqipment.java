package edu.keddad.stasi.EqipmentAgent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class MenuEqipment {
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuEquipments {

        public int type;

        public int id;

        public String name;

        @JsonProperty("is_active")
        public boolean active;
        public boolean reserved = true;
        public long ReserveTime = 0;
    }

    public MenuEquipments[] equipment;
}