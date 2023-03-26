package edu.keddad.stasi.Storage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MenuStorage {
    @NoArgsConstructor
    @AllArgsConstructor

    public static class Product {
        public int id;
        public int type;
        public String name;
        public double quantity;
    }

    public Product[] products;
}
