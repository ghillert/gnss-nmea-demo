/*
 * Copyright 2020 Gunnar Hillert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hillert.gnss.demo.integration;

import java.util.function.Supplier;

import com.hillert.gnss.demo.model.GnssProvider;
import com.hillert.gnss.demo.model.GnssStatus;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.GSVSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes all Nmea messages.
 *
 * @author Gunnar Hillert
 *
 */
public class NmeaProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(NmeaProcessor.class);

	private GnssStatus gnssStatus = new GnssStatus();
	private int gnssStatusHashcode;

	public NmeaProcessor() {
		super();
		this.gnssStatusHashcode = this.gnssStatus.hashCode();
	}

	/**
	 * When retrieving data from NMEA messages, the underlying library may throw
	 * a {@link DataNotAvailableException}. This helper method will catch the exception
	 * and return null, indicating that the data is not available, yet.
	 *
	 * @param <T> The data to return
	 * @param s The function to be executed
	 * @return The requested NMEA data or null
	 */
	public <T> T handleNmeaData(Supplier<T> s) {
		T returnValue;
		try {
			returnValue = s.get();
		}
		catch (DataNotAvailableException e) {
			e.printStackTrace();
			returnValue = null;
		}
		return returnValue;
	}

	/**
	 * Currently handles the following NMEA messages:
	 *
	 * <ul>
	 *   <li>GGA (Global positioning system fix data) - For position data
	 *   <li>GSA (GNSS DOP and Active Satellites) - GpsFixStatus
	 *   <li>GSV (GNSS Satellites in View) - Number of Satellites in view
	 *</ul>
	 *
	 * Note: The GGL message (Latitude and longitude, with time of position fix and status)
	 * would be technically more appropriate compared to the GGA message, but the
	 * GGA message also provides altitude information.
	 *
	 * @param message The NMEA to process, only GGA, GSA, GSV - other messages will be ignored
	 */
	public void process(String message) {

		final SentenceFactory sf = SentenceFactory.getInstance();
		final Sentence sentence = (Sentence) sf.createParser(message);

		final String sentenceId = sentence.getSentenceId();
		switch (SentenceId.valueOf(sentenceId)) {
			case GGA:
				final GGASentence ggaSentence = (GGASentence) sentence;
				final Position position = handleNmeaData(ggaSentence::getPosition);

				if (position != null) {
					final Double latitude = position.getLatitude();
					final Double longitude = position.getLongitude();
					this.gnssStatus.setLatitude(latitude);
					this.gnssStatus.setLongitude(longitude);
				}

				final Double altitude = handleNmeaData(ggaSentence::getAltitude);
				final GpsFixQuality fixQuality = handleNmeaData(ggaSentence::getFixQuality);
				this.gnssStatus.setAltitude(altitude);
				this.gnssStatus.setFixQuality(fixQuality);
				break;
			case GSA:
				final GSASentence gsaSentence = (GSASentence) sentence;
				this.gnssStatus.setGpsFixStatus(handleNmeaData(gsaSentence::getFixStatus));
				break;
			case GSV:
				final GSVSentence gsvSentence = (GSVSentence) sentence;
				final GnssProvider gnssProvider = GnssProvider.fromKey(gsvSentence.getTalkerId().name());
				this.gnssStatus.getSatelliteCount().put(gnssProvider, gsvSentence.getSatelliteCount());
				break;
			default:
		}

		if (this.gnssStatusHashcode != this.gnssStatus.hashCode()) {
			LOGGER.info(this.gnssStatus.toString());
			this.gnssStatusHashcode = this.gnssStatus.hashCode();
		}

	}

}
