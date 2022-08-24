package io.gitee.jinceon.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair {
    private String label; // for display
    private String prop;  // for value
}
