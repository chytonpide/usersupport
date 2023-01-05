package com.nconnect.usersupport;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Sandbox {

    @Test
    public void testThatTheInitialValueIsForOptimization() {
        //given
        List<String> strings = new ArrayList<>(1);

        //when
        strings.add("a");
        strings.add("b");
        strings.add("c");

        //then
        assertEquals(3, strings.size());
    }
}
