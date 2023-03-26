package edu.keddad.stasi.Manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dish {
        public int id;
        public int price;
    }

    public Dish[] items;
}
