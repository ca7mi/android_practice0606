package com.android_practice0606.ca7mi.android_practice0606_2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = this.getIntent();
        String text = intent.getStringExtra("searchText");
        TextView textView = (TextView)this.findViewById(R.id.resultText);
        textView.setText(text);
    }

    private void showTweetList(){

        StringBuilder stb = new StringBuilder();

        for (int i=0; i< mainActivity.tweetList.size(); i++ ){
            stb.append(mainActivity.tweetList.get(i));
        }

    }
}
