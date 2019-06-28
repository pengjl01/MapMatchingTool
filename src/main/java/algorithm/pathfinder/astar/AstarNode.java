package algorithm.pathfinder.astar;

class AstarNode implements Comparable<AstarNode> {

	public int index;
	public AstarNode parent;
	public double G;
	public double H;

//	public Node(int x, int y) {
//		this.c = new Coordinate(x, y);
//	}
	public AstarNode(int index) {
		this.index = index;
	}

	public AstarNode(int index, AstarNode parent, double G, double H) {
		this.index = index;
		this.parent = parent;
		this.G = G;
		this.H = H;
	}

	@Override
	public int compareTo(AstarNode o) {
		if (o == null)
			return -1;
		double flag = G + H - o.G - o.H;
		if (flag > 0)
			return 1;
		if (flag < 0)
			return -1;
		return 0;
	}

	@Override
	public String toString() {
		return "(index=" + index + ",G=" + G + ",H=" + H + ")";
	}
}