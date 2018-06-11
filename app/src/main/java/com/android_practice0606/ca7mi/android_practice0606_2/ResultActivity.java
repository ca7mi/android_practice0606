package com.android_practice0606.ca7mi.android_practice0606_2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class ResultActivity extends AppCompatActivity {

 //   MainActivity mainActivity = new MainActivity();
    TextView textView;
    LinearLayout linearLayout;
    TwitterFactory twitterFactory = null;
    Twitter twitter = null;
    private ConfigurationBuilder cb = new ConfigurationBuilder();
    public EntitySupport es;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textView = (TextView) this.findViewById(R.id.resultText);
        linearLayout = (LinearLayout) this.findViewById(R.id.tweet_list);

        Intent intent = this.getIntent();
        String text = intent.getStringExtra("searchText");
        textView.setText(text);
        try {
            searchForTwitter(text);
        } catch (TwitterException e) {
            Log.d("debug", "error twitter onCreate" + twitter, e);
        }
    }


    private void searchForTwitter(final String searchText) throws TwitterException {

        createAuth();
        AsyncTask<String, Void, ArrayList<String>> task = new AsyncTask<String, Void, ArrayList<String>>() {

            @Override
            protected ArrayList doInBackground(String... params) {
                //createAuth();
                ArrayList<String> tweetList = new ArrayList<String>();
                if (twitter != null) {
                    Log.d("debug", "search" + twitter);
                    try {
                        Query query = new Query();

                        // 検索ワードをセット
                        query.setQuery(searchText);

                        // 検索実行
                        QueryResult result = twitter.search(query);

                        System.out.println("ヒット数 : " + result.getTweets().size());

                        // 検索結果を見てみる
                        for (twitter4j.Status status : result.getTweets()) {

                            TextView tweet = new TextView(getApplicationContext());

                            createEntitiySupport();
                            System.out.println("画像取得できた？" + es.getURLEntities());

                            // 本文
                            tweetList.add(status.getText());

                            System.out.println("Tweet内容" + status.getText());
                            // 発言したユーザ
                            System.out.println("Tweetした人" + status.getUser());
                            // 発言した日時
                            System.out.println("Tweetした日" + status.getCreatedAt());
                        }
                    } catch (TwitterException e) {
                        Log.d("debug", "error twitter" + twitter, e);
                    }
                } else {
                    Log.d("debug", "twitter null" + twitter);
                }
                return tweetList;
            }

            protected void onPostExecute(ArrayList<String> tweetList) {

                for(String tweetText : tweetList){
                    TextView tweet = new TextView(getApplicationContext());
                    tweet.setText(tweetText);
                    linearLayout.addView(tweet);
                    Log.d("debug", "tweetList" + tweetText);
                }
            }

        };
        task.execute();
    }

    private void createAuth() {
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(getString(R.string.consumer_key))
                .setOAuthConsumerSecret(getString(R.string.consumer_secret))
                .setOAuthAccessToken(getString(R.string.access_token))
                .setOAuthAccessTokenSecret(getString(R.string.access_token_secret));

        twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
    }

    private void createEntitiySupport() {
        // Twitterの画像を取得
        es = new EntitySupport() {
            @Override
            public UserMentionEntity[] getUserMentionEntities() {
                return new UserMentionEntity[0];
            }

            @Override
            public URLEntity[] getURLEntities() {
                return new URLEntity[0];
            }

            @Override
            public HashtagEntity[] getHashtagEntities() {
                return new HashtagEntity[0];
            }

            @Override
            public MediaEntity[] getMediaEntities() {
                return new MediaEntity[0];
            }

            @Override
            public ExtendedMediaEntity[] getExtendedMediaEntities() {
                return new ExtendedMediaEntity[0];
            }

            @Override
            public SymbolEntity[] getSymbolEntities() {
                return new SymbolEntity[0];
            }
        };
    }

}
