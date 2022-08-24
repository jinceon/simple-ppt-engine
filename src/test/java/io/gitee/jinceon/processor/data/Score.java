package io.gitee.jinceon.processor.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public
class Score {
    private String name;
    private int math;
    private int chinese;
    private int english;
    private int chemistry;
    private int physics;
}
