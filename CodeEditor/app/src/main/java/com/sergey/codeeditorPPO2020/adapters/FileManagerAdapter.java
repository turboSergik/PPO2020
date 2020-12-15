package com.sergey.codeeditorPPO2020.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.codeeditorPPO2020.activitys.MainActivity;
import com.sergey.codeeditorPPO2020.R;
import com.sergey.codeeditorPPO2020.helpers.DataBaseHelper;
import com.sergey.codeeditorPPO2020.models.File;

import java.util.List;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.FeedModelViewHolder> {
    private final Context context;

    public List<File> getFiles() {
        return files;
    }

    private List<File> files;
    private final MainActivity mainActivity;

    private DataBaseHelper dbHelper;

    static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;

        FeedModelViewHolder(final View v) {
            super(v);
            rssFeedView = v;
        }
    }

    public FileManagerAdapter(final List<File> rssFeedModels,
                              final Context context,
                              final MainActivity mainActivity) {
        this.context = context;
        files = rssFeedModels;
        this.mainActivity = mainActivity;

        UploadFilesFromDB();
    }

    public void UploadFilesFromDB() {
        dbHelper = new DataBaseHelper(mainActivity);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DataBaseHelper.TABLE_FILES, null, null,
                null, null ,null, null);

        if (cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex((DataBaseHelper.KEY_ID));
            int fileNameIndex = cursor.getColumnIndex((DataBaseHelper.KEY_FILE_NAME));
            int fileContentIndex = cursor.getColumnIndex((DataBaseHelper.KEY_TEXT));

            do {
                int id = cursor.getInt(idIndex);
                String fileName = cursor.getString(fileNameIndex);
                String fileContent = cursor.getString(fileContentIndex);

                addExistFile(new File(fileName, fileContent));

            } while (cursor.moveToNext());
        }

        cursor.close();
        dbHelper.close();
    }

    public void UpdateDBFileContent(String fileName, String fileContent) {

        dbHelper = new DataBaseHelper(mainActivity);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DataBaseHelper.TABLE_FILES, null, null,
                null, null ,null, null);

        int sourceId = 0;

        if (cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex((DataBaseHelper.KEY_ID));
            int fileNameIndex = cursor.getColumnIndex((DataBaseHelper.KEY_FILE_NAME));
            int fileContentIndex = cursor.getColumnIndex((DataBaseHelper.KEY_FILE_NAME));

            do {
                int id = cursor.getInt(idIndex);
                String fileNameDB = cursor.getString(fileNameIndex);
                String fileContentDB = cursor.getString(fileContentIndex);

                if (fileNameDB.equals(fileName)) {
                    sourceId = id;
                    break;
                }
            } while (cursor.moveToNext());
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_FILE_NAME, fileName);
        contentValues.put(DataBaseHelper.KEY_TEXT, fileContent);

        database.update(DataBaseHelper.TABLE_FILES, contentValues,
                DataBaseHelper.KEY_ID + "= ?", new String[] {Integer.toString(sourceId)});

        cursor.close();
        dbHelper.close();
    }

    public void DeleteFileFromDB(String fileName) {
        dbHelper = new DataBaseHelper(mainActivity);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DataBaseHelper.TABLE_FILES, null, null,
                null, null ,null, null);

        int sourceId = -1;

        if (cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex((DataBaseHelper.KEY_ID));
            int fileNameIndex = cursor.getColumnIndex((DataBaseHelper.KEY_FILE_NAME));
            int fileContentIndex = cursor.getColumnIndex((DataBaseHelper.KEY_FILE_NAME));

            do {
                int id = cursor.getInt(idIndex);
                String fileNameDB = cursor.getString(fileNameIndex);
                String fileContentDB = cursor.getString(fileContentIndex);

                if (fileNameDB.equals(fileName)) {
                    sourceId = id;
                    break;
                }
            } while (cursor.moveToNext());
        }

        if (sourceId != -1)
        database.delete(DataBaseHelper.TABLE_FILES,
                DataBaseHelper.KEY_ID + "= ?", new String[] {Integer.toString(sourceId)});

        cursor.close();
        dbHelper.close();

        files.clear();
        UploadFilesFromDB();
    }

    @NonNull
    @Override
    public FeedModelViewHolder onCreateViewHolder(final ViewGroup parent,
                                                  final int type) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_manager, parent, false);
        return new FeedModelViewHolder(v);
    }

    public void addFile(final File newFile) {
        dbHelper = new DataBaseHelper(mainActivity);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_FILE_NAME, newFile.getName().toString());
        contentValues.put(DataBaseHelper.KEY_TEXT, "");
        database.insert(DataBaseHelper.TABLE_FILES, null, contentValues);

        addExistFile(newFile);

        dbHelper.close();
    }

    public void addExistFile(final File newFile) {
        files.add(newFile);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedModelViewHolder holder,
                                 final int position) {
        final File rssFeedModel = files.get(position);

        ((TextView) holder.rssFeedView.findViewById(R.id.fileName)).setText(rssFeedModel.getName());

        holder.rssFeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.getCurrentFileId() != -1)
                    files.get(mainActivity.getCurrentFileId()).setSourceCode(((EditText) mainActivity.findViewById(R.id.editText)).getText().toString());

                ((androidx.appcompat.widget.Toolbar) mainActivity.findViewById(R.id.toolbar)).setTitle(rssFeedModel.getName());

                ((EditText) mainActivity.findViewById(R.id.editText)).getText().clear();
                ((EditText) mainActivity.findViewById(R.id.editText)).getText().append(rssFeedModel.getSourceCode());
                ((EditText) mainActivity.findViewById(R.id.editText)).setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                ((DrawerLayout) mainActivity.findViewById(R.id.drawer_layout)).closeDrawer(Gravity.LEFT, true);
                mainActivity.setCurrentFileId(position);
            }
        });

        holder.rssFeedView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater li = LayoutInflater.from(mainActivity);
                View promptsView = li.inflate(R.layout.delete_popup, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(mainActivity);
                mDialogBuilder.setView(promptsView);


                mDialogBuilder.setCancelable(false)
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String text = files.get(position).getName().toString();
                                DeleteFileFromDB(text);
                            }
                        });

                mDialogBuilder.create().show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
