package org.example.dungeon;


import lombok.Data;
import org.example.dungeon.enums.ItemConsumeWayEnum;

@Data
public class ItemConsumeWayVO {
    /**
     * 物品名称
     */
    private String itemName;
    /**
     * 工人产出or直接购买
     */
    private ItemConsumeWayEnum wayEnum;
    /**
     * 购买单价
     */
    private int buyPrice;
    /**
     * 购买数量
     */
    private int quantity;

    /**
     * 工人名称
     */
    private String worker;
    /**
     * 工人数量
     */
    private int workerCount;
}
