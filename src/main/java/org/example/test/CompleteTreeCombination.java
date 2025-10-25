package org.example.test;

import java.util.*;

public class CompleteTreeCombination {

    static class TreeNode {
        List<TreeNode> children;

        public TreeNode() {
            this.children = new ArrayList<>();
        }

        public void addChild(TreeNode child) {
            this.children.add(child);
        }
    }

    /**
     * 更简洁的实现方式：使用节点列表和索引
     */
    public static List<String> generateCompleteCombinations(TreeNode root) {
        List<String> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        // 获取树的所有节点（按DFS顺序）
        List<TreeNode> allNodes = new ArrayList<>();
        collectAllNodes(root, allNodes);

        // 为所有节点生成所有可能的赋值组合
        generateForAllNodes(allNodes, new char[allNodes.size()], 0, result);
        return result;
    }

    /**
     * 收集树的所有节点（DFS顺序）
     */
    private static void collectAllNodes(TreeNode node, List<TreeNode> nodes) {
        nodes.add(node);
        for (TreeNode child : node.children) {
            collectAllNodes(child, nodes);
        }
    }

    /**
     * 为所有节点生成赋值组合
     */
    private static void generateForAllNodes(List<TreeNode> allNodes, char[] current,
                                            int index, List<String> result) {
        if (index == allNodes.size()) {
            // 所有节点都已赋值，保存结果
            result.add(new String(current));
            return;
        }

        // 当前节点尝试两种值
        current[index] = 'a';
        generateForAllNodes(allNodes, current, index + 1, result);

        current[index] = 'b';
        generateForAllNodes(allNodes, current, index + 1, result);
    }

    /**
     * 构建测试树
     */
    public static TreeNode buildTestTree() {
        // 构建树形状：
        //       root
        //      / | \
        //     c1 c2 c3
        //    / \   \
        //   c4 c5   c6

        TreeNode root = new TreeNode();

        TreeNode c1 = new TreeNode();
        TreeNode c2 = new TreeNode();
        TreeNode c3 = new TreeNode();

        root.addChild(c1);
        root.addChild(c2);
        root.addChild(c3);

        TreeNode c4 = new TreeNode();
        TreeNode c5 = new TreeNode();
        TreeNode c6 = new TreeNode();

        c1.addChild(c4);
        c1.addChild(c5);
        c2.addChild(c6);

        return root;
    }

    /**
     * 构建简单测试树
     */
    public static TreeNode buildSimpleTree() {
        // 构建树形状：
        //       root
        //      /   \
        //     c1   c2

        TreeNode root = new TreeNode();

        TreeNode c1 = new TreeNode();
        TreeNode c2 = new TreeNode();

        root.addChild(c1);
        root.addChild(c2);

        return root;
    }

    public static void main(String[] args) {
        System.out.println("=== 简单树测试 ===");
        TreeNode simpleTree = buildSimpleTree();

        System.out.println("\n方法1 - 所有完整组合:");
        List<String> combinations1 = generateCompleteCombinations(simpleTree);
        for (String comb : combinations1) {
            System.out.println(comb);
        }

        System.out.println("\n组合数量: " + combinations1.size());
        System.out.println("理论数量: 2^3 = " + (int)Math.pow(2, 3));

        System.out.println("\n=== 复杂树测试 ===");
        TreeNode testTree = buildTestTree();
        List<String> combinations = generateCompleteCombinations(testTree);

        System.out.println("节点数量: 7");
        System.out.println("所有组合数量: " + combinations.size());
        System.out.println("理论数量: 2^7 = " + (int)Math.pow(2, 7));

        // 显示部分结果
        System.out.println("\n前10个组合:");
        for (int i = 0; i < Math.min(10, combinations.size()); i++) {
            System.out.println(combinations.get(i));
        }
    }
}
