package ir.ceit.search.model;

import java.io.Serializable;

public class Node implements Serializable {
    private String word;
    private DocListElement docHead;
    private double df;
    private Node rightNode;
    private Node leftNode;

    public Node(String word)
    {
        this.word = word;
        this.docHead = null;
        this.df = 0;
        this.rightNode = null;
        this.leftNode = null;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public DocListElement getDocHead() {
        return docHead;
    }

    public void setDocHead(DocListElement docHead) {
        this.docHead = docHead;
    }

    public double getDf() { return  this.df; }

    public void increaseDf() { this.df ++; }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node nextNode) {
        this.rightNode = nextNode;
    }

    public Node getLeftNode() { return leftNode; }

    public void setLeftNode(Node leftNode) { this.leftNode = leftNode; }
}
