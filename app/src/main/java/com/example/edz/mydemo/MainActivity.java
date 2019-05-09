package com.example.edz.mydemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final int MSG_UPDATA=0x110;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int progress=progressbar.getProgress();
            progressbar.setProgress(++progress);
            if (progress>=100)
            {
                mHandler.removeMessages(MSG_UPDATA);
            }
            mHandler.sendEmptyMessageDelayed(MSG_UPDATA,100);
        }
    };
    private Progressbar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressbar=findViewById(R.id.progressbar);
        mHandler.sendEmptyMessage(MSG_UPDATA);
    }
}
