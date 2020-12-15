package com.sergey.codeeditorPPO2020.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.sergey.codeeditorPPO2020.R;
import com.sergey.codeeditorPPO2020.models.Settings;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        (findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);

                final EditText hostEditText = findViewById(R.id.hostEditText);
                settings.setHost(hostEditText.getText().toString());

                final EditText textSizeEditText = findViewById(R.id.textSizeEditText);
                settings.setTextSize(Integer.parseInt(textSizeEditText.getText().toString()));

                final EditText portEditText = findViewById(R.id.portEditText);
                settings.setPort(Integer.parseInt(portEditText.getText().toString()));

                final RadioGroup radioGroup = findViewById(R.id.radioGroup);

                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonPython2)
                    settings.setInterpreter("Python 2");
                else
                    settings.setInterpreter("Python 3");

                sharedPreferences.edit().putString("interpreter", settings.getInterpreter())
                        .putInt("text_size", settings.getTextSize())
                        .putString("host", settings.getHost())
                        .putInt("port", settings.getPort()).apply();

                finish();
            }
        });

        (findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadSetting();
    }

    private Settings settings = new Settings();

    private void loadSetting() {
        final SharedPreferences sharedPreferences = getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);

        settings.setInterpreter(sharedPreferences.getString("interpreter", "Python 3"));
        settings.setTextSize(sharedPreferences.getInt("text_size", 20));
        settings.setPort(sharedPreferences.getInt("port", 9876));
        settings.setHost(sharedPreferences.getString("host", "10.0.2.2"));

        final EditText hostEditText = findViewById(R.id.hostEditText);
        hostEditText.setText(settings.getHost());

        final EditText textSizeEditText = findViewById(R.id.textSizeEditText);
        textSizeEditText.setText(Integer.toString(settings.getTextSize()));

        final EditText portEditText = findViewById(R.id.portEditText);
        portEditText.setText(Integer.toString(settings.getPort()));

        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        if (settings.getInterpreter().equals("Python 3"))
            radioGroup.check(R.id.radioButtonPython3);
        else
            radioGroup.check(R.id.radioButtonPython2);
    }
}
