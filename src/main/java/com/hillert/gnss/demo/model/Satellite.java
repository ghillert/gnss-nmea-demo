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

public class Satellite implements Comparable<Satellite>{

	private final GnssProvider gnssProvider;
	private final String id;
	private final int elevation;
	private final int azimuth;
	private final int noise;

	public Satellite(GnssProvider gnssProvider, String id, int elevation, int azimuth, int noise) {
		super();
		this.gnssProvider = gnssProvider;
		this.id = id;
		this.elevation = elevation;
		this.azimuth = azimuth;
		this.noise = noise;
	}

	public GnssProvider getGnssProvider() {
		return gnssProvider;
	}

	public String getId() {
		return id;
	}

	public int getElevation() {
		return elevation;
	}

	public int getAzimuth() {
		return azimuth;
	}

	public int getNoise() {
		return noise;
	}

	@Override
	public int compareTo(Satellite otherSatellite) {
		return this.getId().compareTo(otherSatellite.getId());
	}

}
