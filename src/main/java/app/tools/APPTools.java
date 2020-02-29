package app.tools;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.util.NullProgressListener;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.index.strtree.STRtree;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;

import algorithm.mapmatching.Matcher;
import data.datareader.SHPReader;
import data.tools.TXTWriter;

/*
 * @author pjl
 * @version 创建时间：2019年7月2日 下午9:10:55
 * 类说明
 */
public class APPTools {
	static boolean trans = true;

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

	public static void doMatch(File inputSHP, String outputTXT, String outputSHP, Matcher matcher, SpatialIndex index,
			TXTWriter ow) {
		SimpleFeatureCollection pointOrigin = SHPReader.readSHP(inputSHP, trans);
		long start = System.currentTimeMillis();
		SimpleFeatureCollection pointMatched = matcher.match(pointOrigin);
		if (ow != null) {
			try {
				ow.write("轨迹共" + pointMatched.size() + "个点");
				ow.write(matcher.getClass().getSimpleName() + "匹配耗时" + (System.currentTimeMillis() - start) + "ms");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("轨迹共" + pointMatched.size() + "个点");
		System.out.println(matcher.getClass().getSimpleName() + "匹配耗时" + (System.currentTimeMillis() - start) + "ms");
		saveResultTXT(pointMatched, outputTXT);
		saveResultSHP(pointMatched, outputSHP);
	}

	/*
	 * 保存结果到文件
	 */
	public static void saveResultTXT(SimpleFeatureCollection pointMatched, String txtResult) {
		List<String> ansList = getMatchingAnswer(pointMatched);
		saveResultTXT(ansList, txtResult);
	}

	/*
	 * 保存结果到文件
	 */
	public static void saveResultTXT(List<String> ansList, String txtResult) {
		TXTWriter tw = null;
		try {
			tw = new TXTWriter(txtResult);
			for (String s : ansList) {
				tw.write(s);
			}
			tw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				if (tw != null) {
					tw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/*
	 * 从SimpleFeatureCollection提取匹配结果（道路ID）
	 */
	public static List<String> getMatchingAnswer(SimpleFeatureCollection pointMatched) {
		List<String> ans = new ArrayList<String>();
		SimpleFeatureIterator i = pointMatched.features();
		try {
			while (i.hasNext()) {
				ans.add((String) i.next().getProperty("road_id").getValue());
			}
		} finally {
			i.close();
		}
		return ans;
	}

	public static void saveResultSHP(SimpleFeatureCollection pointMatched, String SHPResult) {
		Map<String, Serializable> params = new HashMap<>();
		try {
			params.put("url", new File(SHPResult).toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);
			ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
			ds.createSchema(pointMatched.getSchema());
			ds.setCharset(Charset.forName("UTF-8"));
			SimpleFeatureSource featureSource = ds.getFeatureSource(ds.getTypeNames()[0]);
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			featureStore.addFeatures(pointMatched);
			ds.dispose();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
