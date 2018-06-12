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

import java.io.FileNotFoundException;
import java.util.ArrayList;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ResultActivity extends AppCompatActivity {

 //   MainActivity mainActivity = new MainActivity();
    TextView textView;
    LinearLayout linearLayout;
    TwitterFactory twitterFactory = null;
    Twitter twitter = null;
    private ConfigurationBuilder cb = new ConfigurationBuilder();
    public EntitySupport es;

    //取得件数
    static final int TWEET_NUM = 20;

    //保存対象の画像拡張子
    static final String TARGET_EXTENSION = ".jpg";


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
                      //  query.setCount(TWEET_NUM);

                        // 検索実行
                        QueryResult result = twitter.search(query);

                        System.out.println("ヒット数 : " + result.getTweets().size());

                        // 検索結果を見てみる
                        for (twitter4j.Status status : result.getTweets()) {

                            createEntitiySupport();

                            TextView tweet = new TextView(getApplicationContext());

                        /*    MediaEntity[] arrMedia = status.getMediaEntities();
                            for(MediaEntity media : arrMedia){
                                if(media.getMediaURL().endsWith(TARGET_EXTENSION)) {
                                    URL website = new URL(media.getMediaURL());
                                    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                                    //保存ファイル名にStatusが持つ作成日を付与
                                    DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                                    FileOutputStream fos =
                                            new FileOutputStream("ImageFromTwitter" + df.format(status.getCreatedAt()) + TARGET_EXTENSION);
                                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                }
                            }*/

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
                   /* } catch (FileNotFoundException e) {
                        Log.d("debug", "FileNotFoundException", e);
                    } catch (MalformedURLException e) {
                        Log.d("debug", "MalformedURLException", e);
                    } catch (IOException e) {
                        Log.d("debug", "IOException", e); */
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
