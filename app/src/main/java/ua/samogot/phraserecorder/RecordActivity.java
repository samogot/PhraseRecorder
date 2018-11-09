package ua.samogot.phraserecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RecordActivity extends AppCompatActivity {

    private MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        int index = intent.getIntExtra(MainActivity.EXTRA_INDEX, -1);
        String[] stringArray = getResources().getStringArray(R.array.phrases);

        if (index < 0 || index >= stringArray.length) {
            finish();
            return;
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(stringArray[index]);


        // Check for permissions
        int permission_write = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_record = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        // If we don't have permissions, ask user for permissions
        if (permission_write != PackageManager.PERMISSION_GRANTED || permission_record != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
            int REQUEST_EXTERNAL_STORAGE = 1;

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        Random random = new Random();

        File dir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
        if (!dir.exists() && !dir.mkdirs()) {
            finish();
            return;
        }
        String filename = getResources().getString(R.string.version) + "_" + index + "_" + Math.abs(random.nextInt()) + ".mp3";
        File file = new File(dir, filename);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(file.getAbsolutePath());
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void stopRecording(View view) {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recorder.stop();
    }
}
