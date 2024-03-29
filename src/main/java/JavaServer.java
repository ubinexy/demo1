import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import tutorial.Calculator;

public class JavaServer {

    public static CalculatorHandler handler;
    public static Calculator.Processor processor;

    public static void main(String [] args) {
        try {
            handler = new CalculatorHandler(args[0], args[1]);
            processor = new Calculator.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };
            new Thread(simple).start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void simple(Calculator.Processor processer) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processer));

            System.out.println("Starting the simple server");
            server.serve();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}