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

        @JsonProperty("menu_dish_id")
        public int id;

        @JsonProperty("menu_dish_card")
        public int card;

        @JsonProperty("menu_dish_price")
        public int price;

        @JsonProperty("menu_dish_active")
        public boolean active;
    }

    @JsonProperty("menu_dishes")
    public MenuDish[] dishes;
}
