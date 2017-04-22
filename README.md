# androidthings-cc1101
A TI CC1101 driver for Android Things, ported from several C++ drivers for arduino or raspberry pi, such as https://github.com/SpaceTeddy/CC1101 and the Panstamp library described here http://labalec.fr/erwan/?p=497

# About the CC1101 chip
The Texas Instrument CC1101 (or CC110L) chip is used for RF communication over the 315Mhz/433Mhz/868Mhz/915Mhz bands
The chip this code has been tested with can be bought from many resellers (Amazon, Alibaba, eBay) and looks like this :

![CC1101](http://images10.newegg.com/ProductImage/A35C_1_20131213370714277.jpg)

It is a cheap and effective way to do long range RF communication (between 100 to 300m).

You can find documentation about the chip here : http://www.ti.com/lit/ds/symlink/cc1101.pdf
You will probabably need this documentation to understand which parameters you need to properly use the CC1101.

# Limitations
This is mainly a work in progress.
As of now, the library is focused on RX mode (reception) and has only been tested in that mode. Depending on the registers you use to configure the chip, it might not even work properly. This is due to the fact that you can change which pin will get notified when a new packet is received.

# How to use it
First, connect the CC1101 to your Andoid Things hardware (this library has only been tested on the Raspberry Pi 3 running Android Things DP3).

The pinout is the following :

![CC1101 Pinout](http://labalec.fr/erwan/wp-content/uploads/2013/09/spi.png)

Connect the Vcc to a 3.3V voltage source, and the GND pin to the ground. Then connect the SI pin to your platforms MOSI pin, the SO to the MISO pin, the SCK to the CLK pin.
The CSn port must not be connected to the CE port of your platform. This is due to a limitation of the Android Things DP3 which prevents us from controlling the select/deselect sequence precisely, which is needed to initialize the CC1101 chip.
Connect the CSn pin to a standart GPIO pin or your choice.
Connect the GDO0 pin to another GPIO pin.

Then, include the cc1101-driver library from this project into your app. It is not yet available in jcenter or mavencentral, but that might come at some point.

Implement the PacketListener interface (for example have your Activity implement this interface)
Finally, create a CC1101Manager instance, initialize it, add a packet listener and start the Rxmode :
’’’
@Override
protected void onStart() {
    super.onStart();
    /*
      In the following line, SPI0.1 is the name of the SPI device, BCM5 is the name of the GDO0 Gpio pin
      and BCM23 is the name of the CSn Gpio pin
    */
    mManager = new CC1101Manager("SPI0.1", "BCM5", "BCM23", CC1101Config.GFSK_1_2_kb);
    mManager.setup();
    mManager.setPacketListener(this);
    mManager.setRxState();
}
’’’

Don't forger to unregister the manager when you don't need it anymore :
’’’
@Override
protected void onStop() {
    super.onStop();
    mManager.setPacketListener(null);
    mManager.close();
}
’’’

The ’onNewPacket’ method should then be called when a new packet is received.

Note that you can change the CC1101 register values to change any of the parameters used to operate the chip. The complete set of registers and their accepted values is in the CC1101 datasheet.
