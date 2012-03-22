package com.tpt.nano.custom.types;

/**
 * A custom type for hour:minute format time
 * 
 * @author bulldog
 *
 */
public class Time {

	private int hour;
	private int minute;
	
	public Time(String value) {
		if (value == null || value.indexOf(":") < 0) {
			throw new IllegalArgumentException("Cannot transfrom " + value + " to a Time object");
		}
		String parts[] = value.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Cannot transfrom " + value + " to a Time object");
		}
		int h = Integer.parseInt(parts[0]);
		int m = Integer.parseInt(parts[1]);
		if (h > 0 && h < 24 && m > 0 && m < 60 ) {
			this.hour = h;
			this.minute = m;
		} else {
			throw new IllegalArgumentException("Cannot transfrom " + value + " to a Time object");
		}
	}
	
	public String toString() {
		if (minute < 10) {
			return hour + ":0" + minute;
		} else {
			return hour + ":" + minute;
		}
	}
	
}
