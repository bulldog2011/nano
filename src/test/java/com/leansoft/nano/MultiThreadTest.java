package com.leansoft.nano;

import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.annotation.Element;

public class MultiThreadTest extends TestCase {
	
	private static final int INNER_LOOP = 10;
	private static final int OUTER_LOOP = 5;
	
	private static class Example {
		
		@Element
		private String name;
		
		@Element
		private String value;
		
		@Element
		private int number;
		
		@Element
		private Date date;
		
		@Element
		private Locale locale;
		
	}
	
	private static enum Status {
		ERROR,
		SUCCESS
	}
	
	private static class Worker extends Thread {
		private final CountDownLatch latch;
		private final IReader reader;
		private final IWriter writer;
		private final Queue<Status> queue;
		private final Example example;
		
		public Worker(CountDownLatch latch, IReader reader, IWriter writer, Queue<Status> queue, Example example) {
			this.reader = reader;
			this.writer = writer;
			this.example = example;
			this.latch = latch;
			this.queue = queue;
		}
		
		public void run() {
			try {
				latch.countDown();
				latch.await();
				for(int i = 0; i < INNER_LOOP; i++) {
					String text = writer.write(example);
					Example copy = reader.read(Example.class, text);
					assertEquals(example.name, copy.name);
		            assertEquals(example.value, copy.value);
		            assertEquals(example.number, copy.number);
		            assertEquals(example.date, copy.date);
		            assertEquals(example.locale, copy.locale);
				}
				queue.offer(Status.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
				queue.offer(Status.ERROR);
			}
		}
	}
	
	public void runTest(IReader reader, IWriter writer) throws Exception {
		CountDownLatch latch = new CountDownLatch(OUTER_LOOP);
		BlockingQueue<Status> status = new LinkedBlockingQueue<Status>();
		Example example = new Example();
		
		example.name = "Example Name";
		example.value = "Some Value";
		example.number = 10;
		example.date = new Date();
		example.locale = Locale.CHINA;
		
		for(int i = 0; i < OUTER_LOOP; i++) {
			Worker worker = new Worker(latch, reader, writer, status, example);
			worker.start();
		}
		for(int i = 0; i < OUTER_LOOP; i++) {
			assertEquals("Serialization fails when used concurrently", status.take(), Status.SUCCESS);
		}
	}
	
	public void testCurrencyXML() throws Exception {
		IReader xmlReader = NanoFactory.getXMLReader();
		IWriter xmlWriter = NanoFactory.getXMLWriter();
		runTest(xmlReader, xmlWriter);
	}
	
	public void testCurrencyJSON() throws Exception {
		IReader jsonReader = NanoFactory.getJSONReader();
		IWriter jsonWriter = NanoFactory.getJSONWriter();
		runTest(jsonReader, jsonWriter);
	}

}
