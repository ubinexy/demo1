import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.*;

import tutorial.Calculator;
import Xml.Parser;
import Xml.Visitor;

public class JavaClient {

    public static void main(String[] args) {
        if(args.length == 1) {
            show_table();
            return;
        } else if(args.length == 4 && args[0]=="-h") {
            connect(args[1], Integer.valueOf(args[3]));
            return;
        } else if(args.length == 6) {
            send(args[1], Integer.valueOf(args[3]), args[5]);
            return;
        } else if(args.length == 2) {
            String content = read(args[1]);
            Parser p = new Parser();
            if(p.parse(content)) {
                Visitor v = new Visitor();
                v.visit(p.getNode(), "");
                connect(v.getServerIP(), v.getServerPort());
                return;
            }
            System.exit(2);
        } else if(args.length == 3) {
            String content = read(args[2]);
            Parser p = new Parser();
            if(p.parse(content)) {
                Visitor v = new Visitor();
                v.visit(p.getNode(), "");
                send(v.getServerIP(), v.getServerPort(), content);
                System.out.println("running...");
                return;
            } else {
                System.out.println("parse error");
            }
            System.exit(2);
        }

        System.out.println("Usage:");
        System.out.println("Application [--device]");
        System.out.println("Application [-h] host [-p] port");
        System.out.println("Application [-h] host [-p] port [-f] filePath");
        System.out.println("Application [--device] filePath");
        System.out.println("Application [--device] [--run] filePath");

        System.exit(1);
    }

    private static void connect(String host, Integer port){

        try {
            TTransport transport;
            transport = new TSocket(host, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Calculator.Client client = new Calculator.Client(protocol);

            System.out.println(client.calculate(16, 32));

            transport.close();
        } catch(TException x) {
            x.printStackTrace();
            System.exit(1);
        }
    }

    private static void perform(Calculator.Client client) throws TException {
        client.ping();
        System.out.println(client.calculate(16, 32));
        client.commParameters("hello");

        System.out.println("ping()");
    }

    private static void show_table() {
        System.out.println("|   设备   |        IP       | Port |");
        System.out.println("| -------- | --------------- | ---- |");
        System.out.println("| Mac-Pro  | 192.168.199.249 | 9090 |");
        System.out.println("| juanjuan | 192.168.199.123 | 9090 |");
        System.out.println("| MiaoBoya | 192.168.199.232 | 9090 |");
    }

    private static void send(String host, Integer port, String message)  {

        try {
            TTransport transport;
            transport = new TSocket(host, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Calculator.Client client = new Calculator.Client(protocol);

            client.commParameters(message);

            transport.close();
        } catch(TException x) {
            x.printStackTrace();
            System.exit(1);
        }
    }

    private static String read(String fileName) {
        StringBuffer strBuf = null;
        try {
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempStr;
            strBuf = new StringBuffer();
            while ((tempStr = reader.readLine()) != null) {
                strBuf.append(tempStr);
                strBuf.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return strBuf.toString();
    }
}