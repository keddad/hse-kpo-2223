package edu.keddad.stasi.HumanAgent;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CookerRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class CookEntry {
        public long CookTime;
    }

    public CookEntry[] cookers;
}
