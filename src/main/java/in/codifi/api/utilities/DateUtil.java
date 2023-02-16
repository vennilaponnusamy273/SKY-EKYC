package in.codifi.api.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateUtil {

	public static String currentTimeZone(long date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		System.out.println("Date and time in Madrid: " + df.format(date * 1000));
		return df.format(date);
	}

	public static void main(String[] args) {
		currentTimeZone(1676536092);
	}

}
