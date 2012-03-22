package com.tpt.nano.transform;

import com.tpt.nano.custom.types.Time;

/**
 * Transformer between a string and a custom Time object
 * 
 * @author bulldog
 *
 */
class TimeTransform implements Transformable<Time> {

	public Time read(String value) throws Exception {
		return new Time(value);
	}

	public String write(Time value) throws Exception {
		return value.toString();
	}

}
