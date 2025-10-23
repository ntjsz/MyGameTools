package org.example.dungeon;

import org.apache.commons.collections4.CollectionUtils;
import org.example.dungeon.enums.ItemConsumeSolutionEnum;
import org.example.dungeon.enums.ItemConsumeWayEnum;
import org.example.dungeon.vo.ConsumeExpressionTermVO;
import org.example.dungeon.vo.ItemConsumeSolution;
import org.example.dungeon.vo.ItemConsumeSolutionTreeNode;
import org.example.dungeon.vo.ItemVO;

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
        if (itemVO.isCanTrade()) {
            itemVO.setSellSolution(calcBestSolution(itemVO, ItemConsumeSolutionEnum.SELL));
            itemVO.setProduceSolution(calcBestSolution(itemVO, ItemConsumeSolutionEnum.PRODUCE));
        } else {
            // 无法直接购买的商品，只计算一个生产的最佳方案
            itemVO.setProduceSolution(calcBestSolution(itemVO, ItemConsumeSolutionEnum.PRODUCE));
        }
    }

    private ItemConsumeSolution calcBestSolution(ItemVO itemVO, ItemConsumeSolutionEnum solutionEnum) {
        ItemConsumeSolution bestSolution = new ItemConsumeSolution();
        bestSolution.setSolutionEnum(solutionEnum);
        bestSolution.setPricePerWorker(0);

        ItemConsumeSolutionTreeNode root = new ItemConsumeSolutionTreeNode();
        root.setItemName(itemVO.getItemName());
        root.setWayEnum(ItemConsumeWayEnum.PRODUCE);

        calcBestSolutionHelper(bestSolution, root, true);
        return bestSolution;
    }

    /**
     * 递归构建solution tree
     */
    private void calcBestSolutionHelper(ItemConsumeSolution bestSolution,
                                        ItemConsumeSolutionTreeNode parentNode,
                                        boolean finalNode) {
        List<ConsumeExpressionTermVO> consumeExpression = itemVOMap.get(parentNode.getItemName()).getConsumeExpression();
        if (CollectionUtils.isEmpty(consumeExpression)) {
            if (finalNode) {
                compareAndSetBestSolutionAtFinalNode(bestSolution, parentNode);
            }
            return;
        }

        List<ItemConsumeSolutionTreeNode> childrenNodeList = parentNode.getChildren();
        for (ConsumeExpressionTermVO termVO : consumeExpression) {
            ItemConsumeSolutionTreeNode treeNode = new ItemConsumeSolutionTreeNode();
            treeNode.setParent(parentNode);
            childrenNodeList.add(treeNode);

            String itemName = termVO.getItemName();
            treeNode.setItemName(itemName);
        }

        calcBestSolutionHelper2(bestSolution, parentNode, finalNode, consumeExpression, 0);
    }


    /**
     * 递归构建consumeExpression的所有ItemConsumeWayEnum.PRODUCE or BUY 组合
     */
    private void calcBestSolutionHelper2(ItemConsumeSolution bestSolution,
                                         ItemConsumeSolutionTreeNode parentNode,
                                         boolean finalNode,
                                         List<ConsumeExpressionTermVO> consumeExpression,
                                         int index) {
        if (index >= consumeExpression.size()) {
            for (int i = 0; i < consumeExpression.size(); i++) {
                finalNode = finalNode && (i == consumeExpression.size() - 1);
                calcBestSolutionHelper(bestSolution, parentNode, finalNode);
            }
            return;
        }

        ItemVO itemVO = itemVOMap.get(consumeExpression.get(index).getItemName());
        ItemConsumeSolutionTreeNode treeNode = parentNode.getChildren().get(index);
        treeNode.setWayEnum(ItemConsumeWayEnum.PRODUCE);
        calcBestSolutionHelper2(bestSolution, treeNode, finalNode, consumeExpression, index + 1);
        if (itemVO.isCanTrade()) {
            treeNode.setWayEnum(ItemConsumeWayEnum.BUY);
            calcBestSolutionHelper2(bestSolution, treeNode, finalNode, consumeExpression, index + 1);
        }
    }


    /**
     * 更新最佳解决方案
     */
    private void compareAndSetBestSolutionAtFinalNode(ItemConsumeSolution bestSolution,
                                                      ItemConsumeSolutionTreeNode parentNode) {
        System.out.println("good");
    }
}
