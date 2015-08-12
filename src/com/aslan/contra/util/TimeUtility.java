package com.aslan.contra.util;

import java.util.Calendar;

public class TimeUtility {
	private TimeUtility() {
	}

	/**
	 * Check given time is day or night.
	 * 
	 * @param calendar
	 * @return
	 */
	public static boolean isDayTime(Calendar calendar) {
		final int MORNING = 6;
		final int EVENING = 16;

		int start = calendar.get(Calendar.HOUR_OF_DAY);

		if (start >= MORNING && start <= EVENING) { // Day time
			return true;
		} else {
			return false;
		}
	}
}
