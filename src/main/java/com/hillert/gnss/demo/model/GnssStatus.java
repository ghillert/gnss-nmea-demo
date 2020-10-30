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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.GpsFixStatus;

/**
* Basic status information of the GNSS (GPS, Galileo etc.) data.
*
* @author Gunnar Hillert
*
*/
public class GnssStatus {

	private volatile Double altitude;
	private volatile Double longitude;
	private volatile Double latitude;

	private volatile GpsFixQuality fixQuality;
	private volatile Map<GnssProvider, Integer> satelliteCount = new ConcurrentHashMap<>();
	private volatile GpsFixStatus gpsFixStatus;

	private volatile Double calculatedHorizontalAccuracyInMeters;
	private volatile Double ubloxHorizontalAccuracyInMeters;
	private volatile Double ubloxVerticalAccuracyInMeters;

	public Double getAltitude() {
		return this.altitude;
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
	public GpsFixQuality getFixQuality() {
		return this.fixQuality;
	}
	public void setFixQuality(GpsFixQuality fixQuality) {
		this.fixQuality = fixQuality;
	}
	public Map<GnssProvider, Integer> getSatelliteCount() {
		return this.satelliteCount;
	}
	public void setSatelliteCount(Map<GnssProvider, Integer> satelliteCount) {
		this.satelliteCount = satelliteCount;
	}
	public GpsFixStatus getGpsFixStatus() {
		return this.gpsFixStatus;
	}
	public void setGpsFixStatus(GpsFixStatus gpsFixStatus) {
		this.gpsFixStatus = gpsFixStatus;
	}
	public Double getLongitude() {
		return this.longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return this.latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getCalculatedHorizontalAccuracyInMeters() {
		return calculatedHorizontalAccuracyInMeters;
	}
	public void setCalculatedHorizontalAccuracyInMeters(Double calculatedHorizontalAccuracyInMeters) {
		this.calculatedHorizontalAccuracyInMeters = calculatedHorizontalAccuracyInMeters;
	}
	public Double getUbloxHorizontalAccuracyInMeters() {
		return ubloxHorizontalAccuracyInMeters;
	}
	public void setUbloxHorizontalAccuracyInMeters(Double ubloxHorizontalAccuracyInMeters) {
		this.ubloxHorizontalAccuracyInMeters = ubloxHorizontalAccuracyInMeters;
	}
	public Double getUbloxVerticalAccuracyInMeters() {
		return ubloxVerticalAccuracyInMeters;
	}
	public void setUbloxVerticalAccuracyInMeters(Double ubloxVerticalAccuracyInMeters) {
		this.ubloxVerticalAccuracyInMeters = ubloxVerticalAccuracyInMeters;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.altitude == null) ? 0 : this.altitude.hashCode());
		result = prime * result + ((this.fixQuality == null) ? 0 : this.fixQuality.hashCode());
		result = prime * result + ((this.gpsFixStatus == null) ? 0 : this.gpsFixStatus.hashCode());
		result = prime * result + ((this.latitude == null) ? 0 : this.latitude.hashCode());
		result = prime * result + ((this.longitude == null) ? 0 : this.longitude.hashCode());
		result = prime * result + ((this.satelliteCount == null) ? 0 : this.satelliteCount.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GnssStatus other = (GnssStatus) obj;
		if (this.altitude == null) {
			if (other.altitude != null) {
				return false;
			}
		}
		else if (!this.altitude.equals(other.altitude)) {
			return false;
		}
		if (this.fixQuality != other.fixQuality) {
			return false;
		}
		if (this.gpsFixStatus != other.gpsFixStatus) {
			return false;
		}
		if (this.latitude == null) {
			if (other.latitude != null) {
				return false;
			}
		}
		else if (!this.latitude.equals(other.latitude)) {
			return false;
		}
		if (this.longitude == null) {
			if (other.longitude != null) {
				return false;
			}
		}
		else if (!this.longitude.equals(other.longitude)) {
			return false;
		}
		if (this.satelliteCount == null) {
			if (other.satelliteCount != null) {
				return false;
			}
		}
		else if (!this.satelliteCount.equals(other.satelliteCount)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GnssStatus [altitude=");
		builder.append(this.altitude);
		builder.append(", longitude=");
		builder.append(this.longitude);
		builder.append(", latitude=");
		builder.append(this.latitude);
		builder.append(", fixQuality=");
		builder.append(this.fixQuality);
		builder.append(", totalSatelliteCount=");
		builder.append(getTotalSatelliteCount());
		builder.append(", gpsFixStatus=");
		builder.append(this.gpsFixStatus);
		builder.append("]");
		return builder.toString();
	}

	public int getTotalSatelliteCount() {
		int totalSatelliteCount = 0;
		for (Map.Entry<GnssProvider, Integer> satelliteCountEntry : this.satelliteCount.entrySet()) {
			if (satelliteCountEntry.getValue() != null) {
				totalSatelliteCount = totalSatelliteCount + satelliteCountEntry.getValue().intValue();
			}
		}
		return totalSatelliteCount;
	}
}
