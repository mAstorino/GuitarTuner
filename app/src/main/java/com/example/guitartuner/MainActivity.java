package com.example.guitartuner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.DecimalFormat;

//Extern library: TarsosDSP version 2.4
//Link to the documentation:
//https://0110.be/releases/TarsosDSP/TarsosDSP-latest/TarsosDSP-latest-Documentation/

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class MainActivity extends AppCompatActivity {

    //Guitar strings note frequencies
    private static final double LOW_E = 82.4;
    private static final double A = 110.0;
    private static final double D = 146.8;
    private static final double G = 196.0;
    private static final double B = 246.9;
    private static final double HIGH_E = 329.6;

    //Convention values
    private static final double ACCEPTED_OF_KEY_RANGE = 1.0;
    private static final double NO_INPUT_VALUE = -1.0;
    private static final String NO_INPUT_STRING = "...";
    private static final String BUTTON_NOT_FOCUS = "Waiting...";
    private static final String BUTTON_FOCUS = "Listening...";
    private static final String BUTTON_TUNED = "Tuned!";
    private static final String START_TUNING_STRING = "START TUNING";
    private static final String STOP_TUNING_STRING = "STOP TUNING";
    private static final boolean BUTTON_PRESSED = true;
    private static final boolean BUTTON_NOT_PRESSED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checking that microphone permission is granted
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    0);
        }

        //Initializing all button tags
        Button buttonLowE = findViewById(R.id.buttonLowE);
        buttonLowE.setTag(BUTTON_NOT_PRESSED);
        Button buttonA = findViewById(R.id.buttonA);
        buttonA.setTag(BUTTON_NOT_PRESSED);
        Button buttonD = findViewById(R.id.buttonD);
        buttonD.setTag(BUTTON_NOT_PRESSED);
        Button buttonG = findViewById(R.id.buttonG);
        buttonG.setTag(BUTTON_NOT_PRESSED);
        Button buttonB = findViewById(R.id.buttonB);
        buttonB.setTag(BUTTON_NOT_PRESSED);
        Button buttonHighE = findViewById(R.id.buttonHighE);
        buttonHighE.setTag(BUTTON_NOT_PRESSED);
        Button buttonStartTuning = findViewById(R.id.buttonStartTuning);
        buttonStartTuning.setTag(BUTTON_NOT_PRESSED);

        //Default Microphone Detection
        AudioDispatcher dispatcher =
            AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        //This is the common declaration of the class PitchDetectionHandler
        //It is shown like follows in both documentation and examples
        //It is recommended to keep it this way to guarantee the maximum compatibility
        PitchDetectionHandler pdh = (res, e) -> {
            final float pitchInHz = res.getPitch();
            runOnUiThread(() -> processPitch(pitchInHz));
        };

        //This is the declaration of the thread that will be executed until the app gets closed
        AudioProcessor pitchProcessor = new
            PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);
        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    private void processPitch(float pitchInHz) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        ImageView imageViewLowE = findViewById(R.id.imageViewLowE);
        TextView textViewLowE = findViewById(R.id.textViewLowE);

        Button buttonA = findViewById(R.id.buttonA);
        ImageView imageViewA = findViewById(R.id.imageViewA);
        TextView textViewA = findViewById(R.id.textViewA);

        Button buttonD = findViewById(R.id.buttonD);
        ImageView imageViewD = findViewById(R.id.imageViewD);
        TextView textViewD = findViewById(R.id.textViewD);

        Button buttonG = findViewById(R.id.buttonG);
        ImageView imageViewG = findViewById(R.id.imageViewG);
        TextView textViewG = findViewById(R.id.textViewG);

        Button buttonB = findViewById(R.id.buttonB);
        ImageView imageViewB = findViewById(R.id.imageViewB);
        TextView textViewB = findViewById(R.id.textViewB);

        Button buttonHighE = findViewById(R.id.buttonHighE);
        ImageView imageViewHighE = findViewById(R.id.imageViewHighE);
        TextView textViewHighE = findViewById(R.id.textViewHighE);

        Button buttonStartTuning = findViewById(R.id.buttonStartTuning);

        DecimalFormat df = new DecimalFormat("#.##");

        //This assigns a value to the TextView of the pitch detection
        TextView textViewPitch = findViewById(R.id.pitchTextView);
        if (pitchInHz == NO_INPUT_VALUE) {
            textViewPitch.setText(NO_INPUT_STRING);
        }
        else {
            textViewPitch.setText(df.format(pitchInHz).concat(" hz"));
        }

        //Management Button Low E
        if (buttonLowE.getTag().equals(BUTTON_PRESSED)) {
            if (pitchInHz < LOW_E - ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewLowE.setImageResource(R.mipmap.green_up_arrow_foreground);
                textViewLowE.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > LOW_E + ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewLowE.setImageResource(R.mipmap.red_down_arrow_foreground);
                textViewLowE.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz == NO_INPUT_VALUE && textViewLowE.getText() != BUTTON_TUNED) {
                imageViewLowE.setImageResource(R.mipmap.question_mark_foreground);
                textViewLowE.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > LOW_E - ACCEPTED_OF_KEY_RANGE &&
                     pitchInHz < LOW_E + ACCEPTED_OF_KEY_RANGE) {
                imageViewLowE.setImageResource(R.mipmap.green_tick_foreground);
                textViewLowE.setText(BUTTON_TUNED);
            }
        }
        else {
            if (textViewLowE.getText() != BUTTON_TUNED) {
                imageViewLowE.setImageResource(R.mipmap.question_mark_foreground);
                textViewLowE.setText(BUTTON_NOT_FOCUS);
            }
        }

        //Management Button A
        if (buttonA.getTag().equals(BUTTON_PRESSED)) {
            if (pitchInHz < A - ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewA.setImageResource(R.mipmap.green_up_arrow_foreground);
                textViewA.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > A + ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewA.setImageResource(R.mipmap.red_down_arrow_foreground);
                textViewA.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz == NO_INPUT_VALUE && textViewA.getText() != BUTTON_TUNED) {
                imageViewA.setImageResource(R.mipmap.question_mark_foreground);
                textViewA.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > A - ACCEPTED_OF_KEY_RANGE &&
                    pitchInHz < A + ACCEPTED_OF_KEY_RANGE) {
                imageViewA.setImageResource(R.mipmap.green_tick_foreground);
                textViewA.setText(BUTTON_TUNED);
            }
        }
        else {
            if (textViewA.getText() != BUTTON_TUNED) {
                imageViewA.setImageResource(R.mipmap.question_mark_foreground);
                textViewA.setText(BUTTON_NOT_FOCUS);
            }
        }

        //Management Button D
        if (buttonD.getTag().equals(BUTTON_PRESSED)) {
            if (pitchInHz < D - ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewD.setImageResource(R.mipmap.green_up_arrow_foreground);
                textViewD.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > D + ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewD.setImageResource(R.mipmap.red_down_arrow_foreground);
                textViewD.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz == NO_INPUT_VALUE && textViewD.getText() != BUTTON_TUNED) {
                imageViewD.setImageResource(R.mipmap.question_mark_foreground);
                textViewD.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > D - ACCEPTED_OF_KEY_RANGE &&
                    pitchInHz < D + ACCEPTED_OF_KEY_RANGE) {
                imageViewD.setImageResource(R.mipmap.green_tick_foreground);
                textViewD.setText(BUTTON_TUNED);
            }
        }
        else {
            if (textViewD.getText() != BUTTON_TUNED) {
                imageViewD.setImageResource(R.mipmap.question_mark_foreground);
                textViewD.setText(BUTTON_NOT_FOCUS);
            }
        }

        //Management Button G
        if (buttonG.getTag().equals(BUTTON_PRESSED)) {
            if (pitchInHz < G - ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewG.setImageResource(R.mipmap.green_up_arrow_foreground);
                textViewG.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > G + ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewG.setImageResource(R.mipmap.red_down_arrow_foreground);
                textViewG.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz == NO_INPUT_VALUE && textViewG.getText() != BUTTON_TUNED) {
                imageViewG.setImageResource(R.mipmap.question_mark_foreground);
                textViewG.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > G - ACCEPTED_OF_KEY_RANGE &&
                    pitchInHz < G + ACCEPTED_OF_KEY_RANGE) {
                imageViewG.setImageResource(R.mipmap.green_tick_foreground);
                textViewG.setText(BUTTON_TUNED);
            }
        }
        else {
            if (textViewG.getText() != BUTTON_TUNED) {
                imageViewG.setImageResource(R.mipmap.question_mark_foreground);
                textViewG.setText(BUTTON_NOT_FOCUS);
            }
        }

        //Management Button B
        if (buttonB.getTag().equals(BUTTON_PRESSED)) {
            if (pitchInHz < B - ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewB.setImageResource(R.mipmap.green_up_arrow_foreground);
                textViewB.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > B + ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewB.setImageResource(R.mipmap.red_down_arrow_foreground);
                textViewB.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz == NO_INPUT_VALUE && textViewB.getText() != BUTTON_TUNED) {
                imageViewB.setImageResource(R.mipmap.question_mark_foreground);
                textViewB.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > B - ACCEPTED_OF_KEY_RANGE &&
                    pitchInHz < B + ACCEPTED_OF_KEY_RANGE) {
                imageViewB.setImageResource(R.mipmap.green_tick_foreground);
                textViewB.setText(BUTTON_TUNED);
            }
        }
        else {
            if (textViewB.getText() != BUTTON_TUNED) {
                imageViewB.setImageResource(R.mipmap.question_mark_foreground);
                textViewB.setText(BUTTON_NOT_FOCUS);
            }
        }

        //Management Button High E
        if (buttonHighE.getTag().equals(BUTTON_PRESSED)) {
            if (pitchInHz < HIGH_E - ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewHighE.setImageResource(R.mipmap.green_up_arrow_foreground);
                textViewHighE.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > HIGH_E + ACCEPTED_OF_KEY_RANGE && pitchInHz != NO_INPUT_VALUE) {
                imageViewHighE.setImageResource(R.mipmap.red_down_arrow_foreground);
                textViewHighE.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz == NO_INPUT_VALUE && textViewHighE.getText() != BUTTON_TUNED) {
                imageViewHighE.setImageResource(R.mipmap.question_mark_foreground);
                textViewHighE.setText(BUTTON_FOCUS);
            }
            else if (pitchInHz > HIGH_E - ACCEPTED_OF_KEY_RANGE &&
                    pitchInHz < HIGH_E + ACCEPTED_OF_KEY_RANGE) {
                imageViewHighE.setImageResource(R.mipmap.green_tick_foreground);
                textViewHighE.setText(BUTTON_TUNED);
            }
        }
        else {
            if (textViewHighE.getText() != BUTTON_TUNED) {
                imageViewHighE.setImageResource(R.mipmap.question_mark_foreground);
                textViewHighE.setText(BUTTON_NOT_FOCUS);
            }
        }

        //Management Button Start Tuning
        if (buttonStartTuning.getTag().equals(BUTTON_PRESSED)) {

            //Tuning Low E
            if (textViewLowE.getText().equals(BUTTON_TUNED)) {
                buttonLowE.setTag(BUTTON_NOT_PRESSED);
                buttonA.setTag(BUTTON_PRESSED);
            }

            //Tuning A
            if (textViewA.getText().equals(BUTTON_TUNED)) {
                buttonA.setTag(BUTTON_NOT_PRESSED);
                buttonD.setTag(BUTTON_PRESSED);
            }

            //Tuning D
            if (textViewD.getText().equals(BUTTON_TUNED)) {
                buttonD.setTag(BUTTON_NOT_PRESSED);
                buttonG.setTag(BUTTON_PRESSED);
            }

            //Tuning G
            if (textViewG.getText().equals(BUTTON_TUNED)) {
                buttonG.setTag(BUTTON_NOT_PRESSED);
                buttonB.setTag(BUTTON_PRESSED);
            }

            //Tuning B
            if (textViewB.getText().equals(BUTTON_TUNED)) {
                buttonB.setTag(BUTTON_NOT_PRESSED);
                buttonHighE.setTag(BUTTON_PRESSED);
            }

            //Tuning High E
            if (textViewHighE.getText().equals(BUTTON_TUNED)) {
                buttonHighE.setTag(BUTTON_NOT_PRESSED);

                //Restoring Start Button
                buttonStartTuning.setText(START_TUNING_STRING);
                buttonStartTuning.setTag(BUTTON_NOT_PRESSED);

                //All the buttons get able
                buttonLowE.setClickable(true);
                buttonA.setClickable(true);
                buttonD.setClickable(true);
                buttonG.setClickable(true);
                buttonB.setClickable(true);
                buttonHighE.setClickable(true);
            }
        }
    }

    //Button Low E Tag = 1, Other Buttons Tag = 0
    public void buttonLowEPressed(View view) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        if (buttonLowE.getTag().equals(BUTTON_PRESSED)) {
            buttonLowE.setTag(BUTTON_NOT_PRESSED);
        }
        else {
            buttonLowE.setTag(BUTTON_PRESSED);
            ImageView imageViewLowE = findViewById(R.id.imageViewLowE);
            imageViewLowE.setImageResource(R.mipmap.question_mark_foreground);
            TextView textViewLowE = findViewById(R.id.textViewLowE);
            textViewLowE.setText(BUTTON_FOCUS);
        }
        Button buttonA = findViewById(R.id.buttonA);
        buttonA.setTag(BUTTON_NOT_PRESSED);
        Button buttonD = findViewById(R.id.buttonD);
        buttonD.setTag(BUTTON_NOT_PRESSED);
        Button buttonG = findViewById(R.id.buttonG);
        buttonG.setTag(BUTTON_NOT_PRESSED);
        Button buttonB = findViewById(R.id.buttonB);
        buttonB.setTag(BUTTON_NOT_PRESSED);
        Button buttonHighE = findViewById(R.id.buttonHighE);
        buttonHighE.setTag(BUTTON_NOT_PRESSED);
    }

    //Button A Tag = 1, Other Buttons Tag = 0
    public void buttonAPressed(View view) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        buttonLowE.setTag(BUTTON_NOT_PRESSED);
        Button buttonA = findViewById(R.id.buttonA);
        if (buttonA.getTag().equals(BUTTON_PRESSED)) {
            buttonA.setTag(BUTTON_NOT_PRESSED);
        }
        else {
            buttonA.setTag(BUTTON_PRESSED);
            ImageView imageViewA = findViewById(R.id.imageViewA);
            imageViewA.setImageResource(R.mipmap.question_mark_foreground);
            TextView textViewA = findViewById(R.id.textViewA);
            textViewA.setText(BUTTON_FOCUS);
        }
        Button buttonD = findViewById(R.id.buttonD);
        buttonD.setTag(BUTTON_NOT_PRESSED);
        Button buttonG = findViewById(R.id.buttonG);
        buttonG.setTag(BUTTON_NOT_PRESSED);
        Button buttonB = findViewById(R.id.buttonB);
        buttonB.setTag(BUTTON_NOT_PRESSED);
        Button buttonHighE = findViewById(R.id.buttonHighE);
        buttonHighE.setTag(BUTTON_NOT_PRESSED);
    }

    //Button D Tag = 1, Other Buttons Tag = 0
    public void buttonDPressed(View view) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        buttonLowE.setTag(BUTTON_NOT_PRESSED);
        Button buttonA = findViewById(R.id.buttonA);
        buttonA.setTag(BUTTON_NOT_PRESSED);
        Button buttonD = findViewById(R.id.buttonD);
        if (buttonD.getTag().equals(BUTTON_PRESSED)) {
            buttonD.setTag(BUTTON_NOT_PRESSED);
        }
        else {
            buttonD.setTag(BUTTON_PRESSED);
            ImageView imageViewD = findViewById(R.id.imageViewD);
            imageViewD.setImageResource(R.mipmap.question_mark_foreground);
            TextView textViewD = findViewById(R.id.textViewD);
            textViewD.setText(BUTTON_FOCUS);
        }
        Button buttonG = findViewById(R.id.buttonG);
        buttonG.setTag(BUTTON_NOT_PRESSED);
        Button buttonB = findViewById(R.id.buttonB);
        buttonB.setTag(BUTTON_NOT_PRESSED);
        Button buttonHighE = findViewById(R.id.buttonHighE);
        buttonHighE.setTag(BUTTON_NOT_PRESSED);
    }

    //Button G Tag = 1, Other Buttons Tag = 0
    public void buttonGPressed(View view) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        buttonLowE.setTag(BUTTON_NOT_PRESSED);
        Button buttonA = findViewById(R.id.buttonA);
        buttonA.setTag(BUTTON_NOT_PRESSED);
        Button buttonD = findViewById(R.id.buttonD);
        buttonD.setTag(BUTTON_NOT_PRESSED);
        Button buttonG = findViewById(R.id.buttonG);
        if (buttonG.getTag().equals(BUTTON_PRESSED)) {
            buttonG.setTag(BUTTON_NOT_PRESSED);
        }
        else {
            buttonG.setTag(BUTTON_PRESSED);
            ImageView imageViewG = findViewById(R.id.imageViewG);
            imageViewG.setImageResource(R.mipmap.question_mark_foreground);
            TextView textViewG = findViewById(R.id.textViewG);
            textViewG.setText(BUTTON_FOCUS);
        }
        Button buttonB = findViewById(R.id.buttonB);
        buttonB.setTag(BUTTON_NOT_PRESSED);
        Button buttonHighE = findViewById(R.id.buttonHighE);
        buttonHighE.setTag(BUTTON_NOT_PRESSED);
    }

    //Button B Tag = 1, Other Buttons Tag = 0
    public void buttonBPressed(View view) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        buttonLowE.setTag(BUTTON_NOT_PRESSED);
        Button buttonA = findViewById(R.id.buttonA);
        buttonA.setTag(BUTTON_NOT_PRESSED);
        Button buttonD = findViewById(R.id.buttonD);
        buttonD.setTag(BUTTON_NOT_PRESSED);
        Button buttonG = findViewById(R.id.buttonG);
        buttonG.setTag(BUTTON_NOT_PRESSED);
        Button buttonB = findViewById(R.id.buttonB);
        if (buttonB.getTag().equals(BUTTON_PRESSED)) {
            buttonB.setTag(BUTTON_NOT_PRESSED);
        }
        else {
            buttonB.setTag(BUTTON_PRESSED);
            ImageView imageViewB = findViewById(R.id.imageViewB);
            imageViewB.setImageResource(R.mipmap.question_mark_foreground);
            TextView textViewB = findViewById(R.id.textViewB);
            textViewB.setText(BUTTON_FOCUS);
        }
        Button buttonHighE = findViewById(R.id.buttonHighE);
        buttonHighE.setTag(BUTTON_NOT_PRESSED);
    }

    //Button High E Tag = 1, Other Buttons Tag = 0
    public void buttonHighEPressed(View view) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        buttonLowE.setTag(BUTTON_NOT_PRESSED);
        Button buttonA = findViewById(R.id.buttonA);
        buttonA.setTag(BUTTON_NOT_PRESSED);
        Button buttonD = findViewById(R.id.buttonD);
        buttonD.setTag(BUTTON_NOT_PRESSED);
        Button buttonG = findViewById(R.id.buttonG);
        buttonG.setTag(BUTTON_NOT_PRESSED);
        Button buttonB = findViewById(R.id.buttonB);
        buttonB.setTag(BUTTON_NOT_PRESSED);
        Button buttonHighE = findViewById(R.id.buttonHighE);
        if (buttonHighE.getTag().equals(BUTTON_PRESSED)) {
            buttonHighE.setTag(BUTTON_NOT_PRESSED);
        }
        else {
            buttonHighE.setTag(BUTTON_PRESSED);
            ImageView imageViewHighE = findViewById(R.id.imageViewHighE);
            imageViewHighE.setImageResource(R.mipmap.question_mark_foreground);
            TextView textViewHighE = findViewById(R.id.textViewHighE);
            textViewHighE.setText(BUTTON_FOCUS);
        }
    }

    //Button Start Tuning Tag = 1, Other Buttons Tag = 0
    public void buttonStartTuningPressed(View view) {
        Button buttonLowE = findViewById(R.id.buttonLowE);
        buttonLowE.setTag(BUTTON_NOT_PRESSED);
        Button buttonA = findViewById(R.id.buttonA);
        buttonA.setTag(BUTTON_NOT_PRESSED);
        Button buttonD = findViewById(R.id.buttonD);
        buttonD.setTag(BUTTON_NOT_PRESSED);
        Button buttonG = findViewById(R.id.buttonG);
        buttonG.setTag(BUTTON_NOT_PRESSED);
        Button buttonB = findViewById(R.id.buttonB);
        buttonB.setTag(BUTTON_NOT_PRESSED);
        Button buttonHighE = findViewById(R.id.buttonHighE);
        buttonHighE.setTag(BUTTON_NOT_PRESSED);

        TextView textViewLowE = findViewById(R.id.textViewLowE);
        TextView textViewA = findViewById(R.id.textViewA);
        TextView textViewD = findViewById(R.id.textViewD);
        TextView textViewG = findViewById(R.id.textViewG);
        TextView textViewB = findViewById(R.id.textViewB);
        TextView textViewHighE = findViewById(R.id.textViewHighE);

        ImageView imageViewLowE = findViewById(R.id.imageViewLowE);
        ImageView imageViewA = findViewById(R.id.imageViewA);
        ImageView imageViewD = findViewById(R.id.imageViewD);
        ImageView imageViewG = findViewById(R.id.imageViewG);
        ImageView imageViewB = findViewById(R.id.imageViewB);
        ImageView imageViewHighE = findViewById(R.id.imageViewHighE);

        textViewLowE.setText(BUTTON_NOT_FOCUS);
        textViewA.setText(BUTTON_NOT_FOCUS);
        textViewD.setText(BUTTON_NOT_FOCUS);
        textViewG.setText(BUTTON_NOT_FOCUS);
        textViewB.setText(BUTTON_NOT_FOCUS);
        textViewHighE.setText(BUTTON_NOT_FOCUS);

        imageViewLowE.setImageResource(R.mipmap.question_mark_foreground);
        imageViewA.setImageResource(R.mipmap.question_mark_foreground);
        imageViewD.setImageResource(R.mipmap.question_mark_foreground);
        imageViewG.setImageResource(R.mipmap.question_mark_foreground);
        imageViewB.setImageResource(R.mipmap.question_mark_foreground);
        imageViewHighE.setImageResource(R.mipmap.question_mark_foreground);

        Button buttonStartTuning = findViewById(R.id.buttonStartTuning);

        if (buttonStartTuning.getTag().equals(BUTTON_PRESSED)) {
            buttonStartTuning.setTag(BUTTON_NOT_PRESSED);
            buttonStartTuning.setText(START_TUNING_STRING);
            //All the buttons get able
            buttonLowE.setClickable(true);
            buttonA.setClickable(true);
            buttonD.setClickable(true);
            buttonG.setClickable(true);
            buttonB.setClickable(true);
            buttonHighE.setClickable(true);

        }
        else {
            //All the buttons get disabled
            buttonLowE.setClickable(false);
            buttonA.setClickable(false);
            buttonD.setClickable(false);
            buttonG.setClickable(false);
            buttonB.setClickable(false);
            buttonHighE.setClickable(false);

            buttonStartTuning.setTag(BUTTON_PRESSED);
            buttonStartTuning.setText(STOP_TUNING_STRING);
            buttonLowE.setTag(BUTTON_PRESSED);
            textViewLowE.setText(BUTTON_FOCUS);
        }
    }
}