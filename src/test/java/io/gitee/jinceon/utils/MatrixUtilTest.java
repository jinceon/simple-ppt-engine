package io.gitee.jinceon.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatrixUtilTest {

    @Test
    void rowColumnTransform() {
        String[][] before = new String[][]{
                {"a", "b", "c", "d"},
                {"e", "f", "g", "h"},
                {"i", "j", "k", "l"}
        };

        String[][] expected = new String[][]{
                {"a", "e", "i"},
                {"b", "f", "j"},
                {"c", "g", "k"},
                {"d", "h", "l"}
        };
        assertArrayEquals(expected, MatrixUtil.rowColumnTransform(before));
    }

}