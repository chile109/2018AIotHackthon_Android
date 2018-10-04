package app.kevin.com.speachrecognizationtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements RecognitionListener {
    private String Tag = "VoiceRecognitionActivity";
    private TextView returnedText;
    private ProgressBar progressBar;
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

        // 判斷裝置是否有支援語音辨識功能的 App, 若沒有則失效之
        PackageManager pm = getPackageManager();
        //---------查詢有無裝Google Voice Search Engine---------
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        //-------------如果有找到-------------
        if (activities.size() != 0) {
            try {

                progressBar.setVisibility(View.INVISIBLE);
                speech = SpeechRecognizer.createSpeechRecognizer(this);
                speech.setRecognitionListener(this);
                recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setIndeterminate(true);
                            speech.startListening(recognizerIntent);
                        } else {
                            progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.INVISIBLE);
                            speech.stopListening();
                        }
                    }
                });

            } catch (Exception e) {//catch error message
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage(e.getMessage()).show();
                ;
            }
        } else {//----------如果找不到-------------
            Toast.makeText(MainActivity.this
                    , "找不到語音辨識 App !!"
                    , Toast.LENGTH_LONG
            ).show();

//且導向Market Google語音下載網頁 讓使用者下載
            String url = "https://market.android.com/details?id=com.google.android.voicesearch";
            Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//啟動Intent
            startActivity(ie);
        }
    }

    //region recognizer
    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(Tag, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onRmsChanged(float rmsdB) {

        progressBar.setProgress((int) rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(Tag, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int error) {
        toggleButton.setChecked(false);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(Tag, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        String saying = "";
        for (String result : matches) {

            switch (result) {
                case "hello":
                    saying = "很高興認識你";
                    break;
                case "Kevin":
                    saying = "美佳小文青";
                    break;
                case "bye":
                    saying = "再見";
                    break;
            }

            text += result + "\n";
        }

        if (saying.isEmpty()) {
            saying = "無法辨識";
        }

        Toast.makeText(MainActivity.this
                , saying
                , Toast.LENGTH_LONG
        ).show();

        returnedText.setText(text);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
    //endregion
}

