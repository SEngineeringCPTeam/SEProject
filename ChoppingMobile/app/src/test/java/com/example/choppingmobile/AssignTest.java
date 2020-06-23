package com.example.choppingmobile;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssignTest {
    Assign assign = new Assign();
    @Test
    public void setId() {
        assign.setId("qwert");
        assertEquals(assign.id,"qwert");
    }

    @Test
    public void setAuthority() {
        assign.setAuthority("qwert");
        assertEquals(assign.authority,"qwert");
    }
}