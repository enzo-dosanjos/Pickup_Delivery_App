package domain.service;

import domain.model.Graphe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class IteratorSeq implements Iterator<Integer> {

	//private Integer[] candidats;
	//private int nbCandidats;
	private final Iterator<Integer> iterator;
	private final List<Integer> candidats;

	/**
	 * Cree un iterateur pour iterer sur l'ensemble des sommets de nonVus qui sont successeurs de sommetCrt dans le graphe g,
	 * dans l'odre d'apparition dans <code>nonVus</code>
	 * @param nonVus
	 * @param sommetCrt
	 * @param g
	 */
	public IteratorSeq(Collection<Integer> nonVus, int sommetCrt, Graphe g) {
        candidats = new ArrayList<>();
        for (Integer s : nonVus) {
            if (g.estArc(sommetCrt, s)) {
                candidats.add(s);
            }
        }
        // Ordenar por costo ascendente (más prometedores primero)
        candidats.sort(Comparator.comparingDouble(s -> g.getCout(sommetCrt, s)));
        iterator = candidats.iterator();
    }
	
	@Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Integer next() {
        return iterator.next();
    }

	@Override
	public void remove() {}
}
