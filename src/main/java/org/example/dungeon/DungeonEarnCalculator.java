package org.example.dungeon;

import com.google.common.collect.Lists;
import lombok.Data;
import org.example.dungeon.enums.ItemConsumeWayEnum;
import org.example.dungeon.vo.ConsumeExpressionTermVO;
import org.example.dungeon.vo.ItemConsumeSolution;
import org.example.dungeon.vo.ItemVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DungeonEarnCalculator {

    Map<String, ItemVO> itemVOMap;

    public void calc(List<ItemVO> itemVOList) {
        itemVOMap = itemVOList.stream()
                .collect(Collectors.toMap(ItemVO::getItemName, itemVO -> itemVO));

        for (ItemVO itemVO : itemVOList) {
            handleOneItem(itemVO);
        }
    }


    private void handleOneItem(ItemVO itemVO) {
        if (itemVO.isDone()) {
            return;
        }

        if (itemVO.isCanTrade()) {
            //以商品买入价计
            //直接买入作为首个策略
            List<ItemConsumeWayVO> solutionStepListBuy = Lists.newArrayList(
                    new ItemConsumeWayVO(itemVO.getItemName(), ItemConsumeWayEnum.BUY));
            ItemConsumeSolution buyPriceSolution = new ItemConsumeSolution(solutionStepListBuy, itemVO.getBuyPrice());
            itemVO.setBuyPriceSolution(buyPriceSolution);
            calcBestSolution(itemVO, buyPriceSolution);


            //以商品卖出价计
        } else {

        }

    }

    private void calcBestSolution(ItemVO itemVO, ItemConsumeSolution solution) {
        List<ConsumeExpressionTermVO> consumeExpression = itemVO.getConsumeExpression();
        List<ItemConsumeWayVO> buyPriceExpression = new ArrayList<>();
    }
}
