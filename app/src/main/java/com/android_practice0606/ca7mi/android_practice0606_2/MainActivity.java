package com.android_practice0606.ca7mi.android_practice0606_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends AppCompatActivity {

    private Uri m_uri;
    private Uri resultUri = null;
    private static final int REQUEST_CHOOSER = 1000;
    private ImageView imageView = null;
    TwitterFactory twitterFactory = null;
    Twitter twitter = null;
    private ConfigurationBuilder cb = new ConfigurationBuilder();

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final int REQUEST_OAUTH=0;

    private static long user_id=0L;
    private static String screen_name=null;
    private static String token=null;
    private static String token_secret=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_pocchama);

        final Button button_select = findViewById(R.id.button_select);
        button_select.setOnClickListener(buttonClick);

        final Button button_twitter = findViewById(R.id.button_twitter);
        button_twitter.setOnClickListener(buttonClick);

        final  Button button_search = findViewById(R.id.button_search);
        button_search.setOnClickListener(buttonClick);

    };

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_select:
                    Log.d("debug", "button_select, Perform action on click");
                    selectPicture();
                    break;

                case R.id.button_twitter:
                    Log.d("debug", "button_twitter, Perform action on click");
                    //postingCommentOnTwitter();
                    postingImageOnTwitter();
                    break;

                case R.id.button_search :
                    Log.d("debug", "button_search Tap" );
                    createAuth();
                    Log.d("debug", "After createAuth" + twitter);
                    AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(String... params) {
                            //createAuth();
                            if(twitter !=null) {
                                Log.d("debug", "search" + twitter);
                                try {
                                    searchForTwitter();
                                } catch (TwitterException e) {
                                    Log.d("debug", "error twitter" + twitter, e);
                                }
                            } else {
                                Log.d("debug", "twitter null" + twitter);
                            }
                            return true;
                        };
                    };
                    task.execute();

                    break;
            }
        }
    };

    // ギャラリーを開く
    private void selectPicture(){
        Intent intentGallery;
        intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
        intentGallery.setType("image/jpeg");

        startActivityForResult(intentGallery, REQUEST_CHOOSER);
    };

    // ギャラリーから写真を選んでimageView_pocchamaに表示
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHOOSER) {

            if(resultCode != RESULT_OK) {
                // キャンセル時
                return ;
            }
            resultUri = (data != null ? data.getData() : m_uri);
            if(resultUri == null) {
                // 取得失敗
                return;
            }
            // 画像を設定
            imageView.setImageURI(resultUri);
        }
    }

    // Step1 コメントをTwitterで投稿
    private void postingCommentOnTwitter(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String message= Uri.encode("ポッチャマインテント ＃コメント");
        intent.setData(Uri.parse("twitter://post?message=" + message));
        startActivity(intent);
    };

    // Step2 画像を投稿
    private void postingImageOnTwitter(){
        String message="";
        String imagePath= String.valueOf(resultUri);
        Log.d("debug","imagepath" + imagePath);

        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this);

        // データをセットする
        builder.setChooserTitle("Choose App");
        builder.setText(message);
        if(imagePath!=null){
            builder.setType("image/png");
            builder.addStream(resultUri);
            // アプリ選択画面を起動
            builder.startChooser();
        }else{
            nonImage();
        }
    };

    private void nonImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("写真を選んでから投稿しましょう！")
                .setTitle("Information")
                .setIcon(R.drawable.pocchama)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                     // ボタンをクリックしたときの動作
                    }
                });
        builder.show();
    };

    private void asyncSearch(){

    }

    private void searchForTwitter() throws TwitterException {
        Query query = new Query();

        // 検索ワードをセット（試しにバルスを検索）
        query.setQuery("RICOH THETA V");

        // 検索実行
        QueryResult result = twitter.search(query);

        System.out.println("ヒット数 : " + result.getTweets().size());

        // 検索結果を見てみる
        for (Status status : result.getTweets()) {
            // 本文
            System.out.println(status.getText());
            // 発言したユーザ
            System.out.println(status.getUser());
            // 発言した日時
            System.out.println(status.getCreatedAt());
            // 他、取れる値はJavaDoc参照
            // http://twitter4j.org/ja/javadoc/twitter4j/Tweet.html
        }
    }

    private void createAuth(){
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(getString(R.string.consumer_key))
                .setOAuthConsumerSecret(getString(R.string.consumer_secret))
                .setOAuthAccessToken(getString(R.string.access_token))
                .setOAuthAccessTokenSecret(getString(R.string.access_token_secret));

        twitterFactory = new TwitterFactory(cb.build());
        twitter = twitterFactory.getInstance();
    };
}