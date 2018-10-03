package app.kevin.com.speachrecognizationtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    static String TAG = "main";
    private SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById( R.id.button);

        // 判斷裝置是否有支援語音辨識功能的 App, 若沒有則失效之
        PackageManager pm = getPackageManager();
        //---------查詢有無裝Google Voice Search Engine---------
        List<ResolveInfo> activities=pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        //-------------如果有找到-------------
        if(activities.size()!=0){
            try{

        //------------語音辨識Intent-----------
                Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

//                intent.putExtra
//                        (
//                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//                        );

                intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, Locale.TRADITIONAL_CHINESE.toString() );
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);//設定只出現辨識結果第一筆

                // ----------開啟語音辨識Intent-----------
                startActivityForResult( intent, 0 );

            }catch(Exception e){//catch error message
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage(e.getMessage()).show();;
            }
        }else{//----------如果找不到-------------
            Toast.makeText(MainActivity.this
                    , "找不到語音辨識 App !!"
                    , Toast.LENGTH_LONG
            ).show();

//且導向Market Google語音下載網頁 讓使用者下載
            String url="https://market.android.com/details?id=com.google.android.voicesearch";
            Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//啟動Intent
            startActivity(ie);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if( requestCode == 0 && resultCode == RESULT_OK )
        {
            String resultsString = "";

            // 取得 STT 語音辨識的結果段落
            ArrayList results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );

            // 語音辨識的每個段落
            for( int i = 0; i < results.size(); i++ )
            {
                // 一個段落可拆解為多個字組
                String[] resultWords = results.get(i).toString().split(" ");

                for( int j = 0; j < resultWords.length; j++ )
                {
                    resultsString += resultWords[j] + ":";
                }
            }

            // 顯示結果
            Toast.makeText( this, resultsString, Toast.LENGTH_LONG ).show();
        }

        super.onActivityResult( requestCode, resultCode, data );
    }
}
