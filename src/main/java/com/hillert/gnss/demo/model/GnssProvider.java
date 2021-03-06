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
public enum GnssProvider {

	/**
	 * Represents the Global Positioning System (USA).
	 */
	GPS("GP", "GPS"),

	/**
	 * Represents the GLObal NAvigation Satellite System (Russia).
	 */
	GLONASS("GL", "GLONASS"),

	/**
	 * Represents the global navigation satellite system of the EU.
	 */
	GALILEO("GA", "Galileo"),

	/**
	 * Represents the global navigation satellite system of China.
	 */
	BAIDOU("GB", "BeiDou");

	private String id;
	private String name;

	GnssProvider(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public static GnssProvider fromKey(String id) {

		Assert.hasText(id, "Parameter id must not be null or empty.");

		for (GnssProvider gnssProvider : GnssProvider.values()) {
			if (gnssProvider.getId().equals(id)) {
				return gnssProvider;
			}
		}

		throw new IllegalArgumentException("Unable to gnssProvider " + id);

	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

}
