package org.example.dungeon.vo;

import lombok.Data;
import org.example.dungeon.ItemConsumeWayVO;
import org.example.dungeon.enums.ItemConsumeSolutionEnum;

import java.util.ArrayList;
import java.util.List;


@Data
public class ItemConsumeSolution {
    private ItemConsumeSolutionTreeNode root;
    private ItemConsumeSolutionEnum solutionEnum;
    private float pricePerWorker;
}
