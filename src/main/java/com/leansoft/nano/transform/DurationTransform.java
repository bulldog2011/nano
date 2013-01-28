package com.leansoft.nano.transform;

import com.leansoft.nano.custom.types.Duration;

/**
 * Transformer between a string and custom Duration object
 * 
 * @author bulldog
 *
 */
public class DurationTransform implements Transformable<Duration> {

	public Duration read(String value) throws Exception {
		return new Duration(value);
	}

	public String write(Duration value) throws Exception {
		return value.toString();
	}

}
