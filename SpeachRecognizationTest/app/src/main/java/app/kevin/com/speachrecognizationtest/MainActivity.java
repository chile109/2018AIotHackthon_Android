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
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    static String TAG = "main";
    private TextToSpeech tts;
    List<ResolveInfo> activities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button2 = (Button)findViewById( R.id.button2);
        createLanguageTTS();

        button2.setOnClickListener( new Button.OnClickListener(){
            @Override
            public void onClick(View arg0)
            {
                //【英文】發音
                tts.speak( "good job", TextToSpeech.QUEUE_FLUSH, null );
            }
        });

        Button button = (Button)findViewById( R.id.button);
        PackageManager pm = getPackageManager();

        activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);

        button.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {
                if(activities.size()!=0){
                    try{

                        //------------語音辨識Intent-----------
                        Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, Locale.TRADITIONAL_CHINESE.toString() );
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);//設定只出現辨識結果第一筆

                        // ----------開啟語音辨識Intent-----------
                        startActivityForResult( intent, 0 );

                    }catch(Exception e){//catch error message
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage(e.getMessage()).show();;
                    }
                }else {
                    Toast.makeText(MainActivity.this
                            , "找不到語音辨識 App !!"
                            , Toast.LENGTH_LONG
                    ).show();

                    String url = "https://market.android.com/details?id=com.google.android.voicesearch";
                    Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(ie);
                }

            }

        });
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

    private void createLanguageTTS()
    {
        if( tts == null )
        {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int arg0)
                {
                    // TTS 初始化成功
                    if( arg0 == TextToSpeech.SUCCESS )
                    {
                        // 指定的語系: 英文(美國)
                        Locale l = Locale.US;  // 不要用 Locale.ENGLISH, 會預設用英文(印度)

                        // 目前指定的【語系+國家】TTS, 已下載離線語音檔, 可以離線發音
                        if( tts.isLanguageAvailable( l ) == TextToSpeech.LANG_COUNTRY_AVAILABLE )
                        {
                            tts.setLanguage( l );
                        }
                    }
                }}
            );
        }
    }
}
