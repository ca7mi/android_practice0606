package com.android_practice0606.ca7mi.android_practice0606_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import twitter4j.EntitySupport;
import twitter4j.ExtendedMediaEntity;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.SymbolEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;

public class ResultActivity extends AppCompatActivity {

 //   MainActivity mainActivity = new MainActivity();
    TextView textView;
    LinearLayout linearLayout;
    TwitterFactory twitterFactory = null;
    Twitter twitter = null;
    private ConfigurationBuilder cb = new ConfigurationBuilder();
    public EntitySupport es;
    ArrayList<String> imageURLList = new ArrayList<String>();

    //取得件数
    static final int TWEET_NUM = 30;

    //保存対象の画像拡張子
    static final String TARGET_EXTENSION = ".jpg" ;


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
                        query.setCount(TWEET_NUM);

                        // 検索実行
                        QueryResult result = twitter.search(query);

                        System.out.println("ヒット数 : " + result.getTweets().size());

                        // 検索結果を見てみる
                        for (twitter4j.Status status : result.getTweets()) {

                            createEntitiySupport();

                            MediaEntity[] arrMedia = status.getMediaEntities();
                            for(MediaEntity media : arrMedia){
                                Log.d("debug", "madiaURL : " + media.getMediaURL());
                                String filePath =  downloadImage(media.getMediaURL());
                                imageURLList.add(filePath);
                            }

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

                for(String imageUrl : imageURLList){

                    ImageView image = new ImageView(getApplicationContext());

                    try {
                        FileInputStream fileInputStream = openFileInput(imageUrl);
                        Bitmap bm = BitmapFactory.decodeStream(fileInputStream);
                        image.setImageBitmap(bm);
                        linearLayout.addView(image);
                        Log.d("debug", "imageUrlList" + imageUrl);
                    } catch (FileNotFoundException e) {
                        Log.d("debug", "FileNotFoundException imageUrl" ,e);
                    }
                }

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

    private String downloadImage(String strUrl){
        try {

            URL url = new URL(strUrl);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.connect();

            int httpStatusCode = conn.getResponseCode();

            if(httpStatusCode != HttpURLConnection.HTTP_OK){
                throw new Exception();
            }

            String[] splitUrl = strUrl.split("/");
            String inputPath = splitUrl[splitUrl.length-1];

            // Input Stream
            DataInputStream dataInStream
                    = new DataInputStream(
                    conn.getInputStream());

            // Output Stream
            DataOutputStream dataOutStream
                    = new DataOutputStream(
                    new BufferedOutputStream(
                            openFileOutput(inputPath, Context.MODE_PRIVATE)));

            // Read Data
            byte[] b = new byte[4096];
            int readByte = 0;

            while(-1 != (readByte = dataInStream.read(b))){
                dataOutStream.write(b, 0, readByte);
            }

            // Close Stream
            dataInStream.close();
            dataOutStream.close();

            return inputPath;

        } catch (FileNotFoundException e) {
            Log.d("debug", "FileNotFoundException" ,e);
        } catch (ProtocolException e) {
            Log.d("debug", "ProtocolException" ,e);
        } catch (MalformedURLException e) {
            Log.d("debug", "MalformedURLException" ,e);
        } catch (IOException e) {
            Log.d("debug", "IOException" ,e);
        } catch (Exception e) {
            Log.d("debug", "Exception" ,e);
        }

        return null;
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
