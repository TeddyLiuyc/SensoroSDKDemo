package com.yucan.sensoro.demo.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.beacon.kit.Beacon;
import com.yucan.sensoro.demo.ConnectionApp;
import com.yucan.sensoro.demo.R;


public class ModifyActivity extends Activity {

    Button saveButton;// save button
    Beacon selectedBeacon;// selected beacon
    ActionBar actionBar;// action bar
    TextView uuidView;// view of uuid
    TextView majorView;// view of major
    TextView minorView;// view of minor
    ConnectionApp connection; // connection App
    Intent intent;

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        connection = (ConnectionApp) getApplication();

        //set initActionBar
        initActionBar();

        //get the Beacon
        selectedBeacon = getIntent().getParcelableExtra("Beacon");

        setDis();

        btnCtrl();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify, menu);
        return true;
    }


    void btnCtrl() {
        saveButton = (Button) findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
            }
        });
    }

    // set hints to display
    void setDis() {
        //set all those values in display
        uuidView = (TextView) findViewById(R.id.uuid_dis);
        uuidView.setHint(selectedBeacon.getProximityUUID());
        uuidView.setHintTextColor(Color.GRAY);

        majorView = (TextView) findViewById(R.id.major_dis);
        majorView.setHint(String.format("%05d", selectedBeacon.getMajor()));
        majorView.setHintTextColor(Color.GRAY);

        minorView = (TextView) findViewById(R.id.minor_dis);
        minorView.setHint(String.format("%05d", selectedBeacon.getMinor()));
        minorView.setHintTextColor(Color.GRAY);

    }

    // get the input info and set to beacon, then back to DetailActivity
    void saveInfo() {
        if (connection.getConnection().isConnected()) {
            Log.i(TAG, "connected");
        } else {
            Log.i(TAG, "not connected");
        }


        if (!uuidView.getText().toString().equals("")) {// uuid is changed
            connection.getConnection().writeProximityUUID(uuidView.getText().toString());
        }
        if (!majorView.getText().toString().equals("") && !minorView.getText().toString().equals("")) {// major and minor is changed
            connection.getConnection().writeMajorMinor(Integer.valueOf(majorView.getText().toString()),
                    Integer.valueOf(minorView.getText().toString()));
        } else if (!majorView.getText().toString().equals("") && minorView.getText().toString().equals("")) {// only major is changed
            connection.getConnection().writeMajorMinor(Integer.valueOf(majorView.getText().toString()),
                    selectedBeacon.getMinor());
        } else if (majorView.getText().toString().equals("") && !minorView.getText().toString().equals("")) {// only minor is changed
            connection.getConnection().writeMajorMinor(selectedBeacon.getMajor(),
                    Integer.valueOf(minorView.getText().toString()));
        }

        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).
                show(); // show success info

        Log.i(TAG, selectedBeacon.toString());
        intent = new Intent();
        intent.setClass(this, DetailActivity.class);


        setResult(RESULT_OK, intent);
        finish();
    }

    // show the back button in the Action Bar
    void initActionBar() {
        //display the back button in the ActionBar
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // back to DetailActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //back to last activity
        switch (item.getItemId()) {
            case android.R.id.home:
                dialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ModifyActivity.this);
        builder.setTitle("确定要离开吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
