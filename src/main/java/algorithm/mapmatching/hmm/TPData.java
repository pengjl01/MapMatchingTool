package algorithm.mapmatching.hmm;

public class TPData {
	public TPData() {
		tp = 0;
		parentNode = null;
	}

	public TPData(double maxtp, HMMNode bestParent) {
		tp = maxtp;
		parentNode = bestParent;
	}

	public double tp;
	public HMMNode parentNode;
}