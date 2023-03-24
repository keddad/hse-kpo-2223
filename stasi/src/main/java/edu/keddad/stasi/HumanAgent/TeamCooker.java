package edu.keddad.stasi.HumanAgent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class TeamCooker {


    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cookers {

        public int id;

        public String name;

        @JsonProperty("is_active")
        public boolean active;
        long ReserveTime = 0;
    }

    public Cookers[] cookers;

}
