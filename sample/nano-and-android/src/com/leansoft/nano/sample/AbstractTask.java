package com.leansoft.nano.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public abstract class AbstractTask<ResultType> extends
		AsyncTask<Object, Integer, ResultType> {
	
	private Context ctx;
	private OnCompletionListener listener;
	private ProgressDialog progress;
	private String title;
	protected final String LOG;
	
	protected AbstractTask(Context ctx, String title, OnCompletionListener listener) {
        this.ctx      = ctx;
        this.title    = title;
        this.listener = listener;
        LOG = this.getClass().getSimpleName();
    }
	
	@Override
    protected void onPreExecute() {
        progress = new ProgressDialog(ctx);
        progress.setTitle(title + " ...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.show();
    }
	
	@Override
    protected void onPostExecute(ResultType result) {
        progress.dismiss();
        if (listener != null) listener.onCompletion(result);
    }
    Reader openForRead(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }
    Writer openForWrite(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file));
    }
    // get file for SD card
    File getFile(String dir, String file) {
        File sdDir  = Environment.getExternalStorageDirectory();
        File appDir = new File(sdDir, dir);
        
        if (!(appDir.exists() && appDir.isDirectory() && appDir.canWrite())) {
            appDir.mkdir();
        }
        return new File(appDir, file);
    }

}
