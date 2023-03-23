package edu.keddad.stasi.HumanAgent;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.keddad.stasi.EqipmentAgent.MenuEqipment;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class TeamCookers {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cooker {

        public int id;

        public String name;

        @JsonProperty("is_active")
        public boolean active;
        long ReserveTime = 0;
    }

    public static Cooker[] humans;

}
