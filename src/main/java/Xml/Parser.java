package Xml;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public interface Primitive {
        boolean parse(String text);

        Node getNode();

        String getRest();
    }

    private Primitive parser;

    public Parser() {
//        parser = new And(new NodeParser(), new Eof());
//        parser = new And(new Or(new NodeParser(), new And(new NodeParser(), new Ignore())), new Eof());
        parser = new Or(new And(new NodeParser(), new Eof()),
                        new And(new And(new NodeParser(), new Ignore()), new Eof()));
    }

    public boolean parse(String text) {
        return parser.parse(text);
    }

    public Node getNode() {
        return parser.getNode();
    }


    public class And implements Primitive {
        private Primitive p1;
        private Primitive p2;
        private String rest;
        public And(Primitive p1, Primitive p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public boolean parse(String text) {
            if(!p1.parse(text)) {
                return false;
            }

            if(!p2.parse(p1.getRest())) {
                return false;
            }

            rest = p2.getRest();

            return true;
        }

        @Override
        public Node getNode() {
            if(p1.getNode() == null) {
                if(p2.getNode() == null) {
                    return null;
                } else {
                    return p2.getNode();
                }
            } else {
                if(p2.getNode() == null) {
                    return p1.getNode();
                } else {
                    p1.getNode().insertChild(p2.getNode());
                    return p1.getNode();
                }

            }
        }

        @Override
        public  String getRest() {
            return rest;
        }
    }

    public class Or implements Primitive {
        private Primitive p1;
        private Primitive p2;
        private String rest;
        private Node node;

        public Or(Primitive p1, Primitive p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public boolean parse(String text) {
            if(p1.parse(text)) {
                node = p1.getNode();
                rest = p1.getRest();
                return true;
            }

            if(p2.parse(text)) {
                node = p2.getNode();
                rest = p2.getRest();
                return true;
            }

            return false;
        }

        @Override
        public Node getNode() {
            return node;
        }

        @Override
        public  String getRest() {
            return rest;
        }
    }

    public class Ignore implements Primitive {
        private String rest;

        @Override
        public boolean parse(String text) {
System.out.println("Ignore::parse()");
System.out.println("Ignore::parse() text=" + text);
            Pattern r = Pattern.compile("\\s+");
            Matcher m = r.matcher(text);
            if (m.find( )) {
                rest = text.substring(m.group(0).length());
                return true;
            }
            return false;
        }

        @Override
        public Node getNode() {
            return null;
        }

        @Override
        public String getRest() {
            return rest;
        }
    }

    public class Eof implements Primitive {

        @Override
        public boolean parse(String text) {
            if(text.length() == 0) return true;
            return false;
        }

        @Override
        public Node getNode() {
            return null;
        }

        @Override
        public String getRest() {
            return "";
        }
    }

    public class Literal implements Primitive {
        private Node node;
        private String pattern;
        private String rest;
        private String whole;
        public Literal(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean parse(String text) {
System.out.println("Literal::parse()");
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(text);
            if (m.find( )) {
                whole = m.group(0);
                rest = text.substring(m.group(0).length());
System.out.println("Literal::parse(), match" + m.group(0));
                return true;
            }
            return false;
        }

        @Override
        public Node getNode() {
            return null;
        }

        @Override
        public String getRest() {
            return rest;
        }

        public String getWhole() {
            return whole;
        }
    }

    public class Left implements Primitive {
        private String name;
        private String attr;
        private String pattern;
        private String rest;

        public Left(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean parse(String text) {
System.out.println("Left::parse(), text=" + text);

            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(text);

            if (m.find( )) {
                name = m.group(2);
                if(m.groupCount() > 3) {
                    attr = m.group(4);
                }
                rest = text.substring(m.group().length());
System.out.println("Left::parse(), matched, name:" + name + " attr:" + attr);
System.out.println(m.group(0));
//System.out.println(m.group(1));
//System.out.println(rest);
                return true;
            }
            return false;
        }

        @Override
        public Node getNode() {
            return null;
        }

        @Override
        public String getRest() {
            return rest;
        }

        public String getName() {
            return name;
        }

        public String getAttr() {
            return attr;
        }
    }

/*
  Value:= e | String | NodeHelper
  NodeHelper := {Node}+
  Node := <tag> + Value + </tag>
 */

    public class ValueParser implements Primitive {
        private String rest;
        private Node node;

        @Override
        public boolean parse(String text) {
            System.out.println(">>>Into ValueParser");
            if(text == "") {
                return true;
            }

            Literal p0= new Literal("^[\\w\\.\\+-]+");
            if(p0.parse(text)) {
                node = new StringNode("string", p0.getWhole());
                rest = p0.getRest();
                return true;
            }

            NodeHelperParser p1 = new NodeHelperParser();
            if(p1.parse(text)) {
                rest = p1.getRest();
                node = p1.getNode();
                return true;
            }

            return false;
        }

        @Override
        public Node getNode() {
            return node;
        }

        @Override
        public String getRest() {
            return rest;
        }
    }

    public class NodeHelperParser implements Primitive {
        private Node node;
        private String rest;

        @Override
        public boolean parse(String text) {
            System.out.println(">>>>Into NodehelperParser");

            rest = text;
            Literal p0 = new Literal("^(\\s)*<(\\w+)(\\sattr=\"(\\w+)\")?>");

            while (p0.parse(rest)) {
                NodeParser p1 = new NodeParser();
                if(!p1.parse(rest)) {
                    return false;
                }
                rest = p1.getRest();
                if(node !=null)
                    node.insertSibling(p1.getNode());
                else
                    node = p1.getNode();
            }
            return true;
        }
/*
  错误写法
  Value:= e | String | NodeHelper
  NodeHelper := Node | Node + NodeHelper
  Node := <tag> + Value + </tag>
*/
//
//        public boolean parse(String text) {
//            System.out.println(">>>>Into NodehelperParser");
//
//            NodeParser p0 = new NodeParser();
//            if(parser.parse(text)) {
//                rest = parser.getRest();
//                node = parser.getNode();
//                return true;
//            }
//
//            NodeParser p1 = new NodeParser();
//            NodeHelperParser p2 = new NodeHelperParser();
//            And parser = new And(p1, p2);
//
//            if(parser.parse(text)) {
//                rest = parser.getRest();
//                node = parser.getNode();
//                return true;
//            }
//            return false;
//        }

        @Override
        public Node getNode() {
            return node;
        }

        @Override
        public String getRest() {
            return rest;
        }
    }

    public class NodeParser implements Primitive {

        private Node node;
        private String rest;

        @Override
        public boolean parse(String text) {
            System.out.println(">>>>Into NodeParser");

            Left p1 = new Left("^(\\s)*<(\\w+)(\\sattr=\"(\\w+)\")?>");
            ValueParser p2 = new ValueParser();
            Literal p3 = new Literal("^</\\w+>");
            Ignore i1 =  new Ignore();
            Ignore i2 =  new Ignore();

            And parser = new And(p1, new Or(
                    new And(p2, new Or(p3, new And(i2, p3))),
                    new And(i1, new And(p2, new Or(p3, new And(i2, p3))))));

            if(parser.parse(text)) {
                node = new NodeNode(p1.getName());
                node.insertChild(p2.getNode());
                rest = p3.getRest();
                return true;
            }

            return false;
        }

        @Override
        public Node getNode() {
            return node;
        }

        @Override
        public String getRest() {
            return rest;
        }
    }
}
