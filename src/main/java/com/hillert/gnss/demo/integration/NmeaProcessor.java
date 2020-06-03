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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hillert.gnss.demo.model.GnssProvider;
import com.hillert.gnss.demo.model.GnssStatus;

import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GNSSentence;
import net.sf.marineapi.nmea.sentence.GSASentence;
import net.sf.marineapi.nmea.sentence.GSVSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.Position;

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

	/**
	 * When retrieving data from NMEA messages, the underlying library may throw
	 * a {@link DataNotAvailableException}. This helper method will catch the exception
	 * and return null, indicating that the data is not available, yet.
	 *
	 * @param <T> The data to return
	 * @param s The function to be executed
	 * @return The requested NMEA data or null
	 */
	public <T> T handleNmeaData(Supplier<T> s) {
		T returnValue;
		try {
			returnValue = s.get();
		}
		catch (DataNotAvailableException e) {
			e.printStackTrace();
			returnValue = null;
		}
		return returnValue;
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
	public void process(String message) {
		final SentenceFactory sf = SentenceFactory.getInstance();
		final Sentence sentence = (Sentence) sf.createParser(message);

		final String sentenceId = sentence.getSentenceId();

		if (sentenceId.startsWith("PUBX")) {
			System.out.println(">>>>>....." + sentenceId);
		}
		switch (SentenceId.valueOf(sentenceId)) {
			case GGA:
				final GGASentence ggaSentence = (GGASentence) sentence;
				final Position position = handleNmeaData(ggaSentence::getPosition);

				final Double latitude;
				final Double longitude;
				if (position != null) {
					latitude = position.getLatitude();
					longitude = position.getLongitude();

					addToCache(latitude, longitude);
				}
				else {
					latitude = null;
					longitude = null;
				}

				final Double altitude = handleNmeaData(ggaSentence::getAltitude);
				final GpsFixQuality fixQuality = handleNmeaData(ggaSentence::getFixQuality);
				this.gnssStatus.setAltitude(altitude);
				this.gnssStatus.setFixQuality(fixQuality);
				break;
			case GSA:
				final GSASentence gsaSentence = (GSASentence) sentence;
				this.gnssStatus.setGpsFixStatus(handleNmeaData(gsaSentence::getFixStatus));
				break;
			case GNS: //Number of satellites used
				final GNSSentence gnsSentence = (GNSSentence) sentence;
				System.out.println(">>>>>>>>>>>>>>>" + gnsSentence.getSatelliteCount());
				break;
			case GSV: // Satellites in view
				final GSVSentence gsvSentence = (GSVSentence) sentence;
				final GnssProvider gnssProvider = GnssProvider.fromKey(gsvSentence.getTalkerId().name());
				this.gnssStatus.getSatelliteCount().put(gnssProvider, gsvSentence.getSatelliteCount());
				break;
			default:
		}

		if (this.gnssStatusHashcode != this.gnssStatus.hashCode()) {
			LOGGER.info(this.gnssStatus.toString());
			this.gnssStatusHashcode = this.gnssStatus.hashCode();
		}

	}

	final Cache<String, Coordinate> cache = CacheBuilder.newBuilder().maximumSize(100).build();

	private void addToCache(double latitude, double longitude) {

		cache.put(String.valueOf(latitude) + "_" + String.valueOf(longitude), new Coordinate(longitude,latitude));

		if (cache.size() > 20) {
			calculateMinimumBoundingCircle(cache);
		}
	}
	private void calculateMinimumBoundingCircle(Cache<String, Coordinate> cache) {
		CoordinateReferenceSystem wgs84;
		try {
			wgs84 = CRS.decode("EPSG:4326", true);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		}
		CoordinateReferenceSystem utm;
		try {
			utm = CRS.decode(String.format("AUTO2:42001,%s,%s", -155.94929,  19.65767 ), true);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		}


		MathTransform toMeters;
		try {
			toMeters = CRS.findMathTransform(wgs84, utm);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		}
		try {
			MathTransform toDegrees= CRS.findMathTransform(utm, wgs84);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GeometryFactory jtsGf= JTSFactoryFinder.getGeometryFactory();

//		Coordinate point1 = new Coordinate(-155.94929,19.65767);
//		Coordinate point2 = new Coordinate(-155.94934,19.65943);

//		Coordinate[] coordinates = {point1, point2};
//		double dist = JTS.orthodromicDistance(point1, point2, DefaultGeographicCRS.WGS84);
//		System.out.println("ortho " + dist);

		MultiPoint mp = new GeometryFactory().createMultiPointFromCoords(cache.asMap().values().toArray(new Coordinate[cache.asMap().values().size()]));

		MinimumBoundingCircle m = new MinimumBoundingCircle(mp);
//		;
//		System.out.println(">>" + m.getRadius() * 2);

		Geometry g4;
		try {
			g4 = JTS.transform(m.getFarthestPoints(), toMeters);
		} catch (MismatchedDimensionException | TransformException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		}

		BigDecimal accuracy = new BigDecimal(Double.toString(g4.getLength()));
		accuracy = accuracy.setScale(2, RoundingMode.HALF_UP);

		System.out.println("Accuracy: " + accuracy + "m.");
	}
}
