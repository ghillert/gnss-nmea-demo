package com.hillert.gnss.demo.integration;

import java.util.function.Supplier;

import net.sf.marineapi.nmea.parser.DataNotAvailableException;

public class SentenceUtils {
	/**
	 * When retrieving data from NMEA messages, the underlying library may throw
	 * a {@link DataNotAvailableException}. This helper method will catch the exception
	 * and return null, indicating that the data is not available, yet.
	 *
	 * @param <T> The data to return
	 * @param s The function to be executed
	 * @return The requested NMEA data or null
	 */
	public static <T> T handleNmeaData(Supplier<T> s) {
		T returnValue;
		try {
			returnValue = s.get();
		}
		catch (DataNotAvailableException e) {
			//e.printStackTrace();
			returnValue = null;
		}
		return returnValue;
	}

}
