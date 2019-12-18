import org.apache.thrift.TException;
import tutorial.Calculator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CalculatorHandler implements Calculator.Iface {

    private final String dirName;
    private final String appName;
    private final String apiToken;
    private final String userId;


    CalculatorHandler(String dirName, String appName) {
//        this.dirName = "/Users/shiqi/Desktop/";
//        this.appName = "application";
        this.dirName = dirName;
        this.appName = appName;
        this.apiToken = System.getenv("PUSH_API_TOKEN");
        this.userId = System.getenv("PUSH_USER_ID");
    }

    @Override
    public void ping() {
        System.out.println("ping()");
    }

    @Override
    public void commParameters(String msg) {
        saveParameters(msg);

        int status = runScript();

        System.out.println("status: " + status);

        if(status == 0) {
            Notify.builderWithApiToken(apiToken)
                    .setUserId(userId)
                    .setMessage("job finished")
                    .push();
        } else {
            Notify.builderWithApiToken(apiToken)
                    .setUserId(userId)
                    .setMessage("job failed [" + status + "]")
                    .push();
        }
    }

    @Override
    public int calculate(int num1, int num2) {
        return num1 + num2;
    }



    private void saveParameters(String msg) {
        try {
            File file = new File(dirName + "parameters.xml");
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(msg);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int runScript() {
        String[] cmdarray = {dirName + appName, dirName + "parameters.xml", "data"};

        int exitValue = -1;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmdarray);
            exitValue = process.waitFor();
            if( 0 == exitValue ) {
                System.out.println("execute script success.");
            } else {
                System.out.println("execute script failed: exitValue = " + exitValue);
            }
        } catch (IOException execErr) {
            System.out.println("execute script failed with exception: exitValue = " + exitValue);
            exitValue = -400;
        } catch (InterruptedException waitForErr) {
            System.out.println("execute script failed with exception: exitValue = " + exitValue);
            exitValue = -500;
        }
        return exitValue;
    }
}