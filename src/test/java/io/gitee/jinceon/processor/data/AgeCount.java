package io.gitee.jinceon.processor.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class AgeCount {
    private String ageRange;
    private int count;
}
