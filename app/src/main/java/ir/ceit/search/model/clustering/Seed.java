package ir.ceit.search.model.clustering;

import java.util.HashMap;
import java.util.Vector;

import ir.ceit.search.model.Node;

public class Seed
{
    private HashMap<Node, Double> vectorSpace;
    private Vector<Integer> documentIDs;

    public HashMap<Node, Double> getVectorSpace() {
        return vectorSpace;
    }

    public void setVectorSpace(HashMap<Node, Double> vectorSpace) {
        this.vectorSpace = vectorSpace;
    }

    public Vector<Integer> getDocumentIDs() {
        return documentIDs;
    }

    public Seed(HashMap<Node, Double> vectorSpace)
    {
        this.vectorSpace = new HashMap<>(vectorSpace);
        this.documentIDs = new Vector<>();
    }
}
