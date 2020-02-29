package data.simplefeature.pointfeature;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午4:38:18
 * 类说明
 */
public class BJTaxiData implements PointFeature {
	SimpleFeature pointFeature;

	public BJTaxiData(SimpleFeature pointFeature) {
		this.pointFeature = pointFeature;
	}

	@Override
	public Point getPoint() {
		return (Point) pointFeature.getProperty("the_geom").getValue();
	}

	@Override
	public Double getDirection() {
		return Double.valueOf((Integer) pointFeature.getProperty("direction").getValue()) / 180 * Math.PI;
	}

	@Override
	public Long getTime() {
		SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			String time = (String) pointFeature.getProperty("time").getValue();
			return SDF.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	@Override
	public SimpleFeature getFeature() {
		return pointFeature;
	}

	@Override
	public Double getSpeed() {
		return (Double) pointFeature.getProperty("speed").getValue();
	}

}
