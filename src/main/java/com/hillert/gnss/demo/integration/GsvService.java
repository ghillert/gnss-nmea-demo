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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import com.hillert.gnss.demo.model.GnssProvider;
import com.hillert.gnss.demo.model.Satellite;
import com.hillert.gnss.demo.store.GnssStatusStore;
import com.hillert.gnss.demo.store.SatelliteStore;

import net.sf.marineapi.nmea.sentence.GSVSentence;
import net.sf.marineapi.nmea.util.SatelliteInfo;

/**
 * Processes {@link GSVSentence}s.
 *
 * @author Gunnar Hillert
 *
 */
public class GsvService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GsvService.class);

	private final SatelliteStore satelliteStore;
	private final GnssStatusStore gnssStatusStore;

	public GsvService(SatelliteStore satelliteStore, GnssStatusStore gnssStatusStore) {
		super();
		this.satelliteStore = satelliteStore;
		this.gnssStatusStore = gnssStatusStore;
	}

	/**
	 * Currently handles the {@link GSVSentence} (GNSS Satellites in View).
	 *
	 * @param message The GSVSentence array to process
	 */
	public void process(Message<GSVSentence[]> message) {
		GnssProvider gnssProvider = null;
		int satelliteCount = -1;
		Set<Satellite> satellites = new TreeSet<>();

		for (GSVSentence gsvSentence : message.getPayload()) {
			final GnssProvider gnssProviderFromSentence = GnssProvider.fromKey(gsvSentence.getTalkerId().name());
			if (gnssProvider != null && !gnssProvider.equals(gnssProviderFromSentence)) {
				throw new IllegalStateException("All GnssProvider in this message should be the same.");
			}
			else {
				gnssProvider = gnssProviderFromSentence;
			}

			satelliteCount = gsvSentence.getSatelliteCount();
			final List<SatelliteInfo> satelliteInfos = gsvSentence.getSatelliteInfo();

			for (SatelliteInfo satelliteInfo : satelliteInfos) {
				final String id = satelliteInfo.getId();
				final int azimuth = satelliteInfo.getAzimuth();
				final int elevation = satelliteInfo.getElevation();
				final int noise = satelliteInfo.getNoise();
				final Satellite satellite = new Satellite(gnssProvider, id, elevation, azimuth, noise);
				satellites.add(satellite);
			}

		}

		this.gnssStatusStore.getGnssStatus().getSatelliteCount().put(gnssProvider, satelliteCount);
		satelliteStore.getSatellites().put(gnssProvider, Collections.unmodifiableSet(satellites));

		this.satelliteStore.setProcessed(false);
		this.satelliteStore.getProcessedGnssProviders().add(gnssProvider);
	}
}
