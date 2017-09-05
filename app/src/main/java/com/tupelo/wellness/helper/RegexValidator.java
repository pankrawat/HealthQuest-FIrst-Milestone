package com.tupelo.wellness.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator {

	private Pattern pattern;
	private Matcher matcher;
	
	
	public boolean userNameValidator(String user_name)
	{
		String userNamePattern="^[A-Za-z]+[0-9]*+([_]*[A-Za-z0-9]+)*$";
		pattern= Pattern.compile(userNamePattern);
		matcher=pattern.matcher(user_name);
		return matcher.matches();
	}
	
}
