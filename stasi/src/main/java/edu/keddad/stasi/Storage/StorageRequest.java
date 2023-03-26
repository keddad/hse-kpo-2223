package edu.keddad.stasi.Storage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class StorageRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    public class Product {
        int id;
        double quantity;
    }

    Product[] products;
}
