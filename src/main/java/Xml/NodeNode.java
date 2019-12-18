package Xml;

public class NodeNode extends Node {

    public String name;

    public NodeNode(String name) {
        // System.out.println("NodeNode::constructor " + name);
        this.child = null;
        this.sibling = null;
        this.name = name;
    }

    public void accept(Visitor v, String name) {
        v.visit(this, name);
    }
}
