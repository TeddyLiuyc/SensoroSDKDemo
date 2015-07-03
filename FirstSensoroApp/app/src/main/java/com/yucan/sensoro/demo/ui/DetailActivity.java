package com.yucan.sensoro.demo.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Script;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.SensoroBeaconConnection;
import com.yucan.sensoro.demo.ConnectionApp;
import com.yucan.sensoro.demo.R;
import com.yucan.sensoro.demo.db.DrawView;

import org.apache.http.MethodNotSupportedException;


public class DetailActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ConnectionApp connection;

    Button setButton;
    Beacon selectedBeacon;
    ActionBar actionBar;
    Intent intentBack;

    // ui textview
    TextView snView;
    TextView majView;
    TextView minView;
    TextView uuidView;
    TextView accView;
    TextView lightView;
    TextView tempView;
    LinearLayout layout;
    int xPixel;
    int yPixel;

    ProgressDialog  progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        xPixel = dm.widthPixels;
        yPixel = dm.heightPixels;

        System.out.println(xPixel);
        System.out.println(yPixel);

        //get the map from the MainActivity
        selectedBeacon = getIntent().getParcelableExtra("Beacon");
        //get the application
        connection = (ConnectionApp) getApplication();

        //ActionBar button
        initActionBar();

        //initial values and set them
        init();
        setDis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        CreateMenu(menu);
        return true;
    }

    void goSetting() {
        Intent intent = new Intent();
        intent.setClass(this, ModifyActivity.class);
        intent.putExtra(ConnectionApp.EXTRA_BEACONS, selectedBeacon);

        startActivityForResult(intent, 0);
    }

    //
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:

                setDis();

                break;
            default:
                break;
        }
    }

    // initial all values
    void init() {
        snView = (TextView) findViewById(R.id.sn_info);
        majView = (TextView) findViewById(R.id.major_info);
        minView = (TextView) findViewById(R.id.minor_info);
        uuidView = (TextView) findViewById(R.id.uuid_info);
        accView = (TextView) findViewById(R.id.acc_info);
        //lightView = (TextView) findViewById(R.id.light_info);
        tempView = (TextView) findViewById(R.id.temp_info);
        layout = (LinearLayout) findViewById(R.id.thermometer);
    }

    // set values
    void setDis() {
        snView.setText(selectedBeacon.getSerialNumber());
        majView.setText(String.format("0x" + "%04X", selectedBeacon.getMajor()));
        minView.setText(String.format("0x" + "%04X", selectedBeacon.getMinor()));
        uuidView.setText(selectedBeacon.getProximityUUID());
        accView.setText(String.format("%04d", selectedBeacon.getAccelerometerCount()));
        //lightView.setText(String.format("%04X", selectedBeacon.getLight()));
        tempView.setText(String.format("%04d", selectedBeacon.getTemperature()));

        if (selectedBeacon.getTemperature()!=null){
            setTemperature();
        }

    }

    void setTemperature() {
        final DrawView view = new DrawView(this,(int)(xPixel*1.5), yPixel);
        layout.addView(view);
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = -10; i <= selectedBeacon.getTemperature()+3; i++) {
                    try {
                        Thread.sleep(80);
                        view.setTemp(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = selectedBeacon.getTemperature()+3; i >= selectedBeacon.getTemperature()-2; i--) {
                    try {
                        Thread.sleep(90);
                        view.setTemp(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = selectedBeacon.getTemperature()-2; i <= selectedBeacon.getTemperature(); i++) {
                    try {
                        Thread.sleep(100);
                        view.setTemp(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        myThread.start();
    }

    // show the back button in the ActionBar
    void initActionBar() {
        //display the back button in the ActionBar
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // back to MainActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //back to last activity
        switch (item.getItemId()) {
            case android.R.id.home:
                intentBack = new Intent();
                intentBack.setClass(this, DetailActivity.class);
                intentBack.putExtra(ConnectionApp.EXTRA_BEACONS, selectedBeacon);

                setResult(RESULT_OK, intentBack);
                finish();
            default:
                return menuChoice(item);
        }
    }

    class MyConnectionCallback implements SensoroBeaconConnection.BeaconConnectionCallback {

        @Override
        public void onConnectedState(Beacon beacon, int i, int i1) {
            //progressView bar
            if (i == Beacon.CONNECTED && i1 == SensoroBeaconConnection.SUCCESS) {
                progressDialog.dismiss();
                //setting button
                goSetting();
            } else if (!connection.getConnection().isConnected()) {
                Log.i(TAG, "DISCONNECT");
            }


        }

        @Override
        public void onWritePassword(Beacon beacon, int i) {

        }

        @Override
        public void onDisablePassword(Beacon beacon, int i) {

        }

        @Override
        public void onRequireWritePermission(Beacon beacon, int i) {

        }

        @Override
        public void onWriteBaseSetting(Beacon beacon, int i) {

        }

        @Override
        public void onWirteSensorSetting(Beacon beacon, int i) {

        }

        @Override
        public void onWriteMajorMinor(Beacon beacon, int i) {

        }

        @Override
        public void onWriteProximityUUID(Beacon beacon, int i) {

        }

        @Override
        public void onWriteSecureBroadcastInterval(Beacon beacon, int i) {

        }

        @Override
        public void onEnableIBeacon(Beacon beacon, int i) {

        }

        @Override
        public void onDisableIBeacon(Beacon beacon, int i) {

        }

        @Override
        public void onWriteBroadcastKey(Beacon beacon, int i) {

        }

        @Override
        public void onResetToFactorySettings(Beacon beacon, int i) {

        }

        @Override
        public void onResetAcceleratorCount(Beacon beacon, int i) {

        }

        @Override
        public void onReloadSensorData(Beacon beacon, int i) {

        }

        @Override
        public void onUpdateTemperatureData(Beacon beacon, Integer integer) {


        }

        @Override
        public void onUpdateLightData(Beacon beacon, Double aDouble) {

        }

        @Override
        public void onUpdateMovingState(Beacon beacon, Beacon.MovingState movingState) {

        }

        @Override
        public void onUpdateAccelerometerCount(Beacon beacon, int i) {

        }

        @Override
        public void onFlashLightWitCommand(Beacon beacon, int i) {

        }
    }

    void CreateMenu(Menu menu){
        MenuItem menu1 = menu.add(0,0,0,"Item 1");
        {
            menu1.setTitle("设置");
            menu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    boolean menuChoice(MenuItem item){
        switch (item.getItemId()){
            case 0:
                try {
                    MyConnectionCallback callback = new MyConnectionCallback();
                    connection.setConnection(new SensoroBeaconConnection(DetailActivity.this, selectedBeacon,
                            callback));
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setTitle("提示");
                    progressDialog.setMessage("正在连接中");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    connection.getConnection().connect();
                } catch (SensoroBeaconConnection.SensoroException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return false;
    }

}
