import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import tutorial.Calculator;

public class JavaClient {
    public static void main(String[] args) {

        try {
            TTransport transport;
            transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Calculator.Client client = new Calculator.Client(protocol);

            perform(client);

            transport.close();
        } catch(TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(Calculator.Client client) throws TException {
        client.ping();
        System.out.println(client.calculate(16, 32));
        client.commParameters("hello");

        System.out.println("ping()");
    }
}