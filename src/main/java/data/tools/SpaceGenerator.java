package data.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.List;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import app.tools.DiffTools;
import data.datareader.SHPReader;

/*
 * @author pjl
 * @version 创建时间：2019年7月19日 上午10:23:50
 * 删除一部分点以生成平均采样间隔更大的新数据集
 */
public class SpaceGenerator {
	public static void addSpacePath(String path, int interval) {
		File[] inputFiles = new File(path).listFiles();
		for (File file : inputFiles) {
			if (file.isFile()) {
//				String name = file.getName().replaceAll("[.][^.]+$", "");
				String name = file.getName().substring(0, 17);
				if (file.getName().endsWith(".shp")) {
					addSpaceSHP(path, name, interval);
				} else if (file.getName().endsWith(".txt")) {
					addSpaceTXT(path, name, interval);
				}
			}
		}
	}

	/*
	 * 间隔interval个 shp版
	 */
	public static void addSpaceSHP(String path, String fileName, int interval) {
		SimpleFeatureCollection pointOrigin = SHPReader.readSHP(new File(path + fileName + ".shp"), false);
		SimpleFeatureIterator i = pointOrigin.features();
		ShapefileDataStore newsds;
		try {
			newsds = new ShapefileDataStore(new File(path + fileName + "_" + interval + ".shp").toURI().toURL());
			SimpleFeatureType schema = pointOrigin.getSchema();
			newsds.createSchema(schema);
			newsds.setCharset(Charset.forName("UTF-8"));
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newsds.getFeatureWriter(newsds.getTypeNames()[0],
					Transaction.AUTO_COMMIT);
			int gap = 0;
			while (i.hasNext()) {
				SimpleFeature sf = i.next();
				if (gap <= 0) {
					gap = interval;
					writer.hasNext();
					SimpleFeature temp = writer.next();
					temp.setAttributes(sf.getAttributes());
					try {
						writer.write();
					} catch (Exception e) {
						for (int k = 0; k < temp.getAttributeCount(); k++) {
							System.out.print(temp.getAttribute(k).toString() + " ");
						}
						System.out.println();
					}
				} else {
					--gap;
				}
			}
			i.close();
			writer.close();
			newsds.dispose();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 间隔interval个 txt版
	 */
	public static void addSpaceTXT(String path, String fileName, int interval) {
		try {
			TXTWriter tw = new TXTWriter(path + fileName + "_" + interval + "_TRUTH" + ".txt");
			List<String> txtData = DiffTools.file2List(path + fileName + "_TRUTH" + ".txt");
			int gap = 0;
			for (String s : txtData) {
				if (gap <= 0) {
					gap = interval;
					tw.write(s);
				} else {
					--gap;
				}
			}
			tw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}