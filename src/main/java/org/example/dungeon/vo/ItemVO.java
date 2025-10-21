package org.example.dungeon.vo;


import lombok.Data;
import org.example.dungeon.ItemConsumeWayVO;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemVO {
    /**
     * 物品名称 索引
     */
    private String itemName;
    /**
     * 工人名称
     */
    private String worker;
    /**
     * 可以购买和卖出
     */
    private boolean canTrade;
    /**
     * 买入单价
     */
    private int buyPrice;
    /**
     * 卖出单价
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
    private List<ConsumeExpressionTermVO> consumeExpression;


    /**
     * 以下为计算填入字段
     */
    /**
     * 该物品计算完成
     */
    private boolean done;
    /**
     * 以商品买入价计，每工人最高产值的方案
     */
    private ItemConsumeSolution buyPriceSolution;
    /**
     * 以商品卖出价计，每工人最高产值
     */
    private ItemConsumeSolution sellPriceSolution;
}
