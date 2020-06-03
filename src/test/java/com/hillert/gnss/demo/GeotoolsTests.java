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
package com.hillert.gnss.demo;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

class GeotoolsTests {

	@Test
	void minimumBoundingCircleTest() throws TransformException, FactoryException {
		final CoordinateReferenceSystem wgs84= CRS.decode("EPSG:4326", true);
		final CoordinateReferenceSystem utm=CRS.decode(String.format("AUTO2:42001,%s,%s", -155.94929,  19.65767 ), true);


		final MathTransform toMeters= CRS.findMathTransform(wgs84, utm);
		final MathTransform toDegrees= CRS.findMathTransform(utm, wgs84);

		final GeometryFactory jtsGf= JTSFactoryFinder.getGeometryFactory();

		final Coordinate point1 = new Coordinate(-155.94929,19.65767);
		final Coordinate point2 = new Coordinate(-155.94934,19.65943);

		final Coordinate[] coordinates = { point1, point2 };
		double dist = JTS.orthodromicDistance(point1, point2, DefaultGeographicCRS.WGS84);

		System.out.println("ortho " + dist);

		final MultiPoint mp = new GeometryFactory().createMultiPointFromCoords(coordinates);

		final MinimumBoundingCircle m = new MinimumBoundingCircle(mp);

		System.out.println(">>" + m.getRadius() * 2);

		final Geometry g4 = JTS.transform(m.getFarthestPoints(), toMeters);

		System.out.println("final >>" + g4.getLength());

//		CoordinateReferenceSystem auto = CRS.decode("epsg:27700");
//		  MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, auto);
//		  Geometry g3 = JTS.transform(point2, transform);
//		  Geometry g4 = JTS.transform(point1, transform);
//		  double dist1 = g3.distance(g4);
//		  System.out.println("epsg27700: " + dist1);
//		  dist = JTS.orthodromicDistance(point.getCoordinate(), point2.getCoordinate(), DefaultGeographicCRS.WGS84);
//		  System.out.println("ortho " + dist);
	}
}
