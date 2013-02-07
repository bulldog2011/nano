package com.leansoft.nano.benchmark;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.dom.DOMParser;
import com.leansoft.nano.person.PersonListType;
import com.leansoft.nano.pull.PullParser;
import com.leansoft.nano.sax.SAXParser;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Handler mHandler = new Handler();
	
	private ProgressDialog pgDialog;
	
	private static final int LOOP = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final RadioGroup threadNumberRadioGroup = (RadioGroup) findViewById(R.id.threads);
		final RadioGroup recordSizeRadioGroup = (RadioGroup) findViewById(R.id.size);
		final RadioGroup testTypeRadioGroup = (RadioGroup) findViewById(R.id.type);
		
		final Button goButton = (Button) findViewById(R.id.do_test);
		goButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pgDialog = new ProgressDialog(MainActivity.this);
				pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pgDialog.setTitle("Running...");
				pgDialog.setIndeterminate(false);
				pgDialog.setCancelable(false);
				pgDialog.show();
				
				final int threadId = threadNumberRadioGroup.getCheckedRadioButtonId();
				final int sizeId = recordSizeRadioGroup.getCheckedRadioButtonId();
				final int typeId = testTypeRadioGroup.getCheckedRadioButtonId();
				
				// must start a thread as each takes too long on the UI thread
				new Thread() {
					
					public void run() {
					
						int threadNumber = 1;
						switch (threadId) {
						case R.id.one : 
							threadNumber = 1;
							break;
						case R.id.three :
							threadNumber = 3;
							break;
						case R.id.five :
							threadNumber = 5;
							break;
						}
						
						CountDownLatch latch = new CountDownLatch(threadNumber);
						
						int recordNumber = 10;
						switch (sizeId) {
						case R.id.small:
							recordNumber = 10;
							break;
						case R.id.medium:
							recordNumber = 50;
							break;
						case R.id.large:
							recordNumber = 300;
							break;
						}
						
						BlockingQueue<Result> results = new LinkedBlockingQueue<Result>();
						for(int i = 0; i < threadNumber; i++) {
							Runner runner = null;
							if (typeId == R.id.nano_xml_read) {
								runner = new NanoXmlReadRunner(latch, results, recordNumber + ".xml", LOOP);
							} else if (typeId == R.id.nano_xml_write) {
								runner = new NanoXmlWriteRunner(latch, results, recordNumber + ".xml", LOOP);
							} else if (typeId == R.id.nano_json_read) {
								runner = new NanoJsonReadRunner(latch, results, recordNumber + ".json", LOOP);
							} else if (typeId == R.id.raw_sax_read) {
								runner = new SAXReadRunner(latch, results, recordNumber + ".xml", LOOP);
							} else if (typeId == R.id.raw_dom_read) {
								runner = new DOMReadRunner(latch, results, recordNumber + ".xml", LOOP);
							} else if (typeId == R.id.raw_pull_read) {
								runner = new PullReadRunner(latch, results, recordNumber + ".xml", LOOP);
							} else {
								runner = new NanoJsonWriteRunner(latch, results, recordNumber + ".json", LOOP);
							}
							runner.start();
						}
						
						final String report = getReport(results, threadNumber, recordNumber);
						Log.v("Nano_benchmark", report);
						
						pgDialog.dismiss();
						
						mHandler.post(new Runnable() {
							public void run() {
								Toast.makeText(MainActivity.this, report, Toast.LENGTH_LONG).show();
							}
						});				
					}
					
				}.start();
				
			}
		});
		
	}
	
	private String getReport(BlockingQueue<Result> results, int threadNumber, int expectedRecordNumber) {
		boolean hasError = false;
		String errorMessage = null;
		int recordNumber = -1;
		long duration = -1;
		for(int i = 0; i < threadNumber; i++) {
			Result result = null;
			try {
				result = results.take();
			} catch (InterruptedException e) {
				hasError = true;
				errorMessage = "error to take result from result queue";
				break;
			}
			if (result.hasError) {
				hasError = true;
				errorMessage = "testing failure";
				break;
			}
			recordNumber = result.recordNumber;
			if (result.recordNumber != expectedRecordNumber) {
				hasError = true;
				errorMessage = "record number reported by threads are not equal to expected record number";
				break;
			}
			duration += result.duration;
		}
		
		long averageDuration = (long) (duration / (LOOP * (double)threadNumber));
		
		String message = null;
		if (hasError) {
			message = "error to handle records, error messsage : " + errorMessage;
		} else {
			message = "Handled " + recordNumber + " recoreds during testing that took " + averageDuration + " ms on average";
		}
		
		return message;
	}
	
	
	private static class Result {
		public boolean hasError = false;
		public long duration = 0;
		public int recordNumber = 0;
	}
	
	private abstract class Runner extends Thread {
		private final CountDownLatch latch;
		private final Queue<Result> queue;
		private final String fileName;
		private final int loop;
		
		public Runner(CountDownLatch latch, Queue<Result> queue, String fileName, int loop) {
			this.latch = latch;
			this.queue = queue;
			this.fileName = fileName;
			this.loop = loop;
		}
		
		public void run() {
			try {
				Result result = doRun();
				this.queue.offer(result);
			} catch (Exception e) {
				Log.e("Nano_benchmark", "unmarshalling failed.", e);
				Result result = new Result();
				result.hasError = true;
				this.queue.offer(result);
			}
		}
		
		protected abstract Result doRun() throws Exception;
		
	}
	
	private abstract class NanoReadRunner extends Runner {
		
		private final IReader reader;

		public NanoReadRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop, IReader reader) {
			super(latch, queue, fileName, loop);
			this.reader = reader;
		}
		
		@Override
		protected Result doRun() throws Exception {
			super.latch.countDown();
			super.latch.await();
			long startTime = System.currentTimeMillis();
			PersonListType persons = null;
			for(int i = 0; i < super.loop; i++) {
				InputStream is = getAssets().open(super.fileName);
				BufferedInputStream bis = new BufferedInputStream(is, 4 * 1024);
				persons = reader.read(PersonListType.class, bis);
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			Result result = new Result();
			result.duration = duration;
			result.recordNumber = persons.getPerson().size();
			return result;
		}
		
	}
	
	private class SAXReadRunner extends Runner {

		public SAXReadRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop) {
			super(latch, queue, fileName, loop);
		}

		@Override
		protected Result doRun() throws Exception {
			super.latch.countDown();
			super.latch.await();
			long startTime = System.currentTimeMillis();
			PersonListType persons = null;
			for(int i = 0; i < super.loop; i++) {
				InputStream is = getAssets().open(super.fileName);
				BufferedInputStream bis = new BufferedInputStream(is, 4 * 1024);
				persons = SAXParser.parse(bis);
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			Result result = new Result();
			result.duration = duration;
			result.recordNumber = persons.getPerson().size();
			return result;
		}
		
	}
	
	
	private class DOMReadRunner extends Runner {

		public DOMReadRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop) {
			super(latch, queue, fileName, loop);
		}

		@Override
		protected Result doRun() throws Exception {
			super.latch.countDown();
			super.latch.await();
			long startTime = System.currentTimeMillis();
			PersonListType persons = null;
			for(int i = 0; i < super.loop; i++) {
				InputStream is = getAssets().open(super.fileName);
				BufferedInputStream bis = new BufferedInputStream(is, 4 * 1024);
				persons = DOMParser.parse(bis);
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			Result result = new Result();
			result.duration = duration;
			result.recordNumber = persons.getPerson().size();
			return result;
		}
		
	}
	
	private class PullReadRunner extends Runner {

		public PullReadRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop) {
			super(latch, queue, fileName, loop);
		}

		@Override
		protected Result doRun() throws Exception {
			super.latch.countDown();
			super.latch.await();
			long startTime = System.currentTimeMillis();
			PersonListType persons = null;
			for(int i = 0; i < super.loop; i++) {
				InputStream is = getAssets().open(super.fileName);
				BufferedInputStream bis = new BufferedInputStream(is, 4 * 1024);
				persons = PullParser.parse(bis);
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			Result result = new Result();
			result.duration = duration;
			result.recordNumber = persons.getPerson().size();
			return result;
		}
		
	}
	
	private abstract class NanoWriteRunner extends Runner {
		private final IReader reader;
		private final IWriter writer;
		
		public NanoWriteRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop, IReader reader, IWriter writer) {
			super(latch, queue, fileName, loop);
			this.writer = writer;
			this.reader = reader;
		}
		
		private PersonListType read() throws Exception {
			InputStream is = getAssets().open(super.fileName);
			PersonListType persons = this.reader.read(PersonListType.class, is);
			return persons;
		}
		
		@Override
		protected Result doRun() throws Exception {
			PersonListType persons = read();
			super.latch.countDown();
			super.latch.await();
			long startTime = System.currentTimeMillis();
			for(int i = 0; i < super.loop; i++) {
				this.writer.write(persons, new BufferedWriter(new StringWriter(), 1024 * 4));
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			Result result = new Result();
			result.duration = duration;
			result.recordNumber = persons.getPerson().size();
			return result;
		}
		
	}
	
	private class NanoXmlReadRunner extends NanoReadRunner {

		public NanoXmlReadRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop) {
			super(latch, queue, fileName, loop, NanoFactory.getXMLReader());
		}
		
	}
	
	private class NanoJsonReadRunner extends NanoReadRunner {

		public NanoJsonReadRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop) {
			super(latch, queue, fileName, loop, NanoFactory.getJSONReader());
		}
		
	}
	
	private class NanoXmlWriteRunner extends NanoWriteRunner {

		public NanoXmlWriteRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop) {
			super(latch, queue, fileName, loop, NanoFactory.getXMLReader(), NanoFactory.getXMLWriter());
		}
		
	}
	
	private class NanoJsonWriteRunner extends NanoWriteRunner {

		public NanoJsonWriteRunner(CountDownLatch latch, Queue<Result> queue,
				String fileName, int loop) {
			super(latch, queue, fileName, loop, NanoFactory.getJSONReader(), NanoFactory.getJSONWriter());
		}
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
