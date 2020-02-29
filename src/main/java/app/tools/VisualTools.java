package app.tools;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.opengis.feature.simple.SimpleFeature;

import data.datareader.SHPReader;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午9:09:25
 * 地图显示用工具类
 */
public class VisualTools {
//	向待展示的地图中添加道路（简单黑色线）
	static void addRoad(MapContent map, File file) {
		FileDataStore store;
		try {
			store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			Style style = SLD.createSimpleStyle(featureSource.getSchema());
			Layer layer = new FeatureLayer(featureSource, style);
			map.layers().add(layer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addRoad(MapContent map, SimpleFeatureCollection file) {
		Style style = SLD.createSimpleStyle(file.getSchema());
		Layer layer = new FeatureLayer(file, style);
		map.layers().add(layer);
	}

//	static void addRoad1(MapContent map, File file) {
//		FileDataStore store;
//		try {
//			store = FileDataStoreFinder.getDataStore(file);
//			SimpleFeatureSource featureSource = store.getFeatureSource();
//			SimpleFeatureCollection tmpCollection = featureSource.getFeatures();
//			SimpleFeatureIterator pointsIterator = tmpCollection.features();
//			DefaultFeatureCollection modificationCollection = new DefaultFeatureCollection();
//			SimpleFeatureBuilder modificationBuilder = new SimpleFeatureBuilder(GetFeatureType.getLineFeatureType());
//			while (pointsIterator.hasNext()) {
//				Geometry temp = (Geometry) pointsIterator.next().getProperty("the_geom").getValue();
//				CoordinateTrans.trans(temp);
//				modificationBuilder.add(temp);
//				modificationCollection.add(modificationBuilder.buildFeature(null));
//			}
//			Style style = SLD.createSimpleStyle(modificationCollection.getSchema());
//			Layer layer = new FeatureLayer(modificationCollection, style);
//			map.layers().add(layer);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public static void addPoints(MapContent map, SimpleFeatureCollection temp, Color color) {
		addPoints(map, DataUtilities.source(temp), color);
	}

//	向待展示的地图中添加点
	public static void addPoints(MapContent map, SimpleFeatureSource featureSource, Color color) {
		Style style = SLD.createPointStyle("Circle", color, color, 1, 5.0f);
		Layer layer = new FeatureLayer(featureSource, style);
		map.layers().add(layer);

	}

//	向待展示的地图中添加线
	public static void addLines(MapContent map, SimpleFeatureCollection temp, Color color) {
		addLines(map, DataUtilities.source(temp), color);
	}

	public static void addLines(MapContent map, SimpleFeatureSource temp, Color color) {
		Style style = SLD.createLineStyle(color, 2.0f);
		Layer layer = new FeatureLayer(temp, style);
		map.layers().add(layer);

	}

//	将点的FeatureCollection转化为线的FeatureCollection
	public static SimpleFeatureCollection point2Line(SimpleFeatureCollection pointCollection) {
		return point2Line(DataUtilities.source(pointCollection));
	}

	static SimpleFeatureCollection point2Line(SimpleFeatureSource pointSource) {
		List<Coordinate> ModificationCoordinates = new ArrayList<Coordinate>();
		SimpleFeatureTypeBuilder modificationType = new SimpleFeatureTypeBuilder();
		modificationType.setName("Modification");
		modificationType.setCRS(DefaultGeographicCRS.WGS84);
		modificationType.add("the_geom", LineString.class);
		SimpleFeatureBuilder modificationBuilder = new SimpleFeatureBuilder(modificationType.buildFeatureType());
		DefaultFeatureCollection modificationCollection = new DefaultFeatureCollection();
		try {
			SimpleFeatureIterator GPSIterator = pointSource.getFeatures().features();
			while (GPSIterator.hasNext()) {
				// Move to the next item in the iterator.
				SimpleFeature GPSFeature = GPSIterator.next();
				// Load the GPS point and create a coordinate.
				Point GPSPoint = (Point) GPSFeature.getProperty("the_geom").getValue();
				Coordinate GPSCoordinate = new Coordinate(GPSPoint.getX(), GPSPoint.getY());
				ModificationCoordinates.add(GPSCoordinate);
			}
			Coordinate[] ModificationCoordinatesArray = new Coordinate[ModificationCoordinates.size()];
			ModificationCoordinates.toArray(ModificationCoordinatesArray);
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
			LineString modification = geometryFactory.createLineString(ModificationCoordinatesArray);
			modificationBuilder.add(modification);
			modificationCollection.add(modificationBuilder.buildFeature(null));
			GPSIterator.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modificationCollection;
	}

	public static SimpleFeatureCollection point2Route(SimpleFeatureCollection pointCollection, SpatialIndex index) {
		return point2Route(DataUtilities.source(pointCollection), index);
	}

	private static SimpleFeatureCollection point2Route(SimpleFeatureSource pointSource, SpatialIndex index) {
		List<Coordinate> ModificationCoordinates = new ArrayList<Coordinate>();
		SimpleFeatureTypeBuilder modificationType = new SimpleFeatureTypeBuilder();
		modificationType.setName("Modification");
		modificationType.setCRS(DefaultGeographicCRS.WGS84);
		modificationType.add("the_geom", LineString.class);
		SimpleFeatureBuilder modificationBuilder = new SimpleFeatureBuilder(modificationType.buildFeatureType());
		DefaultFeatureCollection modificationCollection = new DefaultFeatureCollection();
		Coordinate preCoordinate = null;
		try {
			SimpleFeatureIterator GPSIterator = pointSource.getFeatures().features();
			while (GPSIterator.hasNext()) {
				// Move to the next item in the iterator.
				SimpleFeature GPSFeature = GPSIterator.next();
				// Load the GPS point and create a coordinate.
				Point GPSPoint = (Point) GPSFeature.getProperty("the_geom").getValue();
				Coordinate GPSCoordinate = new Coordinate(GPSPoint.getX(), GPSPoint.getY());
				ModificationCoordinates.add(GPSCoordinate);
				if (preCoordinate != null) {

				}
			}
			Coordinate[] ModificationCoordinatesArray = new Coordinate[ModificationCoordinates.size()];
			ModificationCoordinates.toArray(ModificationCoordinatesArray);
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
			LineString modification = geometryFactory.createLineString(ModificationCoordinatesArray);
			modificationBuilder.add(modification);
			modificationCollection.add(modificationBuilder.buildFeature(null));
			GPSIterator.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modificationCollection;
	}

	public static void show(String originSHP, String matchedSHP, SimpleFeatureCollection roadCollection) {
		SimpleFeatureCollection origin = SHPReader.readSHP(new File(originSHP), true);
		SimpleFeatureCollection matched = SHPReader.readSHP(new File(matchedSHP), false);
		show(origin, matched, roadCollection);
	}

	public static void show(SimpleFeatureCollection origin, SimpleFeatureCollection matched,
			SimpleFeatureCollection roadCollection) {
		MapContent map = new MapContent();
		addLines(map, VisualTools.point2Line(origin), Color.blue);
		addLines(map, VisualTools.point2Line(matched), Color.red);
		addPoints(map, origin, Color.blue);
		addPoints(map, matched, Color.red);
		addRoad(map, roadCollection);
		JMapFrame.showMap(map);
	}

	public static void show(String shpFile, SimpleFeatureCollection roadCollection) {
		MapContent map = new MapContent();
		SimpleFeatureCollection data = SHPReader.readSHP(new File(shpFile), true);
		addLines(map, VisualTools.point2Line(data), Color.red);
		addPoints(map, data, Color.red);
		addRoad(map, roadCollection);
		JMapFrame.showMap(map);
	}
}
