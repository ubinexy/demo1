import org.apache.thrift.TException;
import tutorial.Calculator;

public class CalculatorHandler implements Calculator.Iface {
    public void ping() {
        System.out.println("ping()");
    }

    @Override
    public int calculate(int num1, int num2) throws TException {
        return 0;
    }
}