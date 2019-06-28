package entity.graph;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiLineString;

/*
 * @author pjl
 * @version 创建时间：2019年6月26日 下午12:42:43
 * 图接口，主要用于道路距离计算
 */
public interface Graph {
	/*
	 * 检测是否直连
	 */
	public boolean directConnected(Integer startIndex, Integer endIndex);

	/*
	 * 检测是否直连
	 */
	public boolean directConnected(Coordinate startNode, Coordinate endNode);

	/*
	 * 返回节点总数
	 */
	public int getNodeNum();

	/*
	 * 通过节点index获取节点坐标，index超过节点总数，返回null
	 */
	public Coordinate getNode(int index);

	/*
	 * 通过坐标获取节点index，若没有该节点，返回null
	 */
	public Integer getIndex(Coordinate c);

	/*
	 * 在node1和node2之间切开，添加newNode节点。
	 */
	public boolean cutAndAdd(Coordinate node1, Coordinate node2, Coordinate newNode);

	/*
	 * cutAndAdd反操作,只能移除最后一个节点
	 */
	public boolean repareCut(Coordinate node1, Coordinate node2, Coordinate newNode);

	/*
	 * 输出图
	 */
	public void showGraph();

	void addNodes(MultiLineString geom);

	void addEdges(MultiLineString geom, char oneway);
}
