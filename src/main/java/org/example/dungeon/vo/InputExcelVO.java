package org.example.dungeon.vo;


import lombok.Data;

@Data
public class InputExcelVO {
    /**
     * 物品名称 索引
     */
    private String itemName;
    /**
     * 工人名称
     */
    private String worker;
    /**
     * 买入单价
     * -1 无法买入卖出
     */
    private int buyPrice;
    /**
     * 卖出单价
     * 默认不填，为买入价的1/3
     * 部分商品特殊需要单独填写
     */
    private int sellPrice;
    /**
     * 一个工人的产出数量
     */
    private int produceCount;
    /**
     * 一个工人的消耗公式，例如
     * 水晶-3石头-1
     */
    private String consumeExpression = "";
}
