package org.example.dungeon;

import org.apache.commons.collections4.CollectionUtils;
import org.example.dungeon.enums.ItemConsumeSolutionEnum;
import org.example.dungeon.enums.ItemConsumeWayEnum;
import org.example.dungeon.vo.ConsumeExpressionTermVO;
import org.example.dungeon.vo.ItemConsumeSolution;
import org.example.dungeon.vo.ItemConsumeSolutionTreeNode;
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
        bestSolution.setPricePerWorker(Integer.MIN_VALUE);

        ItemConsumeSolutionTreeNode root = new ItemConsumeSolutionTreeNode();
        root.setItemName(itemVO.getItemName());
        root.setWayEnum(ItemConsumeWayEnum.PRODUCE);

        calcBestSolutionHelper(bestSolution, root, true);
        calcSolutionBrief(bestSolution);
        return bestSolution;
    }

    /**
     * 递归构建solution tree
     */
    private void calcBestSolutionHelper(ItemConsumeSolution bestSolution,
                                        ItemConsumeSolutionTreeNode parentNode,
                                        boolean finalNode) {
        if (parentNode.getWayEnum() == ItemConsumeWayEnum.BUY) {
            parentNode.setChildren(new ArrayList<>());
        }

        List<ConsumeExpressionTermVO> consumeExpression = itemVOMap.get(parentNode.getItemName()).getConsumeExpression();
        if (CollectionUtils.isEmpty(consumeExpression)
                || parentNode.getWayEnum() == ItemConsumeWayEnum.BUY) { // 直接购买就不需要构建children了
            if (finalNode) {
                compareAndSetBestSolutionAtFinalNode(bestSolution, parentNode);
            }
            return;
        }

        List<ItemConsumeSolutionTreeNode> childrenNodeList = new ArrayList<>();
        for (ConsumeExpressionTermVO termVO : consumeExpression) {
            ItemConsumeSolutionTreeNode treeNode = new ItemConsumeSolutionTreeNode();
            treeNode.setParent(parentNode);
            childrenNodeList.add(treeNode);

            String itemName = termVO.getItemName();
            treeNode.setItemName(itemName);
        }
        parentNode.setChildren(childrenNodeList);

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
            List<ItemConsumeSolutionTreeNode> children = parentNode.getChildren();
            for (int i = 0; i < children.size(); i++) {
                finalNode = finalNode && (i == children.size() - 1);
                calcBestSolutionHelper(bestSolution, children.get(i), finalNode);
            }
            return;
        }

        ItemVO itemVO = itemVOMap.get(consumeExpression.get(index).getItemName());
        ItemConsumeSolutionTreeNode treeNode = parentNode.getChildren().get(index);
        treeNode.setWayEnum(ItemConsumeWayEnum.PRODUCE);
        calcBestSolutionHelper2(bestSolution, parentNode, finalNode, consumeExpression, index + 1);
        if (itemVO.isCanTrade()) {
            treeNode.setWayEnum(ItemConsumeWayEnum.BUY);
            calcBestSolutionHelper2(bestSolution, parentNode, finalNode, consumeExpression, index + 1);
        }
    }


    /**
     * 更新最佳解决方案
     */
    private void compareAndSetBestSolutionAtFinalNode(ItemConsumeSolution bestSolution,
                                                      ItemConsumeSolutionTreeNode parentNode) {
        ItemConsumeSolutionTreeNode root = parentNode;
        while (root.getParent() != null) {
            root = root.getParent();
        }


        ItemVO itemVO = itemVOMap.get(root.getItemName());
        // root must be ItemConsumeWayEnum.PRODUCE
        int cost = 0;
        int worker = 1;

        List<ConsumeExpressionTermVO> consumeExpression = itemVO.getConsumeExpression();
        List<ItemConsumeSolutionTreeNode> children = root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            ConsumeExpressionTermVO termVO = consumeExpression.get(i);
            ItemConsumeSolutionTreeNode child = children.get(i);
            cost = cost + termVO.getCount() * calcCost(child);
            worker = worker + termVO.getCount() * calcWorker(child);
        }

        int price = bestSolution.getSolutionEnum() == ItemConsumeSolutionEnum.PRODUCE ?
                itemVO.getBuyPrice() : itemVO.getSellPrice();
        float pricePerWorker = (float) (itemVO.getProduceCount() * (price - cost)) / worker;
        if (pricePerWorker > bestSolution.getPricePerWorker()) {
            bestSolution.setPricePerWorker(pricePerWorker);
            bestSolution.setRoot(copyNode(root, null));
        }
    }


    private int calcCost(ItemConsumeSolutionTreeNode treeNode) {
        ItemVO itemVO = itemVOMap.get(treeNode.getItemName());
        ItemConsumeWayEnum wayEnum = treeNode.getWayEnum();
        if (wayEnum == ItemConsumeWayEnum.BUY) {
            return itemVO.getBuyPrice();
        }


        int cost = 0;

        List<ConsumeExpressionTermVO> consumeExpression = itemVO.getConsumeExpression();
        List<ItemConsumeSolutionTreeNode> children = treeNode.getChildren();
        for (int i = 0; i < consumeExpression.size(); i++) {
            ConsumeExpressionTermVO termVO = consumeExpression.get(i);
            ItemConsumeSolutionTreeNode child = children.get(i);
            cost = cost + termVO.getCount() * calcCost(child);
        }
        return cost;
    }


    private int calcWorker(ItemConsumeSolutionTreeNode treeNode) {
        ItemVO itemVO = itemVOMap.get(treeNode.getItemName());
        ItemConsumeWayEnum wayEnum = treeNode.getWayEnum();
        if (wayEnum == ItemConsumeWayEnum.BUY) {
            return 0;
        }
        int worker = 1;

        List<ConsumeExpressionTermVO> consumeExpression = itemVO.getConsumeExpression();
        List<ItemConsumeSolutionTreeNode> children = treeNode.getChildren();
        for (int i = 0; i < consumeExpression.size(); i++) {
            ConsumeExpressionTermVO termVO = consumeExpression.get(i);
            ItemConsumeSolutionTreeNode child = children.get(i);
            worker = worker + termVO.getCount() * calcWorker(child);
        }
        return worker;
    }

    private ItemConsumeSolutionTreeNode copyNode(ItemConsumeSolutionTreeNode treeNode, ItemConsumeSolutionTreeNode parentCopy) {
        ItemConsumeSolutionTreeNode copy = new ItemConsumeSolutionTreeNode();
        copy.setItemName(treeNode.getItemName());
        copy.setParent(parentCopy);
        copy.setWayEnum(treeNode.getWayEnum());
        for (ItemConsumeSolutionTreeNode child : treeNode.getChildren()) {
            copy.getChildren().add(copyNode(child, copy));
        }
        return copy;
    }


    private void calcSolutionBrief(ItemConsumeSolution solution) {
        if (solution == null) {
            return;
        }
        ItemConsumeSolutionTreeNode root = solution.getRoot();

        List<ConsumeExpressionTermVO> list = new ArrayList<>();
        solutionStringHelper(root, 1, list);

        if (list.isEmpty()) {
            solution.setSolutionBrief("生产全部");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("购买：");
            for (ConsumeExpressionTermVO termVO : list) {
                sb.append(Integer.valueOf(termVO.getCount()));
                sb.append(termVO.getItemName());
                sb.append("，");
            }
            sb.append("其余生产");
            solution.setSolutionBrief(sb.toString());
        }
    }

    private void solutionStringHelper(ItemConsumeSolutionTreeNode node, int multi, List<ConsumeExpressionTermVO> list) {
        if (node == null) {
            return;
        }
        if (node.getWayEnum() == ItemConsumeWayEnum.BUY) {
            ConsumeExpressionTermVO termVO = new ConsumeExpressionTermVO();
            termVO.setCount(multi);
            termVO.setItemName(node.getItemName());
            list.add(termVO);
        } else {
            List<ConsumeExpressionTermVO> consumeExpression = itemVOMap.get(node.getItemName()).getConsumeExpression();
            List<ItemConsumeSolutionTreeNode> children = node.getChildren();
            for (int i = 0; i < children.size(); i++) {
                ItemConsumeSolutionTreeNode child = children.get(i);
                ConsumeExpressionTermVO termVO = consumeExpression.get(i);
                solutionStringHelper(child, termVO.getCount() * multi, list);
            }
        }
    }
}
