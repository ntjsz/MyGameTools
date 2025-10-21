package org.example.dungeon.vo;

import lombok.Data;
import org.example.dungeon.ItemConsumeWayVO;

import java.util.ArrayList;
import java.util.List;


@Data
public class ItemConsumeSolution {
    private List<ItemConsumeWayVO> solutionStepList;
    private float pricePerWorker;

    public ItemConsumeSolution(List<ItemConsumeWayVO> solutionStepList, float pricePerWorker) {
        this.solutionStepList = new ArrayList<>(solutionStepList);
        this.pricePerWorker = pricePerWorker;
    }
}
