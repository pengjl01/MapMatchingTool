package data.simplefeature.pointfeature;

import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午4:33:36
 * Point Feature 封装
 */
public interface PointFeature {
	/*
	 * 获取坐标点，
	 * 
	 * @return 车辆的位置
	 */
	public Point getPoint();

	/*
	 * 获取行驶方向
	 * 
	 * @return 行驶方向与正北方向顺时针的夹角,结果为弧度
	 */
	public Double getDirection();

	/*
	 * 获取时间戳
	 * 
	 * @return 返回Long型时间戳
	 */
	public Long getTime();

	/*
	 * 获取Feature
	 * 
	 * @return 返回Feature本身
	 */
	public SimpleFeature getFeature();

	/*
	 * 获取速度
	 * 
	 * @return 返回速度值
	 */
	public Double getSpeed();
}
