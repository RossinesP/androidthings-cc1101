package eu.quartum.cc1101_driver;


import eu.quartum.cc1101_driver.utils.BitUtils;

/**
 * Created by pierre on 17/03/17.
 */

public class CC1101Packet {
    public byte length;
    public byte[] data = new byte[64];
    public boolean crcOk;
    public byte rssi;
    public byte lqi;

    @Override
    public String toString() {
        return "Packet of length " + getLength() + ", crc is " + (crcOk ? "ok" : "invalid") + ", rssi is " + BitUtils.getHexValue(rssi) + ", lqi is " + BitUtils.getHexValue(lqi) + ", data is " + BitUtils.getHexValue(data);
    }

    /**
     * Returns the length of the packet as an int.
     * The byte length stored in the CC1101Packet class is
     * signed even though it represents an unsigned value.
     * Use this method to get the proper length
     *
     * @return the length of the packet
     */
    public int getLength() {
        return length & 0xFF;
    }


}
