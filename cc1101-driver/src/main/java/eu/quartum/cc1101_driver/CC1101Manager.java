package eu.quartum.cc1101_driver;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import eu.quartum.cc1101_driver.utils.BitUtils;


/**
 * Created by pierre on 17/03/17.
 */

public class CC1101Manager {
    public final static String TAG = CC1101Manager.class.getSimpleName();
    private final static int CC1101_BUFFER_LEN = 64;
    private final static int CC1101_DATA_LEN = CC1101_BUFFER_LEN - 3;
    private PeripheralManagerService mPMS;
    private String mSPIDeviceName;
    private SpiDevice mSPIDevice;
    private String mGDO0Name;
    private Gpio mGDO0;

    private String mSSName;
    private Gpio mSS;

    private final static int DELAY_CHECKSTATUS = 5000;
    private Handler mHandler = new Handler();

    private PacketListener mListener;

    private CC1101Config mConfig;

    private Runnable mCheckStatusRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Checking the status");
            byte marcState = getMarcState();
            if (marcState != CC1101Constants.MARCSTATE_RX) {
                printMarcState();
                setRxState();
            } else {
                Log.d(TAG, "Nothing to do");
            }

            mHandler.postDelayed(this, DELAY_CHECKSTATUS);
        }
    };

    private GpioCallback mGDO0Callback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            CC1101Packet packet = receiveData();
            if (BuildConfig.DEBUG) Log.v(TAG, "Packet received : " + packet.toString());
            if (mListener != null) {
                mListener.onNewPacket(packet);
            }
            return true;
        }
    };

    /**
     * Builds a new CC1101Manager to drive a CC1101 chip.
     * The spiDeviceName should contain the name of the spi device the CC1101 chip is
     * connected on, but the slave select (also called SS, CE or CSn) must be connected on one of the
     * GPIO ports instead of the designated SS port for that spi device.
     * The GDO0 port is used by the CC1101 chip to notify a new incoming message when the
     * IOCFG2, IOCFG1 and IOCFG0 are respectively set to 0x29, 0x2E and 0x06,
     *
     * @param spiDeviceName the SPI device name
     * @param gpioGDO0Name the GDO0 gpio name
     * @param gpioSlaveSelectName the gpio port used as a slave select for the CC1101 chip
     * @param registerValues the register settings used to configure the CC1101 chip. See the CC1101
     *                       datasheet for instructions. Register addresses are available in the
     *                       CC1101Constants class
     */
    public CC1101Manager(String spiDeviceName, String gpioGDO0Name, String gpioSlaveSelectName, CC1101Config registerValues) {
        mPMS = new PeripheralManagerService();
        mSPIDeviceName = spiDeviceName;
        mGDO0Name = gpioGDO0Name;
        mSSName = gpioSlaveSelectName;
        mConfig = registerValues;
        List<String> deviceList = mPMS.getSpiBusList();
        if (!deviceList.contains(spiDeviceName)) {
            if (BuildConfig.DEBUG) Log.e(TAG, "SPI device does not exist");
        }

        List<String> gpioList = mPMS.getGpioList();
        if (gpioGDO0Name == null || !gpioList.contains(gpioGDO0Name)) {
            throw new InvalidParameterException("The GDO0 gpio name " + gpioGDO0Name + " does not exist");
        }

        if (gpioSlaveSelectName == null || !gpioList.contains(gpioSlaveSelectName)) {
            throw new InvalidParameterException("The slave select gpio name " + gpioSlaveSelectName + " does not exist");
        }
    }

    /**
     * Initializes the CC1101 chip. In a debug build, a correct setup
     * should log values such as 0x00 and 0x14 for partNum and version, but this might depend
     * on your hardware.
     */
    public void setup() {
        connect();
        init();
        byte partNum = readReg(CC1101Constants.CC1101_PARTNUM, CC1101Constants.CC1101_STATUS_REGISTER);
        byte version = readReg(CC1101Constants.CC1101_VERSION, CC1101Constants.CC1101_STATUS_REGISTER);

        if (BuildConfig.DEBUG) Log.v(TAG, "init done, partNum is " + BitUtils.getHexValue(partNum) + ", version is " + BitUtils.getHexValue(version));
        printMarcState();

        setListening();
    }

    /**
     * Puts the chip in Rx state and start listening for incoming messages.
     */
    public void setListening() {
        try {
            mGDO0.registerGpioCallback(mGDO0Callback);
            mGDO0.setEdgeTriggerType(Gpio.EDGE_RISING);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while registering a gpio callback on GDO0");
            ioe.printStackTrace();
        }
        mHandler.removeCallbacks(mCheckStatusRunnable);
        mHandler.postDelayed(mCheckStatusRunnable, DELAY_CHECKSTATUS);
    }

    private void connect() {
        try {
            mSPIDevice = mPMS.openSpiDevice(mSPIDeviceName);
            mSPIDevice.setFrequency(16000000);
            mSPIDevice.setDelay(1000);
            mSPIDevice.setCsChange(true);
            mSPIDevice.setBitJustification(false);
            mSPIDevice.setBitsPerWord(8);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error opening the SPI device");
        }
        try {
            mGDO0 = mPMS.openGpio(mGDO0Name);
            mGDO0.setDirection(Gpio.DIRECTION_IN);

            mSS = mPMS.openGpio(mSSName);
            mSS.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Exception while opening " + mSSName + " and " + mGDO0Name);
            ioe.printStackTrace();
        }
    }

    private void selectChip() {
        if (mSS != null) {
            try {
                mSS.setValue(false);
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Error selecting chip");
            }
        }
    }

    private void deselectChip() {
        if (mSS != null) {
            try {
                mSS.setValue(true);
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Error deselecting chip");
            }
        }
    }


    /**
     * Defines a packetListener. When the chip is configured such as the CC1101 chip notifies
     * incoming messages on GDO0, the PacketListener will be called on all incoming packages.
     *
     * @param listener the listener. Can be null to unset the listener.
     */
    public void setPacketListener(@Nullable PacketListener listener) {
        mListener = listener;
    }

    /**
     * Displays all registers in the logcat. Can be called during debug to check that all registers
     * well correctly set during setup.
     */
    public void printRegisters() {
        for (byte i = CC1101Constants.CC1101_IOCFG2; i <= CC1101Constants.CC1101_TEST0; i++) {
            Log.v(TAG, "Register " + i + " = " + BitUtils.getHexValue(readReg(i, CC1101Constants.CC1101_CONFIG_REGISTER)));
        }
    }

    /**
     * Displays the MarcState (as described in the CC1101 datasheet) in the logcat
     * All possible values are defined in CC1101Constants.
     */
    public void printMarcState() {
        Log.v(TAG, "Marcstate is " + BitUtils.getHexValue(getMarcState()));
    }

    private void writeReg(byte regAddr, byte value) {
        selectChip();
        waitDelay(2);
        byte[] data = new byte[] { regAddr, value };
        try {
            mSPIDevice.write(data, data.length);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while writing " + value + " to " + regAddr);
        }
        waitDelay(2);
        deselectChip();
    }

    private void waitDelay(int micros) {
        try {
            Thread.sleep(0, micros * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Untested
     * @param regAddr
     * @param buffer
     */
    private void writeBurstReg(byte regAddr, byte[] buffer) {
        byte address = (byte) (regAddr | CC1101Constants.WRITE_BURST);

        selectChip();
        waitDelay(2);
        try {
            byte[] data = new byte[buffer.length + 1];
            data[0] = regAddr;
            for (int i = 0; i < buffer.length; i++) {
                data[i] = buffer[i - 1];
            }
            mSPIDevice.write(data, data.length);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error during burst write");
            ioe.printStackTrace();
        }
        waitDelay(2);
        deselectChip();
    }

    private void cmdStrobe(byte cmd) {
        selectChip();
        waitDelay(2);
        try {
            mSPIDevice.write(new byte[] { cmd }, 1);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while send a cmb strobe");
            ioe.printStackTrace();
        }
        waitDelay(2);
        deselectChip();
    }

    private byte readReg(byte regAddr, byte regType) {
        selectChip();
        waitDelay(2);
        byte address = (byte) (regAddr | regType);
        byte[] buffer = new byte[1];
        try {
            mSPIDevice.write(new byte[] { address }, 1);
            mSPIDevice.read(buffer, 1);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while reading a registry");
            ioe.printStackTrace();
        }
        waitDelay(2);
        deselectChip();
        return buffer[0];
    }

    private byte[] readBurstReg(byte regAddr, int length) {
        selectChip();
        waitDelay(2);
        byte address = (byte) (regAddr | CC1101Constants.READ_BURST);
        byte[] buffer = new byte[length];
        try {
            mSPIDevice.write(new byte[] { address }, 1);
            mSPIDevice.read(buffer, length);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error during a burst read");
            ioe.printStackTrace();
        }
        waitDelay(2);
        deselectChip();
        return buffer;
    }

    private void reset() {
        deselectChip();
        waitDelay(5);
        selectChip();
        waitDelay(10);
        deselectChip();
        waitDelay(41);
        selectChip();

        try {
            mSPIDevice.write(new byte[] { CC1101Constants.CC1101_SRES }, 1);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error during reset");
            ioe.printStackTrace();
        }
        deselectChip();
    }

    private void setDefaultRegs() {
        byte[] config = mConfig.getConfig();
        writeReg(CC1101Constants.CC1101_IOCFG2, config[CC1101Constants.CC1101_IOCFG2]);
        writeReg(CC1101Constants.CC1101_IOCFG1, config[CC1101Constants.CC1101_IOCFG1]);
        writeReg(CC1101Constants.CC1101_IOCFG0, config[CC1101Constants.CC1101_IOCFG0]);
        writeReg(CC1101Constants.CC1101_FIFOTHR, config[CC1101Constants.CC1101_FIFOTHR]);
        writeReg(CC1101Constants.CC1101_SYNC1, config[CC1101Constants.CC1101_SYNC1]);
        writeReg(CC1101Constants.CC1101_SYNC0, config[CC1101Constants.CC1101_SYNC0]);
        writeReg(CC1101Constants.CC1101_PKTLEN, config[CC1101Constants.CC1101_PKTLEN]);
        writeReg(CC1101Constants.CC1101_PKTCTRL1, config[CC1101Constants.CC1101_PKTCTRL1]);
        writeReg(CC1101Constants.CC1101_PKTCTRL0, config[CC1101Constants.CC1101_PKTCTRL0]);
        writeReg(CC1101Constants.CC1101_ADDR, config[CC1101Constants.CC1101_ADDR]);
        writeReg(CC1101Constants.CC1101_CHANNR, config[CC1101Constants.CC1101_CHANNR]);
        writeReg(CC1101Constants.CC1101_FSCTRL1, config[CC1101Constants.CC1101_FSCTRL1]);
        writeReg(CC1101Constants.CC1101_FSCTRL0, config[CC1101Constants.CC1101_FSCTRL0]);
        writeReg(CC1101Constants.CC1101_FREQ2, config[CC1101Constants.CC1101_FREQ2]);
        writeReg(CC1101Constants.CC1101_FREQ1, config[CC1101Constants.CC1101_FREQ1]);
        writeReg(CC1101Constants.CC1101_FREQ0, config[CC1101Constants.CC1101_FREQ0]);
        writeReg(CC1101Constants.CC1101_MDMCFG4, config[CC1101Constants.CC1101_MDMCFG4]);
        writeReg(CC1101Constants.CC1101_MDMCFG3, config[CC1101Constants.CC1101_MDMCFG3]);
        writeReg(CC1101Constants.CC1101_MDMCFG2, config[CC1101Constants.CC1101_MDMCFG2]);
        writeReg(CC1101Constants.CC1101_MDMCFG1, config[CC1101Constants.CC1101_MDMCFG1]);
        writeReg(CC1101Constants.CC1101_MDMCFG0, config[CC1101Constants.CC1101_MDMCFG0]);
        writeReg(CC1101Constants.CC1101_DEVIATN, config[CC1101Constants.CC1101_DEVIATN]);
        writeReg(CC1101Constants.CC1101_MCSM2, config[CC1101Constants.CC1101_MCSM2]);
        writeReg(CC1101Constants.CC1101_MCSM1, config[CC1101Constants.CC1101_MCSM1]);
        writeReg(CC1101Constants.CC1101_MCSM0, config[CC1101Constants.CC1101_MCSM0]);
        writeReg(CC1101Constants.CC1101_FOCCFG, config[CC1101Constants.CC1101_FOCCFG]);
        writeReg(CC1101Constants.CC1101_BSCFG, config[CC1101Constants.CC1101_BSCFG]);
        writeReg(CC1101Constants.CC1101_AGCCTRL2, config[CC1101Constants.CC1101_AGCCTRL2]);
        writeReg(CC1101Constants.CC1101_AGCCTRL1, config[CC1101Constants.CC1101_AGCCTRL1]);
        writeReg(CC1101Constants.CC1101_AGCCTRL0, config[CC1101Constants.CC1101_AGCCTRL0]);
        writeReg(CC1101Constants.CC1101_WOREVT1, config[CC1101Constants.CC1101_WOREVT1]);
        writeReg(CC1101Constants.CC1101_WOREVT0, config[CC1101Constants.CC1101_WOREVT0]);
        writeReg(CC1101Constants.CC1101_WORCTRL, config[CC1101Constants.CC1101_WORCTRL]);
        writeReg(CC1101Constants.CC1101_FREND1, config[CC1101Constants.CC1101_FREND1]);
        writeReg(CC1101Constants.CC1101_FREND0, config[CC1101Constants.CC1101_FREND0]);
        writeReg(CC1101Constants.CC1101_FSCAL3, config[CC1101Constants.CC1101_FSCAL3]);
        writeReg(CC1101Constants.CC1101_FSCAL2, config[CC1101Constants.CC1101_FSCAL2]);
        writeReg(CC1101Constants.CC1101_FSCAL1, config[CC1101Constants.CC1101_FSCAL1]);
        writeReg(CC1101Constants.CC1101_FSCAL0, config[CC1101Constants.CC1101_FSCAL0]);
        writeReg(CC1101Constants.CC1101_RCCTRL1, config[CC1101Constants.CC1101_RCCTRL1]);
        writeReg(CC1101Constants.CC1101_RCCTRL0, config[CC1101Constants.CC1101_RCCTRL0]);
        writeReg(CC1101Constants.CC1101_FSTEST, config[CC1101Constants.CC1101_FSTEST]);
        writeReg(CC1101Constants.CC1101_PTEST, config[CC1101Constants.CC1101_PTEST]);
        writeReg(CC1101Constants.CC1101_AGCTEST, config[CC1101Constants.CC1101_AGCTEST]);
        writeReg(CC1101Constants.CC1101_TEST2, config[CC1101Constants.CC1101_TEST2]);
        writeReg(CC1101Constants.CC1101_TEST1, config[CC1101Constants.CC1101_TEST1]);
        writeReg(CC1101Constants.CC1101_TEST0, config[CC1101Constants.CC1101_TEST0]);
    }

    private void init() {
        reset();
        byte patableLevel = (byte) 0xC0;
        spiWrite(new byte[] { patableLevel }, CC1101Constants.CC1101_PATABLE);

        setDefaultRegs();
    }

    private void setPowerDownState() {
        cmdStrobe(CC1101Constants.CC1101_SIDLE);
        cmdStrobe(CC1101Constants.CC1101_SPWD);
    }

    /**
     * Sets the chip in RX state
     *
     */
    public void setRxState() {
        cmdStrobe(CC1101Constants.CC1101_SRX);
        //printMarcState();
    }

    /**
     * Sets the chip in TX state
     */
    public void setTxState() {
        cmdStrobe(CC1101Constants.CC1101_STX);
    }

    /**
     * Sets the chip in Idle state
     */
    public void setIdleState() {
        cmdStrobe(CC1101Constants.CC1101_SIDLE);
    }


    private void flushRxFifo() {
        cmdStrobe(CC1101Constants.CC1101_SFRX);
    }

    private void flushTxFifo() {
        cmdStrobe(CC1101Constants.CC1101_SFTX);
    }

    private byte readStatusReg(byte regAddr) {
        return readReg(regAddr, CC1101Constants.CC1101_STATUS_REGISTER);
    }

    private byte readConfigReg(byte regAddr) {
        return readReg(regAddr, CC1101Constants.CC1101_CONFIG_REGISTER);
    }

    // Untested
    private boolean sendData(CC1101Packet packet) {
        byte marcState;

        setTxState();
        while (getMarcState() != CC1101Constants.MARCSTATE_TX) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Error while sleeping 1ms");
                ie.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while sleeping 500ms");
            ie.printStackTrace();
        }

        writeReg(CC1101Constants.CC1101_TXFIFO, packet.length);

        setTxState();

        marcState = getMarcState();
        if ((marcState != CC1101Constants.MARCSTATE_TX) && (marcState != CC1101Constants.MARCSTATE_TX_END) && (marcState != CC1101Constants.MARCSTATE_RXTX_SWITCH)) {
            setIdleState();
            flushTxFifo();
            setTxState();
            return false;
        }

        try {
            mGDO0.setEdgeTriggerType(Gpio.EDGE_FALLING);
            mGDO0.registerGpioCallback(new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    setTxState();

                    if ((readStatusReg(CC1101Constants.CC1101_TXBYTES) & (byte) 0x7F) == 0) {
                        return true;
                    }
                    gpio.unregisterGpioCallback(this);
                    if (BuildConfig.DEBUG) Log.v(TAG, "Data sent successfully");
                    return true;
                }
            });
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while waiting for the GDO0 pin to go low");
            ioe.printStackTrace();
        }
        return true;
    }

    private byte getMarcState() {
        return (byte) (readStatusReg(CC1101Constants.CC1101_MARCSTATE) & 0x1F);
    }

    /**
     * Call this method to read the RXFIFO from the CC1101 chip when a new packet is received.
     *
     * If you configure the GDO0 pin to receive interrupts when a new packet is received (as it is done
     * in the default config), you should not have to call this method.
     * @return a CC1101Packet read from the CC1101 chip
     */
    public CC1101Packet receiveData() {
        CC1101Packet packet = new CC1101Packet();

        if (getMarcState() == CC1101Constants.MARCSTATE_RXFIFO_OVERFLOW) {
            setIdleState();
            flushRxFifo();
            packet.length = -1;
        } else if (readStatusReg(CC1101Constants.CC1101_RXBYTES) != 0) {
            packet.length = readConfigReg(CC1101Constants.CC1101_RXFIFO);
            if (packet.length > CC1101_DATA_LEN) {
                packet.length = -2;
            } else {
                int length = packet.getLength();
                if (length > 0) {
                    packet.data = readBurstReg(CC1101Constants.CC1101_RXFIFO, length);
                    packet.rssi = readStatusReg(CC1101Constants.CC1101_RSSI);
                    //packet.rssi = readConfigReg(CC1101Constants.CC1101_RXFIFO);
                    //byte val = readConfigReg(CC1101Constants.CC1101_RXFIFO);
                    byte lqi = readStatusReg(CC1101Constants.CC1101_LQI);
                    //packet.lqi = (byte) (lqi & (byte) 0x7F);
                    packet.lqi = BitUtils.bitsRead(lqi, 0, 6);
                    packet.crcOk = BitUtils.bitsRead(lqi, 7, 7) == 1;
                } else {
                    if (BuildConfig.DEBUG) Log.e(TAG, "Packet length is " + BitUtils.getHexValue(packet.length) + ", nothing to read");
                }
            }
        } else {
            packet.length = 0;
        }

        setRxState();

        return packet;
    }


    private void spiWrite(byte[] buffer, byte registerAddress) {
        if (mSPIDevice == null) {
            if (BuildConfig.DEBUG) Log.e(TAG, "SPI device is null, could not write");
        }
        byte[] dataToWrite = new byte[buffer.length + 1];
        dataToWrite[0] = registerAddress;
        for (int i = 1; i < dataToWrite.length; i++) {
            dataToWrite[i] = buffer[i-1];
        }
        try {
            mSPIDevice.write(dataToWrite, dataToWrite.length);
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while reading");
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the SPI device and GPIO connections. Should be called when you don't need to use
     * the CC1101 anymore.
     */
    public void close() {
        if (mSPIDevice != null) {
            try {
                mSPIDevice.close();
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Exception while closing " + mSPIDeviceName);
                ioe.printStackTrace();
            }
            mSPIDevice = null;
        }

        if (mGDO0 != null) {
            try {
                mGDO0.close();
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Exception while closing " + mGDO0Name);
                ioe.printStackTrace();
            }
        }

        if (mSS != null) {
            try {
                mSS.close();
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Exception while closing " + mSSName);
                ioe.printStackTrace();
            }
        }
    }


    public interface PacketListener {
        void onNewPacket(CC1101Packet packet);
    }
}
