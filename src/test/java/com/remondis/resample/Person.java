package com.remondis.resample;

import java.time.LocalDate;

public class Person {

	private String name;
	private String forname;
	private int age;
	private LocalDate brithday;

	public Person() {
		super();
	}

	public Person(String name, String forname, int age, LocalDate brithday) {
		super();
		this.name = name;
		this.forname = forname;
		this.age = age;
		this.brithday = brithday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getForname() {
		return forname;
	}

	public void setForname(String forname) {
		this.forname = forname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public LocalDate getBrithday() {
		return brithday;
	}

	public void setBrithday(LocalDate brithday) {
		this.brithday = brithday;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", forname=" + forname + ", age=" + age + ", brithday=" + brithday + "]";
	}
}
