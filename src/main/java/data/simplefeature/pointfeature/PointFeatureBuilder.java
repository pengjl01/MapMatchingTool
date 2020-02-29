package data.simplefeature.pointfeature;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午8:29:37
 * 创建路径的SimpleFeature
 */
public class PointFeatureBuilder {
	SimpleFeatureBuilder featureBuilder;
	SimpleFeatureType sft;

	public PointFeatureBuilder(SimpleFeatureCollection sfc) {
		sft = getSimpleFeatureType(sfc);
		featureBuilder = new SimpleFeatureBuilder(sft);
	}

	private static SimpleFeatureType getSimpleFeatureType(SimpleFeatureCollection points) {
		SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
		sftb.init(points.getSchema());
		sftb.add("road_id", String.class);
		sftb.add("the_geom_old", Point.class);
		return sftb.buildFeatureType();
	}

	public SimpleFeatureType getFeatureType() {
		return sft;
	}

	public SimpleFeature buildFeature(PointFeature pointSource, String matchedId, Point matchedPoint) {
//		System.out.println(pointSource.getProperty("the_geom").getValue());
		for (int j = 0; j < pointSource.getFeature().getAttributeCount(); ++j) {
			featureBuilder.set(j, pointSource.getFeature().getAttribute(j));
		}
		featureBuilder.set("the_geom", matchedPoint);
		featureBuilder.set("the_geom_old", pointSource.getFeature().getProperty("the_geom").getValue());
		featureBuilder.set("road_id", matchedId);
		return featureBuilder.buildFeature(null);
	}
}
