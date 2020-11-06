package ir.ceit.search.model;

public class DocHeapElement {
    private int docID;
    private double similarity;

    public DocHeapElement(int docID) {
        this.docID = docID;
        this.similarity = 0;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

}
