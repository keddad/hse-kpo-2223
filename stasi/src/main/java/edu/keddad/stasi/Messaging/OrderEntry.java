package edu.keddad.stasi.Messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class OrderEntry {
    @JsonProperty("ord_id")
    public int OrderDishId; // Unique in order?
    @JsonProperty("menu_dish_id")
    public int MenuDishId;
}