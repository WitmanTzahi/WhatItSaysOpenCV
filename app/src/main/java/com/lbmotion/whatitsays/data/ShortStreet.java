package com.lbmotion.whatitsays.data;

import java.io.Serializable;

public class ShortStreet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public int code1;
	public int code2;
	public String name;
	
	@Override
	   public String toString() {
 	   	return new StringBuffer().append(code1).append(code2).append(name).toString();
	}

	public int getCode1() {
		return code1;
	}

	public void setCode1(int code1) {
		this.code1 = code1;
	}

	public int getCode2() {
		return code2;
	}

	public void setCode2(int code2) {
		this.code2 = code2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
