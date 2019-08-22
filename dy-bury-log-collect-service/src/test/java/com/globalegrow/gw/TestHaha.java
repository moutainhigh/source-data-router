package com.globalegrow.gw;

import java.util.Calendar;

public class TestHaha {
	public static void main(String[] args) throws Exception {
		System.out.println(processTimeGroup(1533867875L));
	}
	
	private static Long processTimeGroup(Long tm) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tm*1000);

		int minute = calendar.get(Calendar.MINUTE);
		if (minute % 15 != 0) {
			int j = minute / 15;
			if (j == 0) {
				calendar.set(Calendar.MINUTE, 15);
			} else if (j == 1) {
				calendar.set(Calendar.MINUTE, 30);
			} else if (j == 2) {
				calendar.set(Calendar.MINUTE, 45);
			} else if (j == 3) {
				calendar.add(Calendar.HOUR, 1);
				calendar.set(Calendar.MINUTE, 0);
			}
		}
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}
}
