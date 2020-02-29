package data.simplefeature.roadsegment;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiLineString;

/*
 * @author pjl
 * @version 创建时间：2019年6月26日 下午12:23:01
 * 道路数据的接口
 */
public interface RoadSegment {
	/*
	 * 获取道路ID，
	 * 
	 * @return 道路的ID
	 */
	public String getID();

	/*
	 * 获取行驶方向信息，
	 * 
	 * @return 'B'代表可以双向行驶，'T'表示只能逆向行驶，'F'表示只能正向行驶
	 */
	public char getOneway();

	/*
	 * 获取道路几何信息，通常为只有一个LineString的MultiLineString，
	 * 但数据类型为MultiLineString，不排除出现多条道路的可能
	 * 
	 * @return 道路的几何信息
	 */
	public MultiLineString getGeom();

	/*
	 * 获取该道路上距离输入点最近的两个顶点的坐标； 不会考虑路网方向限制
	 * 
	 * @return 输入道路上与输入点最近的两个顶点
	 */
	public Coordinate[] getClosestNodes(Coordinate c);

	/*
	 * 获取速度
	 * 
	 * @return 速度值
	 */
	public Double getSpeed();
}
