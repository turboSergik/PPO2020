package com.sergey.codeeditorPPO2020.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.sergey.codeeditorPPO2020.custom_view.SyntaxHighlightingEditText;
import com.sergey.codeeditorPPO2020.R;
import com.sergey.codeeditorPPO2020.helpers.TriggerEventByTimer;
import com.sergey.codeeditorPPO2020.socket.SyncTask;
import com.sergey.codeeditorPPO2020.adapters.FileManagerAdapter;
import com.sergey.codeeditorPPO2020.helpers.CodeTextWatcher;
import com.sergey.codeeditorPPO2020.models.File;
import com.sergey.codeeditorPPO2020.models.RunMetaInfo;
import com.sergey.codeeditorPPO2020.models.Settings;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private int currentFileId = -1;
    private FileManagerAdapter fileManagerAdapter;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadSetting();
            }
        }, 2000, 2000);

        loadSetting();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Help.py");
        setSupportActionBar(toolbar);

        fileManagerAdapter = new FileManagerAdapter(new ArrayList<File>(), getApplicationContext(), this);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFileId == -1)
                    return;

                fileManagerAdapter.getFiles().get(currentFileId).setSourceCode(((EditText) findViewById(R.id.editText)).getText().toString());

                final RunMetaInfo runMetaInfo = new RunMetaInfo();
                runMetaInfo.setFiles(fileManagerAdapter.getFiles());
                runMetaInfo.setHost(settings.getHost());
                runMetaInfo.setInterpreter(settings.getInterpreter());
                runMetaInfo.setRunFile(fileManagerAdapter.getFiles().get(currentFileId).getName());
                runMetaInfo.setPort(settings.getPort());

                (new SyncTask((TextView) findViewById(R.id.result))).execute(runMetaInfo);

                fileManagerAdapter.UpdateDBFileContent(fileManagerAdapter.getFiles().get(currentFileId).getName(), fileManagerAdapter.getFiles().get(currentFileId).getSourceCode());
            }
        });

        ((RecyclerView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0)
                .findViewById(R.id.recycleView)).setLayoutManager(new LinearLayoutManager(this));


        ((RecyclerView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0)
                .findViewById(R.id.recycleView)).setAdapter(fileManagerAdapter);


        ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsView = li.inflate(R.layout.dialog, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                mDialogBuilder.setView(promptsView);
                final EditText userInput = promptsView.findViewById(R.id.input_text);

                mDialogBuilder.setCancelable(false)
                    .setPositiveButton("Create file", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            fileManagerAdapter.addFile(new File(userInput.getText().toString(), ""));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                mDialogBuilder.create().show();
            }
        });


        final SyntaxHighlightingEditText editText = findViewById(R.id.editText);

        editText.addTextChangedListener(new CodeTextWatcher(getApplicationContext()));
        editText.getText().clear();
        editText.getText().append("# Step 1: go to settings and select the host and port of the remote server\n\n");
        editText.getText().append("# Step 2: In the menu on the left you can create your first project file\n\n");
        editText.getText().append("# Example (main.py):\n");
        editText.getText().append("print('Hello world!')\n");
        editText.setRawInputType(0x00000000);

        ScheduledExecutorService service = Executors.newScheduledThreadPool(3);
        // service.schedule(new TriggerEventByTimer(fileManagerAdapter, getCurrentFileId(), settings, this), 5000, TimeUnit.MILLISECONDS);

        service.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        TriggerEventByTimer.run(fileManagerAdapter, getCurrentFileId(), settings,
                                MainActivity.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendQueryToServer() {

    }

    private final Settings settings = new Settings();

    private void loadSetting() {
        final SharedPreferences sharedPreferences = getSharedPreferences("MY_SETTINGS", Context.MODE_PRIVATE);

        settings.setInterpreter(sharedPreferences.getString("interpreter", "Python 3"));
        settings.setTextSize(sharedPreferences.getInt("text_size", 20));
        settings.setPort(sharedPreferences.getInt("port", 9876));
        settings.setHost(sharedPreferences.getString("host", "10.0.2.2"));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final SyntaxHighlightingEditText editText = findViewById(R.id.editText);
                final Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Menlo-Regular.ttf");

                editText.setTypeface(tf);
                editText.setTextSize(settings.getTextSize());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                final Intent settings = new Intent(getApplicationContext(), SettingActivity.class);
                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getApplicationContext().startActivity(settings);

                return true;
            case R.id.action_help:
                if (getCurrentFileId() != -1)
                    fileManagerAdapter.getFiles().get(getCurrentFileId()).setSourceCode(((EditText) findViewById(R.id.editText)).getText().toString());

                ((androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar)).setTitle("Help.py");

                ((EditText) findViewById(R.id.editText)).getText().clear();
                ((EditText) findViewById(R.id.editText)).getText().append("# Step 1: go to settings and select the host and port of the remote server\n\n");
                ((EditText) findViewById(R.id.editText)).getText().append("# Step 2: In the menu on the left you can create your first project file\n\n");
                ((EditText) findViewById(R.id.editText)).getText().append("# Example (main.py):\n");
                ((EditText) findViewById(R.id.editText)).getText().append("print('Hello world!')");

                ((EditText) findViewById(R.id.editText)).setRawInputType(0x00000000);
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(Gravity.LEFT, true);
                setCurrentFileId(-1);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int getCurrentFileId() {
        return currentFileId;
    }

    public void setCurrentFileId(int currentFileId) {
        this.currentFileId = currentFileId;
    }

}
