package edu.keddad.stasi.Dish;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LogObject {
    public String parent;
    public int menuId;
    public boolean reservedOk;
    public boolean canceled;
}
