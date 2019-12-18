package Xml;

public class Visitor {
    private String serverIP;
    private int serverPort;

    public void visit(Node node, String name) {
        node.accept(this, name);
    }

    public void visit(NodeNode node, String name) {
        // System.out.println("Visitor::visit(NodeNode): name="+ name);

        if(node.sibling!=null)
            visit(node.sibling, node.name);
        if(node.child!=null)
            visit(node.child, node.name);
    }


    public void visit(StringNode node, String name) {
        // System.out.println("Visitor::visit(StringNode): name=" + name);

        if(name.equals("server_ip")) {
            serverIP = node.getValue();
        } else if(name.equals("server_port")){
            serverPort = Integer.valueOf(node.getValue());
        }
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }
}
