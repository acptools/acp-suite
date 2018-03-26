package net.acptools.suite.ide.utils;

import java.io.IOException;

public class ProcessUtils {

    public interface ProccessRunnableInterface {
        void run(Process proccess);
    }

    public static int run(String proccess, ProccessRunnableInterface onProgress) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(proccess);
            new Thread(() -> onProgress.run(pr)).start();
            pr.waitFor();
            return pr.exitValue();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -2;
        }
    }
}
