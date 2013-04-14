package com.leansoft.nano.sample.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
	
	private Random r = new Random();
	private List<Character> chars = initChars();
	public Organization getOrganization() {
		Organization org = new Organization(getWord("Org", 5), getAddress());
		int N = getInt(25, 150);
		for (int k = 0; k < N; ++k) {
			org.add(getPerson());
		}
		org.setCount(N);
		return org;
	}
	
	public Person getPerson() {
		return new Person(getWord("name", 4), getAddress());
	}
	
	public Address getAddress() {
		return new Address(getWord("Street", 4), getWord(5), getWord("City", 3));
	}
	
	public int getInt(int lb, int ub) {
		return lb + r.nextInt(ub - lb);
	}

	public String getWord(String prefix, int count) {
		return prefix + ' ' + getWord(count);
	}
	
	public String getWord(int count) {
		StringBuffer buf = new StringBuffer(count);
		for(int k = 1; k <= count; ++k) {
			buf.append(nextChar());
		}
		return buf.toString();
	}
	
	char nextChar() {
		return chars.get(r.nextInt(chars.size()));
	}
	
	List<Character> initChars() {
		List<Character> chars = new ArrayList<Character>();
		chars.addAll(generate('A', 'Z'));
		chars.addAll(generate('0', '9'));
		chars.addAll(generate('A', 'Z'));
		return chars;
	}
	
	List<Character> generate(char lb, char ub) {
		List<Character> result = new ArrayList<Character>();
		for(char ch = lb; ch <= ub; ch++) {
			result.add(ch);
		}
		return result;
	}
}
