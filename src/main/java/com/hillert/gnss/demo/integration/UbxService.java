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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import com.google.common.util.concurrent.AtomicDouble;
import com.hillert.gnss.demo.store.GnssStatusStore;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import net.sf.marineapi.nmea.sentence.UBXSentence;
import net.sf.marineapi.ublox.message.UBXMessage00;
import net.sf.marineapi.ublox.message.UBXMessage03;
import net.sf.marineapi.ublox.message.parser.UBXMessage00Parser;
import net.sf.marineapi.ublox.message.parser.UBXMessage03Parser;
import net.sf.marineapi.ublox.util.UbloxNavigationStatus;
import net.sf.marineapi.ublox.util.UbloxSatelliteInfo;

/**
 * Processes {@link UBXSentence}s.
 *
 * @author Gunnar Hillert
 *
 */
public class UbxService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UbxService.class);

	private final GnssStatusStore gnssStatusStore;

	public UbxService(GnssStatusStore gnssStatusStore) {
		super();
		this.gnssStatusStore = gnssStatusStore;
	}

	private AtomicDouble horizontalAccuracyGauge = Metrics.gauge("nmea.accuracy.horizontal", new AtomicDouble(0));
	private AtomicDouble verticalAccuracyGauge = Metrics.gauge("nmea.accuracy.vertical", new AtomicDouble(0));

	/**
	 * Handles the proprietary {@link UBXSentence}.
	 */
	public void process(Message<UBXSentence> message) {
		final UBXSentence sentence = message.getPayload();

		if (sentence.getMessageId().equals(3)) {
			final UBXMessage03 ubxMessage00 = new UBXMessage03Parser(sentence);
			final List<UbloxSatelliteInfo> satelliteInfos = ubxMessage00.getSatellites();
			Metrics.gaugeCollectionSize("nmea.satellites", Tags.empty(), satelliteInfos);
		}
		if (sentence.getMessageId().equals(0)) {
			final UBXMessage00 ubxMessage00 = new UBXMessage00Parser(sentence);

			final double horizontalAccuracyEstimate = ubxMessage00.getHorizontalAccuracyEstimate();
			final double verticaAccuracyEstimate = ubxMessage00.getVerticaAccuracyEstimate();

			this.gnssStatusStore.getGnssStatus().setUbloxHorizontalAccuracyInMeters(horizontalAccuracyEstimate);
			this.gnssStatusStore.getGnssStatus().setUbloxVerticalAccuracyInMeters(verticaAccuracyEstimate);

			horizontalAccuracyGauge.set(horizontalAccuracyEstimate);
			verticalAccuracyGauge.set(verticaAccuracyEstimate);

			final UbloxNavigationStatus navigationalStatus = ubxMessage00.getNavigationStatus();
			Metrics.gauge("nmea.status", Tags.empty(), navigationalStatus.ordinal());
			LOGGER.info(
					"HorizontalAccuracyEstimate: {} | VerticaAccuracyEstimate {} | Nav Status: {}",
					horizontalAccuracyEstimate,
					verticaAccuracyEstimate,
					ubxMessage00.getNavigationStatus());
		}
	}
}
