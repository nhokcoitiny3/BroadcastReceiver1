package com.example.nhokc.project3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.example.nhokc.project3.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IOnRecyclerViewItemLongClickListener,PrefixRvAdapter.IPrefix,TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {
    private ActivityMainBinding binding;
    private PrefixRvAdapter adapter = new PrefixRvAdapter();
    private List<String> prefixList = new ArrayList<>();
    private MyDatabaseHelper myDatabaseHelper;
    private TextToSpeech tts = null;
    private String msg = "";
    private String mDefaultSmsApp;
    private static final int DEF_SMS_REQ = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        requestSmsPermission();
        requestSmsDefaultPermission();
        Intent startingIntent = this.getIntent();
        if (startingIntent.getStringExtra("KEY") != null) {
            msg = "This is spam, has been deleted";
            tts = new TextToSpeech(this, this);
        }
        initialize();
    }
    private void initialize() {
        myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());
        prefixList = myDatabaseHelper.getAllPrefix();
        initializeRv();
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EditActivity.class));
                finish();
            }
        });
    }


    private void initializeRv() {
        adapter.setData(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvPrefix.setLayoutManager(linearLayoutManager);
        binding.rvPrefix.setAdapter(adapter);
        adapter.setOnItemClickListener(new IOnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClicked(int position, int id) {

            }
        });
    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestSmsDefaultPermission() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDefaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);

            if (!getPackageName().equals(mDefaultSmsApp)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                startActivityForResult(intent, DEF_SMS_REQ);
            }
        }
    }

    @Override
    public int getCount() {
        return prefixList.size();
    }

    @Override
    public String getPrefix(int position) {
        return prefixList.get(position);
    }

    @Override
    public void onInit(int i) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
        msg ="";

    }

    @Override
    public void onUtteranceCompleted(String s) {
        tts.shutdown();
        tts = null;
        finish();
    }

    @Override
    public void onRecyclerViewItemLongClicked(int position, int id) {

    }
}
