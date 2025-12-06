package domain.service;

import domain.model.Graphe;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

@Service
public class TSP1 extends TemplateTSP {

	@Override
	protected double bound(Integer sommetCourant, Collection<Integer> nonVus) {
		return 0.0;
	}

	@Override
	protected Iterator<Integer> iterator(Integer sommetCrt, Collection<Integer> nonVus, Graphe g) {
		return new IteratorSeq(nonVus, sommetCrt, g);
	}

}
