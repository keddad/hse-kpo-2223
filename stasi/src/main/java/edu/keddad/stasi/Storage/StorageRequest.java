package edu.keddad.stasi.Storage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class StorageRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        public int id;
        public double quantity;
    }

    public Product[] products;
}
