package eu.quartum.cc1101_driver.utils;

/**
 * Created by pierre on 20/04/17.
 */

public class BitUtils {

    /**
     * Reads a bit from a number
     *
     * @param value the number to read the bit from
     * @param pos the position of the bit, 0 being the LSB
     */
    public static byte bitRead(byte value, int pos) {
        byte mask = (byte) (0x01 << pos);
        return (byte) ((value & mask) >> pos);
    }

    /**
     * Returns a byte representing the n bits between start and end in
     * the value parameter
     *
     * end < 8
     * start >= 0
     * start <= end
     *
     * @param value
     * @param start
     * @param end
     * @return
     */
    public static byte bitsRead(byte value, int start, int end) {
        byte mask = 1;
        for (int i = start + 1; i <= end; i++) {
            mask = (byte) (mask << 1);
            mask |= 1;
        }

        mask = (byte) (mask << start);
        byte masked = (byte) (value & mask);
        byte result = (byte) ((masked & 0xFF) >>> start);
        return result;
    }

    /**
     * Returns the string hex representation of a byte array
     * @param array the array
     * @return a string
     */
    public static String getHexValue(byte[] array){
        char[] symbols="0123456789ABCDEF".toCharArray();
        char[] hexValue = new char[array.length * 2];

        for(int i=0;i<array.length;i++)
        {
            //convert the byte to an int
            int current = array[i] & 0xff;
            //determine the Hex symbol for the last 4 bits
            hexValue[i*2+1] = symbols[current & 0x0f];
            //determine the Hex symbol for the first 4 bits
            hexValue[i*2] = symbols[current >> 4];
        }
        return String.valueOf(hexValue);
    }

    /**
     * Returns a String hex representation of a single byte
     * @param data the byte
     * @return a string
     */
    public static String getHexValue(byte data){
        char[] symbols="0123456789ABCDEF".toCharArray();
        char[] hexValue = new char[2];

        //convert the byte to an int
        int current = data & 0xff;
        //determine the Hex symbol for the last 4 bits
        hexValue[1] = symbols[current & 0x0f];
        //determine the Hex symbol for the first 4 bits
        hexValue[0] = symbols[current >> 4];
        return String.valueOf(hexValue);
    }
}
