package algorithm.pathfinder;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午4:15:10
 * 类说明
 */
public interface PathFinder {

	/*
	 * 获取最短路径
	 */
	public List<Coordinate> findCoordinatePath();

	/*
	 * 获取最短路径的路径距离
	 */
	public Double routeDistance();
}
