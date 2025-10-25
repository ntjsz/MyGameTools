package org.example.dungeon;

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

    private static final String COIN = "金币";

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

        List<ItemConsumeSolutionTreeNode> allNodeList = new ArrayList<>();
        buildTreeAndCollectAllNodes(root, allNodeList);


        tryCompleteCombinations(bestSolution, allNodeList, 0);
        calcSolutionBrief(bestSolution);
        return bestSolution;
    }

    private void buildTreeAndCollectAllNodes(ItemConsumeSolutionTreeNode parentNode, List<ItemConsumeSolutionTreeNode> allNodeList) {
        allNodeList.add(parentNode);
        List<ConsumeExpressionTermVO> consumeExpression = itemVOMap.get(parentNode.getItemName()).getConsumeExpression();
        List<ItemConsumeSolutionTreeNode> childrenNodeList = parentNode.getChildren();
        for (ConsumeExpressionTermVO termVO : consumeExpression) {
            ItemConsumeSolutionTreeNode treeNode = new ItemConsumeSolutionTreeNode();
            treeNode.setParent(parentNode);
            childrenNodeList.add(treeNode);

            String itemName = termVO.getItemName();
            treeNode.setItemName(itemName);
            buildTreeAndCollectAllNodes(treeNode, allNodeList);
        }
    }

    /**
     * 递归构建solution tree
     */
    private void tryCompleteCombinations(ItemConsumeSolution bestSolution,
                                         List<ItemConsumeSolutionTreeNode> allNodeList,
                                         int index) {
        if (index == allNodeList.size()) {
            // 所有节点都已赋值，更新最佳方案
            ItemConsumeSolutionTreeNode root = allNodeList.get(0);
            updateBestSolution(bestSolution, root);
            updateSubTreeSkipRecursively(root, false);
            return;
        }

        ItemConsumeSolutionTreeNode treeNode = allNodeList.get(index);
        if (treeNode.isSkip()) {
            tryCompleteCombinations(bestSolution, allNodeList, index + 1);
            return;
        }

        treeNode.setWayEnum(ItemConsumeWayEnum.PRODUCE);
        tryCompleteCombinations(bestSolution, allNodeList, index + 1);

        ItemVO itemVO = itemVOMap.get(treeNode.getItemName());
        if (index != 0 && itemVO.isCanTrade()) {
            treeNode.setWayEnum(ItemConsumeWayEnum.BUY);
            // 直接购买的商品，不需要计算子树
            updateSubTreeSkipRecursively(treeNode, true);
            tryCompleteCombinations(bestSolution, allNodeList, index + 1);
        }
    }

    private void updateSubTreeSkipRecursively(ItemConsumeSolutionTreeNode treeNode, boolean skip) {
        treeNode.setSkip(skip);
        for (ItemConsumeSolutionTreeNode child : treeNode.getChildren()) {
            updateSubTreeSkipRecursively(child, skip);
        }
    }


    /**
     * 更新最佳解决方案
     */
    private void updateBestSolution(ItemConsumeSolution bestSolution, ItemConsumeSolutionTreeNode treeNode) {
        ItemConsumeSolutionTreeNode root = treeNode;
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
        if (node == null || node.getItemName().equals(COIN)) {
            return;
        }
        if (node.getWayEnum() == ItemConsumeWayEnum.BUY) {
            ConsumeExpressionTermVO termVO = new ConsumeExpressionTermVO();
            termVO.setItemName(node.getItemName());
            termVO.setCount(multi);
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
