package org.example.dungeon;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.dungeon.vo.ConsumeExpressionTermVO;
import org.example.dungeon.vo.InputExcelVO;
import org.example.dungeon.vo.ItemVO;
import org.example.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class DungeonMain {

    private static final String ROOT_DIR = "dungeon";

    public static void main(String[] args) {
        DungeonMain main = new DungeonMain();
        main.execute();
    }

    public void execute() {
        List<String> lines = FileUtils.readFileAllLines(ROOT_DIR, "input.csv");
        List<InputExcelVO> inputExcelVOList = parseInputExcel(lines);
        List<ItemVO> itemVOList = convertToItemVOList(inputExcelVOList);

        DungeonEarnCalculator calculator = new DungeonEarnCalculator();
        calculator.calc(itemVOList);
        System.out.println(1);
    }

    private List<InputExcelVO> parseInputExcel(List<String> lines) {
        List<InputExcelVO> inputExcelVOList = new ArrayList<>();
        // skip first title line
        for (int i = 1; i < lines.size(); i++) {
            try {
                inputExcelVOList.add(parseInputExcelVO(lines.get(i)));
            } catch (Exception e) {
                log.error("输入文件第{}行转换失败，{}", i, lines.get(i), e);
            }
        }
        return inputExcelVOList;
    }

    private InputExcelVO parseInputExcelVO(String line) {
        String[] fields = line.split(",");
        InputExcelVO inputExcelVO = new InputExcelVO();
        inputExcelVO.setItemName(fields[0]);
        inputExcelVO.setWorker(fields[1]);

        int buyPrice = Integer.parseInt(fields[2]);
        inputExcelVO.setBuyPrice(buyPrice);

        if (StringUtils.isBlank(fields[3])) {
            inputExcelVO.setSellPrice(inputExcelVO.getBuyPrice() / 3);
        } else {
            inputExcelVO.setSellPrice(Integer.parseInt(fields[3]));
        }

        inputExcelVO.setProduceCount(Integer.parseInt(fields[4]));

        if (fields.length > 5) {
            inputExcelVO.setConsumeExpression(fields[5]);
        }
        return inputExcelVO;
    }

    private List<ItemVO> convertToItemVOList(List<InputExcelVO> inputExcelVOList) {
        List<ItemVO> itemVOList = new ArrayList<>();
        for (InputExcelVO inputExcelVO : inputExcelVOList) {
            ItemVO itemVO = new ItemVO();
            itemVOList.add(itemVO);

            itemVO.setItemName(inputExcelVO.getItemName());
            itemVO.setWorker(inputExcelVO.getWorker());
            itemVO.setCanTrade(inputExcelVO.getBuyPrice() > 0);
            if (itemVO.isCanTrade()) {
                itemVO.setBuyPrice(inputExcelVO.getBuyPrice());
                itemVO.setSellPrice(inputExcelVO.getSellPrice());
            }
            itemVO.setProduceCount(inputExcelVO.getProduceCount());
            itemVO.setConsumeExpression(parseConsumeExpression(inputExcelVO.getConsumeExpression()));
        }
        return itemVOList;
    }

    private List<ConsumeExpressionTermVO> parseConsumeExpression(String expression) {
        List<ConsumeExpressionTermVO> result = new ArrayList<>();
        // 水晶-3石头-1
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        expression = StringUtils.deleteWhitespace(expression);
        // 水晶-3
        Pattern pattern = Pattern.compile("([^0-9\\-]+)-(\\d+)");
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            ConsumeExpressionTermVO termVO = new ConsumeExpressionTermVO();
            termVO.setItemName(matcher.group(1));
            termVO.setCount(Integer.parseInt(matcher.group(2)));
            result.add(termVO);
        }

        return result;
    }
}
