package com.android_practice0606.ca7mi.android_practice0606_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.support.v4.app.ShareCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity {

    private Uri m_uri;
    private Uri resultUri = null;
    private static final int REQUEST_CHOOSER = 1000;
    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_pocchama);

        final Button button1 = findViewById(R.id.button_select);
        button1.setOnClickListener(buttonClick);

        final Button button2 = findViewById(R.id.button_twitter);
        button2.setOnClickListener(buttonClick);
    };

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_select:
                    Log.d("debug","button_select, Perform action on click");
                    selectPicture();
                    break;

                case R.id.button_twitter:
                    Log.d("debug","button_twitter, Perform action on click");
                    //postingCommentOnTwitter();
                    postingImageOnTwitter();
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
            nonImege();
        }
    };

    private void nonImege(){
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
}