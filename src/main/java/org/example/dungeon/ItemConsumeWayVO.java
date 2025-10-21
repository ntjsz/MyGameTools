package org.example.dungeon;


import lombok.Data;
import org.example.dungeon.enums.ItemConsumeWayEnum;

@Data
public class ItemConsumeWayVO {
    private String itemName;
    private ItemConsumeWayEnum wayEnum;
    private int buyPrice;
    private int quantity;

    private String worker;
    private int workerCount;
}
