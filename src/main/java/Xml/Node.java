package Xml;

public abstract class Node {

    public Node child;
    public Node sibling;

    abstract public void accept(Visitor v, String name);

    public void insertChild(Node node) {
        if(child == null) {
            child = node;
        } else {
            Node p = child;
            while(p.sibling != null) {
                p = p.sibling;
            }
            p.sibling = node;
        }
    }

    public void insertSibling(Node node) {
        if(node != null) {
            Node p = this;
            while(p.sibling != null) {
                p = p.sibling;
            }
            p.sibling = node;
        }
    }

}
