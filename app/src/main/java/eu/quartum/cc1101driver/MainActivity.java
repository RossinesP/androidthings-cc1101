package eu.quartum.cc1101driver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import eu.quartum.cc1101_driver.CC1101Config;
import eu.quartum.cc1101_driver.CC1101Manager;
import eu.quartum.cc1101_driver.CC1101Packet;

public class MainActivity extends AppCompatActivity implements CC1101Manager.PacketListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private CC1101Manager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mManager = new CC1101Manager("SPI0.1", "BCM5", "BCM23", CC1101Config.GFSK_1_2_kb);
        mManager.setup();
        mManager.setPacketListener(this);
        mManager.setRxState();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onNewPacket(CC1101Packet packet) {
        Log.v(TAG, "new packet received ! ");
    }
}
