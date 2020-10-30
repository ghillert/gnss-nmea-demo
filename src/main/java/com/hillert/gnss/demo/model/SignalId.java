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

package com.hillert.gnss.demo.model;

import org.springframework.util.Assert;

/**
 * Defines Bluetooth Service Classes supported by the application.
 *
 * @author Gunnar Hillert
 * @see <a href="https://www.bluetooth.com/specifications/assigned-numbers/service-discovery">
 *   https://www.bluetooth.com/specifications/assigned-numbers/service-discovery</a>
 *
 */
public enum SignalId {

	GPS_L1(GnssProvider.GPS , 1, "GPS L1C/A"),
	GPS_L2CL(GnssProvider.GPS , 6, "GPS L2 CL"),
	GPS_L2CM(GnssProvider.GPS , 5, "GPS L2 CM"),

	GALILEO_E1(GnssProvider.GALILEO , 7, "Galileo E1"),
	GALILEO_E5 (GnssProvider.GALILEO , 2, "Galileo E5"),

	BAIDOU_B1I(GnssProvider.BAIDOU , 1, "BeiDou B1I"),
	BAIDOU_B2I(GnssProvider.BAIDOU , 11, "BeiDou B2I"),

	GLONASS_L1(GnssProvider.GLONASS , 1, "GLONASS L1 OF"),
	GLONASS_L2(GnssProvider.GLONASS , 3, "GLONASS L2 OF");

	private GnssProvider gnssProvider;
	private Integer signalId;
	private String name;

	SignalId(GnssProvider gnssProvider, Integer signalId, String name) {
		this.gnssProvider = gnssProvider;
		this.signalId = signalId;
		this.name = name;
	}

	public static SignalId fromKey(GnssProvider gnssProvider, Integer signalIdToConvert) {

		Assert.notNull(gnssProvider, "Parameter gnssProvider must not be null.");
		//Assert.notNull(signalIdToConvert, "Parameter signalIdToConvert must not be null.");

		for (SignalId signalId : SignalId.values()) {
			if (signalId.getGnssProvider().equals(gnssProvider) &&
				signalId.getSignalId().equals(signalIdToConvert)) {
				return signalId;
			}
		}
		return null;
//		throw new IllegalArgumentException(String.format("Unable to find SignalId for gnssProvider %s and signalId %s.",
//				gnssProvider, signalIdToConvert));

	}

	public String getName() {
		return this.name;
	}

	public GnssProvider getGnssProvider() {
		return gnssProvider;
	}

	public Integer getSignalId() {
		return signalId;
	}
}
