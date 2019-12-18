package Xml;

public class StringNode extends Node {

    private String value;

    public StringNode(String attr, String value) {
        this.value = value;
    }


    public void accept(Visitor v, String nm) {
        v.visit(this, nm);
    }

    public String getValue() {
        return value;
    }
}
