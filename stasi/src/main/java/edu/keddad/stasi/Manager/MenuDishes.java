package edu.keddad.stasi.Manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MenuDishes {
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuDish {

        public int id;

        public int price;

        @JsonProperty("is_active")
        public boolean active;
    }

    public MenuDish[] dishes;
}
