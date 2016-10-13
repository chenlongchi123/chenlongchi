package com.bean;

import java.io.Serializable;

public class Account extends Passenger implements Serializable {
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Account [username=" + username + ", password=" + password
				+ ", toString()=" + super.toString() + "]";
	}



}