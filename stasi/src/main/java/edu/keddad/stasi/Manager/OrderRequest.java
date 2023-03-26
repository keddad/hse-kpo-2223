package edu.keddad.stasi.Manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import edu.keddad.stasi.Messaging.OrderEntry;

// Used for simpler code and serialization support
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    public static String mnemonic = "OrderRequest";
    public boolean estimate;

    public OrderEntry[] items;
}
