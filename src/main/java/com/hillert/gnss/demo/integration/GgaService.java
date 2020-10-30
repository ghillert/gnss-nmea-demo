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
import org.springframework.messaging.Message;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hillert.gnss.demo.store.GnssStatusStore;

import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.Position;

/**
 * Processes {@link GGASentence}s.
 *
 * @author Gunnar Hillert
 *
 */
public class GgaService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GgaService.class);

	private final GnssStatusStore gnssStatusStore;

	public GgaService(GnssStatusStore gnssStatusStore) {
		super();
		this.gnssStatusStore = gnssStatusStore;
	}

	/**
	 * Currently handles the {@link GGASentence} (Global positioning system fix data).
	 *
	 * Note: The GGL message (Latitude and longitude, with time of position fix and status)
	 * would be technically more appropriate compared to the GGA message, but the
	 * GGA message also provides altitude information.
	 *
	 * @param message The {@link GGASentence} to process, only GGA, GSA, GSV - other messages will be ignored
	 */
	public void process(Message<GGASentence> message) {
		final GGASentence sentence = message.getPayload();

		final Position position = SentenceUtils.handleNmeaData(sentence::getPosition);

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

		final Double altitude = SentenceUtils.handleNmeaData(sentence::getAltitude);
		final GpsFixQuality fixQuality = SentenceUtils.handleNmeaData(sentence::getFixQuality);
		this.gnssStatusStore.getGnssStatus().setLatitude(latitude);
		this.gnssStatusStore.getGnssStatus().setLongitude(longitude);
		this.gnssStatusStore.getGnssStatus().setAltitude(altitude);
		this.gnssStatusStore.getGnssStatus().setFixQuality(fixQuality);
	}

	final Cache<String, Coordinate> cache = CacheBuilder.newBuilder().maximumSize(100).build();

	private void addToCache(double latitude, double longitude) {

		cache.put(String.valueOf(latitude) + "_" + String.valueOf(longitude), new Coordinate(longitude,latitude));

		if (cache.size() > 20) {
			final BigDecimal accuracy = calculateMinimumBoundingCircle(cache);
			this.gnssStatusStore.getGnssStatus().setCalculatedHorizontalAccuracyInMeters(accuracy.doubleValue());
		}
	}

	private BigDecimal calculateMinimumBoundingCircle(Cache<String, Coordinate> cache) {
		final CoordinateReferenceSystem wgs84;
		try {
			wgs84 = CRS.decode("EPSG:4326", true);
		}
		catch (FactoryException e) {
			throw new IllegalStateException(e);
		}
		final CoordinateReferenceSystem utm;
		try {
			utm = CRS.decode(String.format("AUTO2:42001,%s,%s", -155.94929,  19.65767 ), true);
		}
		catch (FactoryException e) {
			throw new IllegalStateException(e);
		}

		final MathTransform toMeters;
		try {
			toMeters = CRS.findMathTransform(wgs84, utm);
		}
		catch (FactoryException e) {
			throw new IllegalStateException(e);
		}

		final GeometryFactory jtsGf= JTSFactoryFinder.getGeometryFactory();
		final MultiPoint mp = jtsGf.createMultiPointFromCoords(cache.asMap().values().toArray(new Coordinate[cache.asMap().values().size()]));
		final MinimumBoundingCircle minimumBoundingCircle = new MinimumBoundingCircle(mp);

		final Geometry farthestPointsInUtm;

		try {
			farthestPointsInUtm = JTS.transform(minimumBoundingCircle.getFarthestPoints(), toMeters);
		}
		catch (MismatchedDimensionException | TransformException e) {
			throw new IllegalStateException(e);
		}

		final BigDecimal accuracy = new BigDecimal(Double.toString(farthestPointsInUtm.getLength()))
			.setScale(2, RoundingMode.HALF_UP);

		LOGGER.info("Accuracy: {}m.", accuracy);

		return accuracy;
	}
}
