package org.example.dungeon.vo;


import lombok.Data;
import lombok.ToString;
import org.example.dungeon.enums.ItemConsumeWayEnum;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"parent", "children"})
public class ItemConsumeSolutionTreeNode {
    private ItemConsumeSolutionTreeNode parent;
    private List<ItemConsumeSolutionTreeNode> children = new ArrayList<>();

    /**
     * 物品名称
     */
    private String itemName;
    /**
     * 工人产出or直接购买
     */
    private ItemConsumeWayEnum wayEnum;
}
