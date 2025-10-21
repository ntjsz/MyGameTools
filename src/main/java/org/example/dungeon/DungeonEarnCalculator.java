package org.example.dungeon;

import org.example.dungeon.vo.ItemVO;

import java.util.HashMap;
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


    }
}
