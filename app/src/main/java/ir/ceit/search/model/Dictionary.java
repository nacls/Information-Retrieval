package ir.ceit.search.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Dictionary {
    private Node root;
    private int size;
    private Vector<HashMap<Node, Integer>> docList;
    private int docSize;
    private ArrayList<DocHeapElement> docHeap;


    public Dictionary() {
        this.root = null;
        docList = new Vector<>();
        docHeap = new ArrayList<>();
        this.size = 0;
        this.docSize = 0;
    }

    public void addWord(Node currentNode, String word, int docID, int position) {
        if (this.root == null) {
            Node newNode = new Node(word);
            DocListElement doc = new DocListElement(docID);
            doc.addToPositionList(position);
            newNode.setDocHead(doc);
            newNode.increaseDf();
            this.root = newNode;
            this.docList.get(docID).put(newNode, 1);
            this.size++;
        } else if (currentNode.getWord().equals(word)) {
            if (!this.docList.get(docID).keySet().contains(currentNode)) {
                this.docList.get(docID).put(currentNode, 1);
                currentNode.increaseDf();
            } else {
                int tf = this.docList.get(docID).get(currentNode);
                this.docList.get(docID).replace(currentNode, tf + 1);
            }
            DocListElement currentDoc = currentNode.getDocHead();
            while (currentDoc.getNextDoc() != null && currentDoc.getNextDoc().getDocID() < docID) {
                currentDoc = currentDoc.getNextDoc();
            }
            if (currentDoc.getNextDoc() == null) {
                if (currentDoc.getDocID() == docID) {
                    currentDoc.addToPositionList(position);
                } else {
                    DocListElement newDoc = new DocListElement(docID);
                    newDoc.addToPositionList(position);
                    currentDoc.setNextDoc(newDoc);
                }
            } else if (currentDoc.getNextDoc().getDocID() == docID) {
                currentDoc = currentDoc.getNextDoc();
                currentDoc.addToPositionList(position);
            } else if (currentDoc.getNextDoc().getDocID() > docID) {
                DocListElement newDoc = new DocListElement(docID);
                newDoc.addToPositionList(position);
                newDoc.setNextDoc(currentDoc.getNextDoc());
                currentDoc.setNextDoc(newDoc);
            }
        } else if (currentNode.getWord().compareTo(word) < 0) {
            if (currentNode.getRightNode() == null) {
                Node newNode = new Node(word);
                DocListElement doc = new DocListElement(docID);
                doc.addToPositionList(position);
                newNode.setDocHead(doc);
                this.docList.get(docID).put(newNode, 1);
                newNode.increaseDf();
                currentNode.setRightNode(newNode);
                this.size++;
            } else {
                addWord(currentNode.getRightNode(), word, docID, position);
            }
        } else {
            if (currentNode.getLeftNode() == null) {
                Node newNode = new Node(word);
                DocListElement doc = new DocListElement(docID);
                doc.addToPositionList(position);
                newNode.setDocHead(doc);
                newNode.increaseDf();
                this.docList.get(docID).put(newNode, 1);
                currentNode.setLeftNode(newNode);
                this.size++;
            } else {
                addWord(currentNode.getLeftNode(), word, docID, position);
            }
        }
    }


    public Node searchWord(String word) {
        return searchWord(this.root, word);
    }

    public Node searchWord(Node currentNode, String word) {
        if (currentNode == null) {
            return new Node("");
        }
        if (currentNode.getWord().equals(word)) {
            return currentNode;
        } else if (currentNode.getWord().compareTo(word) < 0) {
            return searchWord(currentNode.getRightNode(), word);
        } else {
            return searchWord(currentNode.getLeftNode(), word);
        }
    }

    public void printDictionary() {
        printDictionary(this.root);
    }

    public void printDictionary(Node node) {
        if (node == null) {
            return;
        } else {
            printDictionary(node.getLeftNode());
            System.out.println("word:" + node.getWord());
            DocListElement docHead = node.getDocHead();
            while (docHead != null) {
                System.out.println("ID:" + docHead.getDocID() + docHead.getPositionList());
                docHead = docHead.getNextDoc();
            }
            printDictionary(node.getRightNode());
        }
    }

    public Vector<HashMap<Node, Integer>> getDocList() {
        return docList;
    }

    public ArrayList<DocHeapElement> getDocHeap() {
        return docHeap;
    }

    public void addDocHeapElement(int docID) {
        this.docHeap.add(new DocHeapElement(docID));
    }

    public void addDocList() {
        this.docList.add(new HashMap<>());
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDocSize() {
        return docSize;
    }

    public void setDocSize(int docSize) {
        this.docSize = docSize;
    }
}