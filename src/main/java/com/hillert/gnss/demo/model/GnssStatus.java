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

	private Double altitude;
	private Double longitude;
	private Double latitude;

	private GpsFixQuality fixQuality;
	private Map<GnssProvider, Integer> satelliteCount = new ConcurrentHashMap<>();
	private GpsFixStatus gpsFixStatus;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((altitude == null) ? 0 : altitude.hashCode());
		result = prime * result + ((fixQuality == null) ? 0 : fixQuality.hashCode());
		result = prime * result + ((gpsFixStatus == null) ? 0 : gpsFixStatus.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((satelliteCount == null) ? 0 : satelliteCount.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GnssStatus other = (GnssStatus) obj;
		if (altitude == null) {
			if (other.altitude != null)
				return false;
		} else if (!altitude.equals(other.altitude))
			return false;
		if (fixQuality != other.fixQuality)
			return false;
		if (gpsFixStatus != other.gpsFixStatus)
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (satelliteCount == null) {
			if (other.satelliteCount != null)
				return false;
		} else if (!satelliteCount.equals(other.satelliteCount))
			return false;
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
