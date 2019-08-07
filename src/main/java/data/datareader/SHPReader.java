package data.datareader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;

import data.tools.GridTrans;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午9:15:45
 * SHP读取工具类
 */
public class SHPReader {
	public static SimpleFeatureCollection readSHP(File file, Boolean trans) {
		FileDataStore store;
		try {
			store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureCollection origin = store.getFeatureSource().getFeatures();
			if (trans) {
				SimpleFeatureIterator iterator = origin.features();
				List<SimpleFeature> list = new ArrayList<SimpleFeature>();
				try {
					while (iterator.hasNext()) {
						SimpleFeature next = iterator.next();
//						System.out.println(next.getProperty("the_geom").getValue());
						GridTrans.trans((Geometry) next.getProperty("the_geom").getValue());
//						System.out.println(next.getProperty("the_geom").getValue());
						list.add(next);
					}
				} finally {
					iterator.close();
				}
				return new ListFeatureCollection(origin.getSchema(), list);
			} else {
				return origin;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
