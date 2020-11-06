package ir.ceit.search.model;

import java.util.ArrayList;
import java.util.Collections;

public class Heap {
    public ArrayList<DocHeapElement> getDocHeap() { return docHeap; }
    private ArrayList<DocHeapElement> docHeap;
    public ArrayList<Integer> getIndices() { return indices; }
    private ArrayList<Integer> indices;

    public Heap(ArrayList<DocHeapElement> docHeapElements)
    {
        docHeap = new ArrayList<>(docHeapElements);
        docHeap.get(0).setSimilarity(Double.MAX_VALUE);
//        maxHeapIFy();
//        indices = new ArrayList<>();
//        for(int index = 0; index < docHeap.size(); index ++)
//        {
//            indices.add(index);
//        }
    }

    private int parent(int pos)
    {
        return pos / 2;
    }

    private int leftChild(int pos)
    {
        return (2 * pos);
    }
    private int rightChild(int pos)
    {
        return (2 * pos) + 1;
    }

    private boolean isLeaf(int pos)
    {
        return pos >= (docHeap.size() / 2) && pos <= docHeap.size();
    }

    public void maxHeapIFy()
    {
        for(int pos = parent(docHeap.size()-1); pos > 0; pos--)
        {
            maxHeapIFy(pos);
        }
    }

    private void maxHeapIFy(int pos)
    {
        if (isLeaf(pos)) return;

        if(docHeap.get(pos).getSimilarity() < docHeap.get(leftChild(pos)).getSimilarity() || docHeap.get(pos).getSimilarity() < docHeap.get(rightChild(pos)).getSimilarity())
        {
            if (docHeap.get(leftChild(pos)).getSimilarity() > docHeap.get(rightChild(pos)).getSimilarity())
            {
                Collections.swap(docHeap, pos, leftChild(pos));
//                Collections.swap(indices,pos,leftChild(pos));
                maxHeapIFy(leftChild(pos));
            }
            else
            {
                Collections.swap(docHeap, pos, rightChild(pos));
//                Collections.swap(indices,pos,rightChild(pos));
                maxHeapIFy(rightChild(pos));
            }
        }
    }

    public DocHeapElement extractMax()
    {
        Collections.swap(docHeap, 1,docHeap.size() - 1);
        DocHeapElement popped = docHeap.remove(docHeap.size() - 1);
        maxHeapIFy(1);
        return popped;
    }

    public void removeZeroSimilarities()
    {
        int index = docHeap.size() - 1;
        while(index > 0)
        {
            if(docHeap.get(index).getSimilarity() == 0)
            {
                docHeap.remove(index);
            }
            index --;
        }
    }

    public void print() {
        for (int i = 1; i <= docHeap.size() / 2; i++) {
            System.out.print(" PARENT : " + docHeap.get(i) + " LEFT CHILD : " +
                    docHeap.get(2*i) + " RIGHT CHILD :" + docHeap.get(2*i+1));
            System.out.println();
        }
    }
}
