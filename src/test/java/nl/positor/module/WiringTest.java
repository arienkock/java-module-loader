package nl.positor.module;

import org.junit.Test;

/**
 * Created by Arien on 24-Mar-16.
 */
public class WiringTest {
    @Test
    public void testDEpendency() {
        Boot.module("printer.Printer")
                .referencing()
    }
}
