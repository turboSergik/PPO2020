package com.sergey.codeeditorPPO2020.socket;

import android.os.AsyncTask;
import android.widget.TextView;

import com.sergey.codeeditorPPO2020.models.RunMetaInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SyncTask extends AsyncTask<RunMetaInfo, Void, StringBuilder> {
    private final TextView textView;

    public SyncTask(final TextView textView) {
        this.textView = textView;
        textView.setText("");
    }

    @Override
    protected StringBuilder doInBackground(RunMetaInfo... lists) {
        RunMetaInfo runMetaInfo = lists[0];

        Connection connection = new Connection(runMetaInfo.getHost(), runMetaInfo.getPort());

        try {
            connection.openConnection();
            connection.sendData(runMetaInfo);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(connection.getSocket().getInputStream()));

            String s;
            StringBuilder full = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                full.append(s);
                full.append("\n");
            }

            return full;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(StringBuilder answer) {
        textView.setText(answer);
    }
}
