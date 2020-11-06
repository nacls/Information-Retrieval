package ir.ceit.search.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class DocListElement implements Serializable {
    private int docID;
    private ArrayList<Integer> positionList;
    private DocListElement nextDoc;

    public DocListElement(int docID)
    {
        this.docID = docID;
        this.positionList = new ArrayList<>();
        this.nextDoc = null;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public void addToPositionList(int position) {
        this.positionList.add(position);
        Collections.sort(this.positionList);
    }

    public void setNextDoc(DocListElement nextDoc) {
        this.nextDoc = nextDoc;
    }

    public int getDocID() {
        return docID;
    }

    public ArrayList<Integer> getPositionList() {
        return positionList;
    }

    public DocListElement getNextDoc() {
        return nextDoc;
    }
}
