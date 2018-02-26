package com.ibm.dscoc.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Test {
	
	private String name;
	private String location;	
	
	public Test() {
		super();		
	}
	public Test(String name, String location) {
		this.name = name;
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

}
