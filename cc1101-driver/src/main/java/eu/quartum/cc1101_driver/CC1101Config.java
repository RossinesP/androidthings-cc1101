package eu.quartum.cc1101_driver;

import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * Created by pierrerossines on 21/04/2017.
 */

public class CC1101Config {
    private byte[] config;

    public CC1101Config(byte[] config) {
        if (config == null || config.length < CC1101Constants.CC1101_COMPLETE_REGISTERS) {
            throw new InvalidParameterException("The configuration must contain the full set of "
                    + CC1101Constants.CC1101_COMPLETE_REGISTERS + " register values");
        }
        this.config = Arrays.copyOf(config, config.length);
    }

    public byte[] getConfig() {
        return Arrays.copyOf(config, config.length);
    }

    public static CC1101Config GFSK_1_2_kb = new CC1101Config(new byte[] {
            (byte) 0x29,  // IOCFG2        GDO2 Output Pin Configuration
            (byte) 0x2E,  // IOCFG1        GDO1 Output Pin Configuration
            (byte) 0x06,  // IOCFG0        GDO0 Output Pin Configuration
            (byte) 0x07,  // FIFOTHR       RX FIFO and TX FIFO Thresholds
            (byte) 0x57,  // SYNC1         Sync Word, High Byte
            (byte) 0x43,  // SYNC0         Sync Word, Low Byte
            (byte) 0x3E,  // PKTLEN        Packet Length
            (byte) 0x0E,  // PKTCTRL1      Packet Automation Control
            (byte) 0x45,  // PKTCTRL0      Packet Automation Control
            (byte) 0xFF,  // ADDR          Device Address
            (byte) 0x00,  // CHANNR        Channel Number
            (byte) 0x08,  // FSCTRL1       Frequency Synthesizer Control
            (byte) 0x00,  // FSCTRL0       Frequency Synthesizer Control
            (byte) 0x21,  // FREQ2         Frequency Control Word, High Byte
            (byte) 0x65,  // FREQ1         Frequency Control Word, Middle Byte
            (byte) 0x6A,  // FREQ0         Frequency Control Word, Low Byte
            (byte) 0xF5,  // MDMCFG4       Modem Configuration
            (byte) 0x83,  // MDMCFG3       Modem Configuration
            (byte) 0x13,  // MDMCFG2       Modem Configuration
            (byte) 0xA0,  // MDMCFG1       Modem Configuration
            (byte) 0xF8,  // MDMCFG0       Modem Configuration
            (byte) 0x15,  // DEVIATN       Modem Deviation Setting
            (byte) 0x07,  // MCSM2         Main Radio Control State Machine Configuration
            (byte) 0x0C,  // MCSM1         Main Radio Control State Machine Configuration
            (byte) 0x19,  // MCSM0         Main Radio Control State Machine Configuration
            (byte) 0x16,  // FOCCFG        Frequency Offset Compensation Configuration
            (byte) 0x6C,  // BSCFG         Bit Synchronization Configuration
            (byte) 0x03,  // AGCCTRL2      AGC Control
            (byte) 0x40,  // AGCCTRL1      AGC Control
            (byte) 0x91,  // AGCCTRL0      AGC Control
            (byte) 0x02,  // WOREVT1       High Byte Event0 Timeout
            (byte) 0x26,  // WOREVT0       Low Byte Event0 Timeout
            (byte) 0x09,  // WORCTRL       Wake On Radio Control
            (byte) 0x56,  // FREND1        Front End RX Configuration
            (byte) 0x17,  // FREND0        Front End TX Configuration
            (byte) 0xA9,  // FSCAL3        Frequency Synthesizer Calibration
            (byte) 0x0A,  // FSCAL2        Frequency Synthesizer Calibration
            (byte) 0x00,  // FSCAL1        Frequency Synthesizer Calibration
            (byte) 0x11,  // FSCAL0        Frequency Synthesizer Calibration
            (byte) 0x41,  // RCCTRL1       RC Oscillator Configuration
            (byte) 0x00,  // RCCTRL0       RC Oscillator Configuration
            (byte) 0x59,  // FSTEST        Frequency Synthesizer Calibration Control,
            (byte) 0x7F,  // PTEST         Production Test
            (byte) 0x3F,  // AGCTEST       AGC Test
            (byte) 0x81,  // TEST2         Various Test Settings
            (byte) 0x3F,  // TEST1         Various Test Settings
            (byte) 0x0B   // TEST0         Various Test Settings
    });
}
