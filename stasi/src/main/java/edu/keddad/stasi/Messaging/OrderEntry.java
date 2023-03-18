package edu.keddad.stasi.Messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class OrderEntry {
    public int OrderDishId; // Unique in order?
    public int MenuDishId;
}