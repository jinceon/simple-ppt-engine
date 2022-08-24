package io.gitee.jinceon.processor.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class Sales {
    private String month;
    private long food;
    private long cloth;
    private long drink;
}
