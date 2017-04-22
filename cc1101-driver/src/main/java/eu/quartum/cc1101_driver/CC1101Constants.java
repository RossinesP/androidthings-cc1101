package eu.quartum.cc1101_driver;

/**
 * Created by pierre on 17/03/17.
 */

public class CC1101Constants {
    /**
     * Frequency channels
     */
    public final static byte NUMBER_OF_FCHANNELS = (byte)10;

    /**
     * Type of transfers
     */
    public final static byte WRITE_BURST = (byte)0x40;
    public final static byte READ_SINGLE = (byte)0x80;
    public final static byte READ_BURST = (byte)0xC0;

    /**
     * Type of register
     */
    public final static byte CC1101_CONFIG_REGISTER = READ_SINGLE;
    public final static byte CC1101_STATUS_REGISTER = READ_BURST;

    /**
     * PATABLE & FIFO's
     */
    public final static byte CC1101_PATABLE = (byte)0x3E;       // PATABLE address
    public final static byte CC1101_TXFIFO = (byte)0x3F;       // TX FIFO address
    public final static byte CC1101_RXFIFO = (byte)0x3F;       // RX FIFO address

    /**
     * Command strobes
     */
    public final static byte CC1101_SRES = (byte)0x30;        // Reset CC1101 chip
    public final static byte CC1101_SFSTXON = (byte)0x31;        // Enable and calibrate frequency synthesizer (if MCSM0.FS_AUTOCAL=1). If in RX (with CCA):
    // Go to a wait state where only the synthesizer is running (for quick RX / TX turnaround).
    public final static byte CC1101_SXOFF = (byte)0x32;        // Turn off crystal oscillator
    public final static byte CC1101_SCAL = (byte)0x33;        // Calibrate frequency synthesizer and turn it off. SCAL can be strobed from IDLE mode without
    // setting manual calibration mode (MCSM0.FS_AUTOCAL=0)
    public final static byte CC1101_SRX = (byte)0x34;        // Enable RX. Perform calibration first if coming from IDLE and MCSM0.FS_AUTOCAL=1
    public final static byte CC1101_STX = (byte)0x35;        // In IDLE state: Enable TX. Perform calibration first if MCSM0.FS_AUTOCAL=1.
    // If in RX state and CCA is enabled: Only go to TX if channel is clear
    public final static byte CC1101_SIDLE = (byte)0x36;       // Exit RX / TX, turn off frequency synthesizer and exit Wake-On-Radio mode if applicable
    public final static byte CC1101_SWOR = (byte)0x38;        // Start automatic RX polling sequence (Wake-on-Radio) as described in Section 19.5 if
    // WORCTRL.RC_PD=0
    public final static byte CC1101_SPWD = (byte)0x39;        // Enter power down mode when CSn goes high
    public final static byte CC1101_SFRX = (byte)0x3A;       // Flush the RX FIFO buffer. Only issue SFRX in IDLE or RXFIFO_OVERFLOW states
    public final static byte CC1101_SFTX = (byte)0x3B;       // Flush the TX FIFO buffer. Only issue SFTX in IDLE or TXFIFO_UNDERFLOW states
    public final static byte CC1101_SWORRST = (byte)0x3C;       // Reset real time clock to Event1 value
    public final static byte CC1101_SNOP = (byte)0x3D;       // No operation. May be used to get access to the chip status byte

    /**
     * CC1101 configuration registers
     */
    public final static int CC1101_COMPLETE_REGISTERS = 47;
    public final static byte CC1101_IOCFG2 = (byte)0x00;       // GDO2 Output Pin Configuration
    public final static byte CC1101_IOCFG1 = (byte)0x01;       // GDO1 Output Pin Configuration
    public final static byte CC1101_IOCFG0 = (byte)0x02;       // GDO0 Output Pin Configuration
    public final static byte CC1101_FIFOTHR = (byte)0x03;       // RX FIFO and TX FIFO Thresholds
    public final static byte CC1101_SYNC1 = (byte)0x04;       // Sync Word, High Byte
    public final static byte CC1101_SYNC0 = (byte)0x05;       // Sync Word, Low Byte
    public final static byte CC1101_PKTLEN = (byte)0x06;       // Packet Length
    public final static byte CC1101_PKTCTRL1 = (byte)0x07;       // Packet Automation Control
    public final static byte CC1101_PKTCTRL0 = (byte)0x08;       // Packet Automation Control
    public final static byte CC1101_ADDR = (byte)0x09;       // Device Address
    public final static byte CC1101_CHANNR = (byte)0x0A;       // Channel Number
    public final static byte CC1101_FSCTRL1 = (byte)0x0B;       // Frequency Synthesizer Control
    public final static byte CC1101_FSCTRL0 = (byte)0x0C;       // Frequency Synthesizer Control
    public final static byte CC1101_FREQ2 = (byte)0x0D;       // Frequency Control Word, High Byte
    public final static byte CC1101_FREQ1 = (byte)0x0E;       // Frequency Control Word, Middle Byte
    public final static byte CC1101_FREQ0 = (byte)0x0F;       // Frequency Control Word, Low Byte
    public final static byte CC1101_MDMCFG4 = (byte)0x10;       // Modem Configuration
    public final static byte CC1101_MDMCFG3 = (byte)0x11;       // Modem Configuration
    public final static byte CC1101_MDMCFG2 = (byte)0x12;       // Modem Configuration
    public final static byte CC1101_MDMCFG1 = (byte)0x13;       // Modem Configuration
    public final static byte CC1101_MDMCFG0 = (byte)0x14;       // Modem Configuration
    public final static byte CC1101_DEVIATN = (byte)0x15;       // Modem Deviation Setting
    public final static byte CC1101_MCSM2 = (byte)0x16;       // Main Radio Control State Machine Configuration
    public final static byte CC1101_MCSM1 = (byte)0x17;       // Main Radio Control State Machine Configuration
    public final static byte CC1101_MCSM0 = (byte)0x18;       // Main Radio Control State Machine Configuration
    public final static byte CC1101_FOCCFG = (byte)0x19;       // Frequency Offset Compensation Configuration
    public final static byte CC1101_BSCFG = (byte)0x1A;       // Bit Synchronization Configuration
    public final static byte CC1101_AGCCTRL2 = (byte)0x1B;       // AGC Control
    public final static byte CC1101_AGCCTRL1 = (byte)0x1C;       // AGC Control
    public final static byte CC1101_AGCCTRL0 = (byte)0x1D;       // AGC Control
    public final static byte CC1101_WOREVT1 = (byte)0x1E;       // High Byte Event0 Timeout
    public final static byte CC1101_WOREVT0 = (byte)0x1F;       // Low Byte Event0 Timeout
    public final static byte CC1101_WORCTRL = (byte)0x20;       // Wake On Radio Control
    public final static byte CC1101_FREND1 = (byte)0x21;       // Front End RX Configuration
    public final static byte CC1101_FREND0 = (byte)0x22;       // Front End TX Configuration
    public final static byte CC1101_FSCAL3 = (byte)0x23;       // Frequency Synthesizer Calibration
    public final static byte CC1101_FSCAL2 = (byte)0x24;       // Frequency Synthesizer Calibration
    public final static byte CC1101_FSCAL1 = (byte)0x25;       // Frequency Synthesizer Calibration
    public final static byte CC1101_FSCAL0 = (byte)0x26;       // Frequency Synthesizer Calibration
    public final static byte CC1101_RCCTRL1 = (byte)0x27;       // RC Oscillator Configuration
    public final static byte CC1101_RCCTRL0 = (byte)0x28;       // RC Oscillator Configuration
    public final static byte CC1101_FSTEST = (byte)0x29;       // Frequency Synthesizer Calibration Control
    public final static byte CC1101_PTEST = (byte)0x2A;       // Production Test
    public final static byte CC1101_AGCTEST = (byte)0x2B;       // AGC Test
    public final static byte CC1101_TEST2 = (byte)0x2C;       // Various Test Settings
    public final static byte CC1101_TEST1 = (byte)0x2D;       // Various Test Settings
    public final static byte CC1101_TEST0 = (byte)0x2E;       // Various Test Settings

    /**
     * Status registers
     */
    public final static byte CC1101_PARTNUM = (byte)0x30;        // Chip ID
    public final static byte CC1101_VERSION = (byte)0x31;        // Chip ID
    public final static byte CC1101_FREQEST = (byte)0x32;        // Frequency Offset Estimate from Demodulator
    public final static byte CC1101_LQI = (byte)0x33;        // Demodulator Estimate for Link Quality
    public final static byte CC1101_RSSI = (byte)0x34;        // Received Signal Strength Indication
    public final static byte CC1101_MARCSTATE = (byte)0x35;        // Main Radio Control State Machine State
    public final static byte CC1101_WORTIME1 = (byte)0x36;        // High Byte of WOR Time
    public final static byte CC1101_WORTIME0 = (byte)0x37;        // Low Byte of WOR Time
    public final static byte CC1101_PKTSTATUS = (byte)0x38;        // Current GDOx Status and Packet Status
    public final static byte CC1101_VCO_VC_DAC = (byte)0x39;        // Current Setting from PLL Calibration Module
    public final static byte CC1101_TXBYTES = (byte)0x3A;        // Underflow and Number of Bytes
    public final static byte CC1101_RXBYTES = (byte)0x3B;        // Overflow and Number of Bytes
    public final static byte CC1101_RCCTRL1_STATUS = (byte)0x3C;        // Last RC Oscillator Calibration Result
    public final static byte CC1101_RCCTRL0_STATUS = (byte)0x3D;        // Last RC Oscillator Calibration Result


    /**
     MARCSTATES
     */
    public final static byte MARCSTATE_SLEEP = (byte) 0x00;
    public final static byte MARCSTATE_IDLE = (byte) 0x01;
    public final static byte MARCSTATE_XOFF = (byte) 0x02;
    public final static byte MARCSTATE_VCOON_MC = (byte) 0x03;
    public final static byte MARCSTATE_REGON_MC = (byte) 0x04;
    public final static byte MARCSTATE_MANCAL = (byte) 0x05;
    public final static byte MARCSTATE_VCOON = (byte) 0x06;
    public final static byte MARCSTATE_REGON = (byte) 0x07;
    public final static byte MARCSTATE_STARTCAL = (byte) 0x08;
    public final static byte MARCSTATE_BWBOOST = (byte) 0x09;
    public final static byte MARCSTATE_FS_LOCK = (byte) 0x0A;
    public final static byte MARCSTATE_IFADCON = (byte) 0x0B;
    public final static byte MARCSTATE_ENDCAL = (byte) 0x0C;
    public final static byte MARCSTATE_RX = (byte) 0x0D;
    public final static byte MARCSTATE_RX_END = (byte) 0x0E;
    public final static byte MARCSTATE_RX_RST = (byte) 0x0F;
    public final static byte MARCSTATE_TXRX_SWITCH = (byte) 0x10;
    public final static byte MARCSTATE_RXFIFO_OVERFLOW = (byte) 0x11;
    public final static byte MARCSTATE_FSTXON = (byte) 0x12;
    public final static byte MARCSTATE_TX = (byte) 0x13;
    public final static byte MARCSTATE_TX_END = (byte) 0x14;
    public final static byte MARCSTATE_RXTX_SWITCH = (byte) 0x15;
    public final static byte MARCSTATE_TXFIFO_UNDEFLOW = (byte) 0x16;
}
