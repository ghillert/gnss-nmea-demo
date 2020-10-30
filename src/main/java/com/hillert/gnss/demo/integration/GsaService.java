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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import com.hillert.gnss.demo.store.GnssStatusStore;

import net.sf.marineapi.nmea.sentence.GSASentence;

/**
 * Processes {@link GSASentence}s.
 *
 * @author Gunnar Hillert
 *
 */
public class GsaService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GsaService.class);

	private final GnssStatusStore gnssStatusStore;

	public GsaService(GnssStatusStore gnssStatusStore) {
		super();
		this.gnssStatusStore = gnssStatusStore;
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
	public void process(Message<GSASentence> message) {
		final GSASentence sentence = message.getPayload();
		this.gnssStatusStore.getGnssStatus().setGpsFixStatus(SentenceUtils.handleNmeaData(sentence::getFixStatus));
	}
}
