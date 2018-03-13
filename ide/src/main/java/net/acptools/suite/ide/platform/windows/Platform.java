package net.acptools.suite.ide.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public class Platform extends net.acptools.suite.ide.platform.Platform {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public String getName() {
        return "Windows";
    }

    // Need to extend com.sun.jna.platform.win32.User32 to access
    // Win32 function GetDpiForSystem()
    interface ExtUser32 extends StdCallLibrary, com.sun.jna.platform.win32.User32 {
        ExtUser32 INSTANCE = (ExtUser32) Native.loadLibrary("user32", ExtUser32.class, W32APIOptions.DEFAULT_OPTIONS);

        public int GetDpiForSystem();

        public int SetProcessDpiAwareness(int value);

        public final int DPI_AWARENESS_INVALID = -1;
        public final int DPI_AWARENESS_UNAWARE = 0;
        public final int DPI_AWARENESS_SYSTEM_AWARE = 1;
        public final int DPI_AWARENESS_PER_MONITOR_AWARE = 2;

        public Pointer SetThreadDpiAwarenessContext(Pointer dpiContext);

        public final Pointer DPI_AWARENESS_CONTEXT_UNAWARE = new Pointer(-1);
        public final Pointer DPI_AWARENESS_CONTEXT_SYSTEM_AWARE = new Pointer(-2);
        public final Pointer DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE = new Pointer(-3);
    }

    private static int detected = detectSystemDPI();

    @Override
    public int getSystemDPI() {
        if (detected == -1)
            return super.getSystemDPI();
        return detected;
    }

    public static int detectSystemDPI() {
        try {
            ExtUser32.INSTANCE.SetProcessDpiAwareness(ExtUser32.DPI_AWARENESS_SYSTEM_AWARE);
        } catch (Throwable e) {
            // Ignore error
        }
        try {
            ExtUser32.INSTANCE.SetThreadDpiAwarenessContext(ExtUser32.DPI_AWARENESS_CONTEXT_SYSTEM_AWARE);
        } catch (Throwable e) {
            // Ignore error (call valid only on Windows 10)
        }
        try {
            return ExtUser32.INSTANCE.GetDpiForSystem();
        } catch (Throwable e) {
            // DPI detection failed, fall back with default
            System.out.println("DPI detection failed, fallback to 96 dpi");
            return -1;
        }
    }
}
