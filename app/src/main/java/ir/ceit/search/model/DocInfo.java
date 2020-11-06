package ir.ceit.search.model;

public class DocInfo {
    private int docID;
    private String path;
    private String category;
    private int row;

    public DocInfo(int docID, String path, String category, int row) {
        docID = docID;
        this.path = path;
        this.category = category;
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        docID = docID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
