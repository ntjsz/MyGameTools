package org.example.dungeon;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dungeon.enums.ItemConsumeWayEnum;

@Data
@AllArgsConstructor
public class ItemConsumeWayVO {
    /**
     * 物品名称
     */
    private String itemName;
    /**
     * 工人产出or直接购买
     */
    private ItemConsumeWayEnum wayEnum;
}
