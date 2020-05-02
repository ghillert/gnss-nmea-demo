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

import com.hillert.gnss.demo.model.GnssStatus;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
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

	public void process(String message) {

		final SentenceFactory sf = SentenceFactory.getInstance();
		final Sentence gsa = (Sentence) sf.createParser(message);

		final String sentenceId = gsa.getSentenceId();
		switch (SentenceId.valueOf(sentenceId)) {
			case GGA:
				final GGASentence ggaSentence = (GGASentence) gsa;
				this.gnssStatus.setLatitude(ggaSentence.getPosition().getLatitude());
				this.gnssStatus.setLongitude(ggaSentence.getPosition().getLongitude());
				this.gnssStatus.setAltitude(ggaSentence.getAltitude());
				this.gnssStatus.setFixQuality(ggaSentence.getFixQuality());
				this.gnssStatus.setSatelliteCount(ggaSentence.getSatelliteCount());
				break;
			case GSA:
				final GSASentence gsaSentence = (GSASentence) gsa;
				this.gnssStatus.setGpsFixStatus(gsaSentence.getFixStatus());
				break;
			default:
		}

		if (this.gnssStatusHashcode != this.gnssStatus.hashCode()) {
			LOGGER.info(this.gnssStatus.toString());
			this.gnssStatusHashcode = this.gnssStatus.hashCode();
		}

	}

}
