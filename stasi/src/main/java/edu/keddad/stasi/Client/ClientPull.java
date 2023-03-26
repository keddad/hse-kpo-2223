package edu.keddad.stasi.Client;

import edu.keddad.stasi.Messaging.OrderEntry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ClientPull {

    public long wait_time;

    public OrderEntry[] ord_dishes;
}
