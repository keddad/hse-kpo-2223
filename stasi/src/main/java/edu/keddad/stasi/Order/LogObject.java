package edu.keddad.stasi.Order;

import edu.keddad.stasi.Manager.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LogObject {
    public OrderRequest order;

    public boolean reserved = false;
    public boolean canceled = false;
}
