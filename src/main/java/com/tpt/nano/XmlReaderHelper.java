package com.tpt.nano;

import com.tpt.nano.util.FastStack;

/**
 * Helper class for XmlReader
 * 
 * @author bulldog
 *
 */
class XmlReaderHelper {
	
	public int depth = 0;
	
	public StringBuilder textBuilder = new StringBuilder();
	
	public FastStack<Object> valueStack = new FastStack<Object>(5);
	
	public boolean isRoot() {
		return valueStack.size() == 1 && depth == 1;
	}
	
	public void clearTextBuffer() {
		int length = textBuilder.length();
		this.textBuilder.delete(0, length);
	}
}
