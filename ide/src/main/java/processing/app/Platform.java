package processing.app;

import net.acptools.suite.ide.App;

import java.io.File;

public class Platform {
    static {
        loadLib(new File(App.getContentFile("lib"), System.mapLibraryName("listSerialsj")));
    }

    private static void loadLib(File lib) {
        try {
            System.load(lib.getAbsolutePath());
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Cannot load native library " + lib.getAbsolutePath());
            System.out.println("The program has terminated!");
            System.exit(1);
        }
    }

    public native String resolveDeviceAttachedToNative(String serial);

    public native String[] listSerialsNative();
}
