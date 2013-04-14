package com.leansoft.nano.sample;

import java.io.File;
import java.io.Reader;
import java.io.Writer;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.sample.domain.Generator;
import com.leansoft.nano.sample.domain.Organization;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Organization organization;
	private TextView status;
	private String dirName = "nano-demo";
	private String fileName = "testdata.xml";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		status = (TextView) findViewById(R.id.status);
		status.setText("", TextView.BufferType.EDITABLE);
		
		Button exportButton = (Button)findViewById(R.id.exportButton);
		exportButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
			    ExportTask  task = new ExportTask(MainActivity.this, new OnCompletionListener() {
			        public void onCompletion(Object result) {
			            File file = (File) result;
			            appendStatus(String.format("Written %d bytes to %s", file.length(), file.getAbsolutePath()));
			        }
			    });
			    task.execute(organization, dirName, fileName);
				
			}
			
		});
		
		Button importButton = (Button)findViewById(R.id.importButton);
		importButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImportTask  task = new ImportTask(MainActivity.this, new OnCompletionListener() {
			        public void onCompletion(Object result) {
			            organization = (Organization) result;
			            appendStatus(String.format("Imported organization '%s' having %d persons",
			                    organization.getName(), organization.getStaff().size()));
			        }
			    });
			    task.execute(dirName, fileName);
				
			}
			
		});
		
		new DataLoader().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	class DataLoader extends AsyncTask<Void, Void, Organization> {

		private ProgressDialog progress;
		
		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(MainActivity.this, "", "Generating data ...", true, false);
		}
		
		@Override
		protected Organization doInBackground(Void... params) {
			return new Generator().getOrganization();
		}
		
		@Override
		protected void onPostExecute(Organization result) {
			progress.dismiss();
			organization = result;
			appendStatus(String.format("Organization '%s' generated, having %d persons", 
					organization.getName(), organization.getStaff().size()));
		}
		
	}
	
	class ExportTask extends AbstractTask<File> {

		protected ExportTask(Context ctx,
				OnCompletionListener listener) {
			super(ctx, "export task", listener);
		}

		@Override
		protected File doInBackground(Object... args) {
		    try {
		        Organization organization = (Organization) args[0];
		        String dirName = (String) args[1];
		        String fileName = (String) args[2];
		        File file = getFile(dirName,  fileName);
		        Writer out = openForWrite(file);
		        IWriter xmlWriter = NanoFactory.getXMLWriter();
		        xmlWriter.write(organization, out);
		        out.close();
		        return file;
		    } catch (Exception e) {
		        Log.d(LOG, "Failed to export. ", e);
		        return null;
		    }
		}
		
	}
	
	class ImportTask extends AbstractTask<Organization> {

		protected ImportTask(Context ctx,
				OnCompletionListener listener) {
			super(ctx, "import task", listener);
		}
		
		protected Organization doInBackground(Object... args) {
		    try {
		        String dirName  = (String) args[0];
		        String fileName = (String) args[1];
		        Reader in = openForRead(getFile(dirName,  fileName));
		        IReader xmlReader = NanoFactory.getXMLReader();
		        Organization org = xmlReader.read(Organization.class, in);
		        in.close();
		        return org;
		    } catch (Exception e) {
		        Log.d(LOG, "Failed to import. ", e);
		        return null;
		    }
		}
		
	}
	
	void appendStatus(String msg) {
		Editable content = (Editable) status.getText();
		content.append(msg).append("\n");
	}
	
}
