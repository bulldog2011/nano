package com.tpt.nano.transform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PooledDateFormatter implements Transformable<Date> {
	
	private AtomicInteger count;
	
	private TaskQueue queue;
	
	private String format;
	
	public PooledDateFormatter(String format) {
		this(format, 10);
	}
		   
	public PooledDateFormatter(String format, int count) {      
		this.count = new AtomicInteger(count);
        this.queue = new TaskQueue(count);
        this.format = format;
    }
	
	private Task borrow() throws InterruptedException {      
	    int size = count.get();
	      
	    if(size > 0) {
	    	int next = count.getAndDecrement();
	         
	        if(next > 0) {
	            return new Task(format);
	        }
	    }
	    return queue.take();
	}

	public Date read(String value) throws Exception {
		return borrow().read(value);
	}

	public String write(Date value) throws Exception {
		return borrow().write(value);
	}

    private void release(Task task) {
	   queue.offer(task);
	}
	

    private class Task {
    	private SimpleDateFormat format;
		  
    	public Task(String format) {
    		this.format = new SimpleDateFormat(format);        
		}
		  
    	public Date read(String value) throws ParseException {
    		try {
    			return format.parse(value);
		    } finally {
		        release(this);
		    }
		}
		  
		public String write(Date value) {
		     try {
		        return format.format(value);
		     } finally {
		        release(this);
		     }
		}
	} 

    private class TaskQueue extends LinkedBlockingQueue<Task> {
    	public TaskQueue(int size) {
    		super(size);
	    }
	}	
}
