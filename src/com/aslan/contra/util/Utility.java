package com.aslan.contra.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class Utility {
	private Utility() {
	}

	/**
	 * Format a Sri Lankan phone number.
	 * 
	 * @param number
	 * @return
	 * @throws NumberParseException
	 */
	public static String formatPhoneNumber(String number) throws NumberParseException {
		return formatPhoneNumber("lk", number);
	}

	public static String formatPhoneNumber(String country, String number) throws NumberParseException {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		PhoneNumber phoneNumber = phoneUtil.parse(number, country);
		String formattedNumber = phoneUtil.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL);
		return formattedNumber;
	}
}
