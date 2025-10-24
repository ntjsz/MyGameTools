package org.example.dungeon.vo;

import lombok.Data;
import org.example.dungeon.enums.ItemConsumeSolutionEnum;


@Data
public class ItemConsumeSolution {
    private ItemConsumeSolutionTreeNode root;
    private ItemConsumeSolutionEnum solutionEnum;
    private float pricePerWorker;
    private String solutionBrief;
}
