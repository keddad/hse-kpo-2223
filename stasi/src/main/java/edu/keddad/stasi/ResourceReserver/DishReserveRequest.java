package edu.keddad.stasi.ResourceReserver;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DishReserveRequest {
    public int dishId;

    public static String mnemonic = "ReserveDish";
}
