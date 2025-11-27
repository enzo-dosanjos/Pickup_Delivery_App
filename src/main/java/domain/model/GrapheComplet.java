package domain.model;

import java.util.Arrays;

public class GrapheComplet implements Graphe {

    long[] sommets;
	int nbSommets;
	double[][] cout;

	public GrapheComplet(int nbSommets) {
        this.nbSommets = nbSommets;
        this.cout = new double[nbSommets][nbSommets];
        for (int i = 0; i < nbSommets; i++) Arrays.fill(cout[i], Integer.MAX_VALUE);
    }

	public GrapheComplet(long[] sommets, int nbSommets){
        this.sommets = Arrays.copyOf(sommets, nbSommets);
		this.nbSommets = nbSommets;
        this.cout = new double[nbSommets][nbSommets];
        for (int i = 0; i < nbSommets; i++) Arrays.fill(cout[i], Integer.MAX_VALUE);
	}

    public long[] getSommets() { return sommets; }

	@Override
	public int getNbSommets() {
		return nbSommets;
	}

	@Override
	public double getCout(int i, int j) {
		if (i<0 || i>=nbSommets || j<0 || j>=nbSommets)
			return -1;
		return cout[i][j];
	}

    public void setCout(int i, int j, double newVal) {
        cout[i][j] = newVal;
    }

	@Override
	public boolean estArc(int i, int j) {
		if (i<0 || i>=nbSommets || j<0 || j>=nbSommets)
			return false;
		return i != j;
	}

    @Override
    public String toString() {
        return "GrapheComplet{" +
                "sommets=" + Arrays.toString(sommets) +
                ", nbSommets=" + nbSommets +
                ", cout=" + Arrays.toString(cout) +
                '}';
    }
}
