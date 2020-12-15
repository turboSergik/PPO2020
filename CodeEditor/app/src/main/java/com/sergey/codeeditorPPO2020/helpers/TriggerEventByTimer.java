package com.sergey.codeeditorPPO2020.helpers;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.sergey.codeeditorPPO2020.R;
import com.sergey.codeeditorPPO2020.activitys.MainActivity;
import com.sergey.codeeditorPPO2020.adapters.FileManagerAdapter;
import com.sergey.codeeditorPPO2020.models.RunMetaInfo;
import com.sergey.codeeditorPPO2020.models.Settings;
import com.sergey.codeeditorPPO2020.socket.SyncTask;

public class TriggerEventByTimer {

    public TriggerEventByTimer(FileManagerAdapter fileManagerAdapter, int currentFileId, Settings settings, MainActivity main) {

        System.out.println(currentFileId);
        if (currentFileId == -1) return;

        fileManagerAdapter.getFiles().get(currentFileId).setSourceCode(((EditText) main.findViewById(R.id.editText)).getText().toString());

        final RunMetaInfo runMetaInfo = new RunMetaInfo();
        runMetaInfo.setFiles(fileManagerAdapter.getFiles());
        runMetaInfo.setHost(settings.getHost());
        runMetaInfo.setInterpreter(settings.getInterpreter());
        runMetaInfo.setRunFile(fileManagerAdapter.getFiles().get(currentFileId).getName());
        runMetaInfo.setPort(settings.getPort());

        (new SyncTask((TextView) main.findViewById(R.id.result))).execute(runMetaInfo);
    }

    private static String lastSendedSource = "";

    public static void run(FileManagerAdapter fileManagerAdapter, int currentFileId, Settings settings, MainActivity main) {

        try {
            System.out.println(currentFileId);
            if (currentFileId == -1) return;

            if (lastSendedSource.equals(((EditText) main.findViewById(R.id.editText)).getText().toString())) {
                return;
            }
            else {
                lastSendedSource = ((EditText) main.findViewById(R.id.editText)).getText().toString();
            }


            fileManagerAdapter.getFiles().get(currentFileId).setSourceCode(((EditText) main.findViewById(R.id.editText)).getText().toString());

            final RunMetaInfo runMetaInfo = new RunMetaInfo();
            runMetaInfo.setFiles(fileManagerAdapter.getFiles());
            runMetaInfo.setHost(settings.getHost());
            runMetaInfo.setInterpreter(settings.getInterpreter());
            runMetaInfo.setRunFile(fileManagerAdapter.getFiles().get(currentFileId).getName());
            runMetaInfo.setPort(settings.getPort());

            (new SyncTask((TextView) main.findViewById(R.id.result))).execute(runMetaInfo);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
