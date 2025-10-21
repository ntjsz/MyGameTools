package org.example.dungeon.vo;


import lombok.Data;

@Data
public class ConsumeExpressionTermVO {
    /**
     * 物品名称
     */
    private String itemName;
    /**
     * 物品数量
     */
    private int count;
}
