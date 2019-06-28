package data.tools;

import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.util.NullProgressListener;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.index.strtree.STRtree;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午9:27:22
 * 原始数据工具类
 */
public class DataTools {
	/*
	 * 为SimpleFeatureCollection建立strtree
	 */
	public static SpatialIndex buildSTRTree(SimpleFeatureCollection roads) {
		SpatialIndex index = new STRtree();
		try {
			roads.accepts(new FeatureVisitor() {

				@Override
				public void visit(Feature feature) {
					SimpleFeature simpleFeature = (SimpleFeature) feature;
					Geometry geom = (MultiLineString) simpleFeature.getDefaultGeometry();
					// Just in case: check for null or empty geometry
					if (geom != null) {
						Envelope env = geom.getEnvelopeInternal();
						if (!env.isNull()) {
							index.insert(env, simpleFeature);
						}
					}
				}
			}, new NullProgressListener());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return index;
	}
}
