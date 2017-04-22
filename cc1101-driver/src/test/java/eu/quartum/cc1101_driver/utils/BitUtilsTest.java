package eu.quartum.cc1101_driver.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by pierre on 20/04/17.
 */
public class BitUtilsTest {
    @Test
    public void bitRead() throws Exception {
        byte result = BitUtils.bitRead((byte) 0x01, 0);
        assertEquals(1, result);

        result = BitUtils.bitRead((byte) 0b01000000, 6);
        assertEquals(1, result);

        result = BitUtils.bitRead((byte) 0b00000000, 3);
        assertEquals(0, result);
    }

    @Test
    public void bitsRead() throws Exception {
        byte result = BitUtils.bitsRead((byte) 0b11111111, 0, 7);
        assertEquals((byte) 0xFF, result);

        result = BitUtils.bitsRead((byte) 0b00001110, 1, 3);
        assertEquals((byte) 0b111, result);

        result = BitUtils.bitsRead((byte) 0b01001000, 1, 6);
        assertEquals((byte) 0b100100, result);

        result = BitUtils.bitsRead((byte) 0b11000000, 6, 6);
        assertEquals((byte) 0b1, result);

        result = BitUtils.bitsRead((byte) 0b10000000, 7, 7);
        assertEquals((byte) 0b1, result);
    }
}