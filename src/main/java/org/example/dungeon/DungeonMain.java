package org.example.dungeon;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.dungeon.vo.ConsumeExpressionTermVO;
import org.example.dungeon.vo.InputExcelVO;
import org.example.dungeon.vo.ItemVO;
import org.example.utils.FileUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class DungeonMain {

    private static final String ROOT_DIR = "dungeon";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.####");

    public static void main(String[] args) {
        DungeonMain main = new DungeonMain();
        main.execute();
    }

    public void execute() {
        List<String> inputLines = FileUtils.readFileAllLines(ROOT_DIR, "input.csv");
        List<InputExcelVO> inputExcelVOList = parseInputExcel(inputLines);
        List<ItemVO> itemVOList = convertToItemVOList(inputExcelVOList);

        DungeonEarnCalculator calculator = new DungeonEarnCalculator();
        calculator.calc(itemVOList);
        List<String> outputLines = convertToOutputExcel(itemVOList);
        FileUtils.writeAllLinesToFile(outputLines, ROOT_DIR, "output.csv");
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

    private List<String> convertToOutputExcel(List<ItemVO> itemVOList) {
        List<String> lines = new ArrayList<>();
        lines.add("物品名称,工人名称,售卖最佳价格每工人,售卖方案,生产最佳价格每工人,生产方案");
        for (ItemVO itemVO : itemVOList) {
            StringBuilder sb = new StringBuilder();
            sb.append(itemVO.getItemName());
            sb.append(',');
            sb.append(itemVO.getWorker());
            sb.append(',');
            if (itemVO.getSellSolution() != null) {
                sb.append(formatFloat(itemVO.getSellSolution().getPricePerWorker()));
            }
            sb.append(',');
            if (itemVO.getSellSolution() != null) {
                sb.append(itemVO.getSellSolution().getSolutionBrief());
            }
            sb.append(',');
            if (itemVO.getProduceSolution() != null) {
                sb.append(formatFloat(itemVO.getProduceSolution().getPricePerWorker()));
            }
            sb.append(',');
            if (itemVO.getProduceSolution() != null) {
                sb.append(itemVO.getProduceSolution().getSolutionBrief());
            }
            lines.add(sb.toString());
        }
        return lines;
    }

    private String formatFloat(float f) {
        return DECIMAL_FORMAT.format(f);
    }

}
