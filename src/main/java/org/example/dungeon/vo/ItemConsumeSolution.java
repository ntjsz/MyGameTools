package org.example.dungeon.vo;

import lombok.Data;
import org.example.dungeon.ItemConsumeWayVO;

import java.util.ArrayList;
import java.util.List;


@Data
public class ItemConsumeSolution {
    private List<ItemConsumeWayVO> solutionStepList;
    private float pricePerWorker;
    private int workerCount;

    public ItemConsumeSolution() {
        this.pricePerWorker = -1;
    }
}
