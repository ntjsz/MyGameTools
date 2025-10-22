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
            ItemConsumeSolution buyPriceSolution = new ItemConsumeSolution();
            itemVO.setBuyPriceSolution(buyPriceSolution);
            calcBestSolution(itemVO, buyPriceSolution, itemVO.getBuyPrice());


            //以商品卖出价计
        } else {

        }

    }

    private void calcBestSolution(ItemVO itemVO, ItemConsumeSolution bestSolution, int itemPrice) {
        List<ConsumeExpressionTermVO> consumeExpression = itemVO.getConsumeExpression();
        List<ItemConsumeWayVO> currentStepList = new ArrayList<>();

        for (int i = 0; i < consumeExpression.size(); i++) {

        }
    }

    private void calcBestSolutionHelper(ItemConsumeSolution bestSolution,
                                        List<ConsumeExpressionTermVO> consumeExpression,
                                        List<ItemConsumeWayVO> currentStepList,
                                        int itemPrice, int i) {
        if (i >= consumeExpression.size()) {
            //no more step, calc solution
            return;
        } else {
            String itemName = consumeExpression.get(i).getItemName();
            ItemVO itemVO = itemVOMap.get(itemName);
            handleOneItem(itemVO);
            if (itemVO.isCanTrade()) {
                // 购买
                currentStepList.add(new ItemConsumeWayVO(itemName, ItemConsumeWayEnum.BUY));
                calcBestSolutionHelper(bestSolution, consumeExpression, currentStepList, itemPrice, i + 1);
                currentStepList.remove(currentStepList.size() - 1);
                // 制作
                currentStepList.add(new ItemConsumeWayVO(itemName, ItemConsumeWayEnum.PRODUCE));
                calcBestSolutionHelper(bestSolution, consumeExpression, currentStepList, itemPrice, i + 1);
                currentStepList.remove(currentStepList.size() - 1);
            } else {
                // 不能交易，只能直接购买
                currentStepList.add(new ItemConsumeWayVO(itemName, ItemConsumeWayEnum.BUY));
                calcBestSolutionHelper(bestSolution, consumeExpression, currentStepList, itemPrice, i + 1);
                currentStepList.remove(currentStepList.size() - 1);
            }
        }
    }

    private void calcBestSolutionFinalStep(ItemConsumeSolution bestSolution,
                                           List<ConsumeExpressionTermVO> consumeExpression,
                                           List<ItemConsumeWayVO> currentStepList,
                                           int itemPrice) {
        float priceTotal = itemPrice;
        int workerCount = 1;
        for (int i = 0; i < consumeExpression.size(); i++) {
            ConsumeExpressionTermVO termVO = consumeExpression.get(i);
            ItemVO itemVO = itemVOMap.get(termVO.getItemName());
            ItemConsumeWayEnum wayEnum = currentStepList.get(i).getWayEnum();
            if (wayEnum == ItemConsumeWayEnum.BUY) {
                priceTotal = priceTotal - itemVO.getBuyPrice() * termVO.getCount();
            } else {
                priceTotal = priceTotal -
                        itemVO.getBuyPriceSolution().getPricePerWorker()
                                * itemVO.getBuyPriceSolution().getWorkerCount()
                                * termVO.getCount();
                workerCount = workerCount + itemVO.getBuyPriceSolution().getWorkerCount() * termVO.getCount();
            }
        }
        float currentPricePerWorker = 
    }
}
