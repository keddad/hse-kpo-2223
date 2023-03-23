package edu.keddad.stasi.HumanAgent;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CookerRequest {

    public class CookEntry {
        public long CookTime;
    }

    public CookEntry[] cookers;
}
