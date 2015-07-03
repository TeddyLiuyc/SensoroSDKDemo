package com.yucan.sensoro.demo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;
import com.yucan.sensoro.demo.ConnectionApp;
import com.yucan.sensoro.demo.R;


import java.util.ArrayList;


public class MainActivity extends Activity {


    private static final String TAG = MainActivity.class.getSimpleName();

    SensoroManager sensoroManager = null;
    Beacon selectedBeacon; // selectedBeacon beacon
    ListView list; // list view
    ArrayList<Beacon> allBeacon = null; // all beacons
    MyAdapter adapter; // Base Adapter
    LayoutInflater inflate;
    Typeface font;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initList();

        initIBeacon();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // initial beacons. when find a new beacon, add it into allBeacon.
    void initIBeacon() {
        sensoroManager = SensoroManager.getInstance(this);
        /**
         * check bluetooth.
         **/
        if (sensoroManager.isBluetoothEnabled()) {

            /**
             * turn on SDK service
             **/
            try {

                BeaconManagerListener beaconManagerListener = new BeaconManagerListener() {

                    @Override
                    public void onUpdateBeacon(ArrayList<Beacon> beacons) {

                    }

                    // when find a new beacon
                    @Override
                    public void onNewBeacon(final Beacon beacon) {
                        // when find a new device
                        if (!allBeacon.contains(beacon)) {
                            // ?
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Log.i(TAG, beacon.toString());

                                    //add the beacon into list when detected.
                                    allBeacon.add(beacon);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                    @Override
                    public void onGoneBeacon(Beacon beacon) {
                        // 一个传感器消失
                    }
                };
                sensoroManager.setBeaconManagerListener(beaconManagerListener);

                sensoroManager.startService();
            } catch (Exception e) {
                e.printStackTrace(); // catch errors
            }

        }
    }

    // initial the list
    void initList() {
        inflate = LayoutInflater.from(this);
        list = (ListView) findViewById(R.id.mylist);
        allBeacon = new ArrayList<Beacon>();
        adapter = new MyAdapter();

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBeacon = allBeacon.get(position);
                //System.out.println(selectedBeacon.getSerialNumber());

                toDetail();
            }
        });

    }

    // go to DetailActivity
    void toDetail() {
        Intent intent = new Intent();

        intent.setClass(this, DetailActivity.class);
        intent.putExtra(ConnectionApp.EXTRA_BEACONS, selectedBeacon);
        startActivityForResult(intent, 1);
    }

    // when the result back to this activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                initList();
                initIBeacon();

                break;
            default:
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (allBeacon != null) {
                return allBeacon.size();
            } else {
                return 0;
            }

        }

        @Override
        public Object getItem(int position) {
            return allBeacon.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView snView;
            TextView majorView;
            TextView minorView;
            TextView uuidView;
            //ImageView batteryView;
            TextView t1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Beacon beacon = allBeacon.get(position);
            ViewHolder v;
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.simple_item, null);
                v = new ViewHolder();
                font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
                v.snView = (TextView) convertView.findViewById(R.id.sn);
                v.majorView = (TextView) convertView.findViewById(R.id.major);
                v.minorView = (TextView) convertView.findViewById(R.id.minor);
                v.uuidView = (TextView) convertView.findViewById(R.id.uuid);
                //v.batteryView = (ImageView) convertView.findViewById(R.id.battery_status);
                v.t1 = (TextView) convertView.findViewById(R.id.right_arrow);
                v.t1.setTypeface(font);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.snView.setText(beacon.getSerialNumber());
            v.majorView.setText(String.format("%04X", beacon.getMajor()));
            v.minorView.setText(String.format("%04X", beacon.getMinor()));
            v.uuidView.setText(beacon.getProximityUUID());

            //v.batteryView.getDrawable().setLevel(100);

            return convertView;
        }
    }
}