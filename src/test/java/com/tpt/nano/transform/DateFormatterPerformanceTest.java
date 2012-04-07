package com.tpt.nano.transform;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import com.tpt.nano.util.ThreadLocalDateFormatter;

import junit.framework.TestCase;

public class DateFormatterPerformanceTest extends TestCase {
	
	private static final String FORMAT = "yyyy-MM-dd HH:mm:ss z";
	
	private static final int CONCURRENCY = 10;
	
	private static final int COUNT = 100;
	
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    public static boolean assertEqualDates(Date date1, Date date2) {
        String d1 = simpleDateFormat.format(date1);            
        String d2 = simpleDateFormat.format(date2);            
        return d1.equals(d2);
    }  
	
	public void testThreadLocalDateFormatter() throws Exception {
		Date date = new Date();
		
		String value = ThreadLocalDateFormatter.format(date, FORMAT);
		Date copy = ThreadLocalDateFormatter.parse(value, FORMAT);
		
		assertEqualDates(date, copy);
	}
	
	public void testPooledDateFormatter() throws Exception {
	    PooledDateFormatter formatter = new PooledDateFormatter(FORMAT);
	    Date date = new Date();
	      
	    String value = formatter.write(date);
	    Date copy = formatter.read(value);
	      
	    assertEqualDates(date, copy);   
	}
	
	public void testPerformance() throws Exception {
		CountDownLatch simpleDateFormatGate = new CountDownLatch(CONCURRENCY);
	    CountDownLatch simpleDateFormatFinisher = new CountDownLatch(CONCURRENCY);
	    AtomicLong simpleDateFormatCount = new AtomicLong();
	      
	    for(int i = 0; i < CONCURRENCY; i++) {
	    	new Thread(new SimpleDateFormatTask(simpleDateFormatFinisher, simpleDateFormatGate, simpleDateFormatCount, FORMAT)).start();
	    }
	    simpleDateFormatFinisher.await();
	    
	    CountDownLatch synchronizedGate = new CountDownLatch(CONCURRENCY);
	    CountDownLatch synchronizedFinisher = new CountDownLatch(CONCURRENCY);
	    AtomicLong synchronizedCount = new AtomicLong();
	    SimpleDateFormat format = new SimpleDateFormat(FORMAT);
	      
	    for(int i = 0; i < CONCURRENCY; i++) {
	    	new Thread(new SynchronizedTask(synchronizedFinisher, synchronizedGate, synchronizedCount, format)).start();
	    }           
	    synchronizedFinisher.await();
	    
	    CountDownLatch formatterGate = new CountDownLatch(CONCURRENCY);
	    CountDownLatch formatterFinisher = new CountDownLatch(CONCURRENCY);
	    AtomicLong formatterCount = new AtomicLong();
	    PooledDateFormatter formatter = new PooledDateFormatter(FORMAT, CONCURRENCY);
	      
	    for(int i = 0; i < CONCURRENCY; i++) {
	    	new Thread(new PooledFormatterTask(formatterFinisher, formatterGate, formatterCount, formatter)).start();
	    }
	    formatterFinisher.await();
	    
	    CountDownLatch threadLocalGate = new CountDownLatch(CONCURRENCY);
	    CountDownLatch threadLocalFinisher = new CountDownLatch(CONCURRENCY);
	    AtomicLong threadLocalCount = new AtomicLong();
	      
	    for(int i = 0; i < CONCURRENCY; i++) {
	    	new Thread(new ThreadLocalFormatterTask(threadLocalFinisher, threadLocalGate, threadLocalCount, FORMAT)).start();
	    }
	    threadLocalFinisher.await();
	    
	    System.out.printf("thread-local: %s, pool: %s, new: %s, synchronized: %s", threadLocalCount.get(), formatterCount.get(), simpleDateFormatCount.get(), synchronizedCount.get());	    
	}
	
    private class ThreadLocalFormatterTask implements Runnable {
	      
	      private String format;
	      
	      private CountDownLatch gate;
	      
	      private CountDownLatch main;
	      
	      private AtomicLong count;

	      public ThreadLocalFormatterTask(CountDownLatch main, CountDownLatch gate, AtomicLong count, String format) {         
	         this.format = format;
	         this.count = count;
	         this.gate = gate;
	         this.main = main;
	      }
	      
	      public void run() {
	         long start = System.currentTimeMillis();
	         
	         try {
	            gate.countDown();
	            gate.await();
	            
	            Date date = new Date();
	            
	            for(int i = 0; i < COUNT; i++) {
	               String value = ThreadLocalDateFormatter.format(date, format);
	               Date copy = ThreadLocalDateFormatter.parse(value, format);

	               assertEqualDates(date, copy);
	            }
	         }catch(Exception e) {
	            assertTrue(false);
	         } finally {
	            count.getAndAdd(System.currentTimeMillis() - start);
	            main.countDown();
	         }
	      }
	}
	
    private class PooledFormatterTask implements Runnable {
	      
	      private PooledDateFormatter formatter;
	      
	      private CountDownLatch gate;
	      
	      private CountDownLatch main;
	      
	      private AtomicLong count;

	      public PooledFormatterTask(CountDownLatch main, CountDownLatch gate, AtomicLong count, PooledDateFormatter formatter) {         
	         this.formatter = formatter;
	         this.count = count;
	         this.gate = gate;
	         this.main = main;
	      }
	      
	      public void run() {
	         long start = System.currentTimeMillis();
	         
	         try {
	            gate.countDown();
	            gate.await();
	            
	            Date date = new Date();
	            
	            for(int i = 0; i < COUNT; i++) {
	               String value = formatter.write(date);
	               Date copy = formatter.read(value);

	               assertEqualDates(date, copy);
	            }
	         }catch(Exception e) {
	            assertTrue(false);
	         } finally {
	            count.getAndAdd(System.currentTimeMillis() - start);
	            main.countDown();
	         }
	      }
	}
	
	private class SimpleDateFormatTask implements Runnable {
		
		private CountDownLatch gate;
		private CountDownLatch main;
		private AtomicLong count;
		private String format;
		
	    public SimpleDateFormatTask(CountDownLatch main, CountDownLatch gate, AtomicLong count, String format) {
	    	this.format = format;
	        this.count = count;
	        this.gate = gate;
	        this.main = main;
	    }		

		public void run() {
			long start = System.currentTimeMillis();
	         
	        try {
	            gate.countDown();
	            gate.await();
	             
	            Date date = new Date();
	            
	            for(int i = 0; i < COUNT; i++) {
	               String value = new SimpleDateFormat(format).format(date);
	               Date copy = new SimpleDateFormat(format).parse(value);
	               
	               assertEqualDates(date, copy);
	            }
	         }catch(Exception e) {
	            assertTrue(false);
	         } finally {
	            count.getAndAdd(System.currentTimeMillis() - start);
	            main.countDown();
	         }
			
		}
		
	}
	
	private class SynchronizedTask implements Runnable {
	      
	      private SimpleDateFormat format;
	      
	      private CountDownLatch gate;
	      
	      private CountDownLatch main;
	      
	      private AtomicLong count;

	      public SynchronizedTask(CountDownLatch main, CountDownLatch gate, AtomicLong count, SimpleDateFormat format) {
	         this.format = format;
	         this.count = count;
	         this.gate = gate;
	         this.main = main;
	      }
	      
	      public void run() {
	         long start = System.currentTimeMillis();
	         
	         try {
	            gate.countDown();
	            gate.await();
	             
	            Date date = new Date();
	            
	            for(int i = 0; i < COUNT; i++) {
	               synchronized(format) {
	                  String value = format.format(date);
	                  Date copy = format.parse(value);
	               
	                  assertEqualDates(date, copy);
	               }
	            }
	         }catch(Exception e) {
	            assertTrue(false);
	         } finally {
	            count.getAndAdd(System.currentTimeMillis() - start);
	            main.countDown();
	         }
	      }
	   }	

}
