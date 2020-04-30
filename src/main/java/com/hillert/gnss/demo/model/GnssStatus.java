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

import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.GpsFixStatus;

/**
*
* @author Gunnar Hillert
*
*/
public class GnssStatus {

	private double altitude;
	private GpsFixQuality fixQuality;
	private int satelliteCount;
	private GpsFixStatus gpsFixStatus;

	public double getAltitude() {
		return this.altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public GpsFixQuality getFixQuality() {
		return this.fixQuality;
	}
	public void setFixQuality(GpsFixQuality fixQuality) {
		this.fixQuality = fixQuality;
	}
	public int getSatelliteCount() {
		return this.satelliteCount;
	}
	public void setSatelliteCount(int satelliteCount) {
		this.satelliteCount = satelliteCount;
	}
	public GpsFixStatus getGpsFixStatus() {
		return this.gpsFixStatus;
	}
	public void setGpsFixStatus(GpsFixStatus gpsFixStatus) {
		this.gpsFixStatus = gpsFixStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.altitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((this.fixQuality == null) ? 0 : this.fixQuality.hashCode());
		result = prime * result + ((this.gpsFixStatus == null) ? 0 : this.gpsFixStatus.hashCode());
		result = prime * result + this.satelliteCount;
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
		if (Double.doubleToLongBits(this.altitude) != Double.doubleToLongBits(other.altitude)) {
			return false;
		}
		if (this.fixQuality != other.fixQuality) {
			return false;
		}
		if (this.gpsFixStatus != other.gpsFixStatus) {
			return false;
		}
		if (this.satelliteCount != other.satelliteCount) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GnssStatus [altitude=");
		builder.append(this.altitude);
		builder.append(", fixQuality=");
		builder.append(this.fixQuality);
		builder.append(", satelliteCount=");
		builder.append(this.satelliteCount);
		builder.append(", gpsFixStatus=");
		builder.append(this.gpsFixStatus);
		builder.append("]");
		return builder.toString();
	}

}
