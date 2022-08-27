package io.gitee.jinceon.processor.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trend {
    private String goods;
    private int thisYear;
    private int lastYear;
    private int theYearBeforeLast;
}
