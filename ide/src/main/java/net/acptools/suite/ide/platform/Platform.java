package net.acptools.suite.ide.platform;

import net.acptools.suite.ide.utils.BoardPort;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Platform {

    public void setLookAndFeel() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    public void init() throws Exception {
    }

    private String resolveDeviceAttachedToNative(String serial) {
        processing.app.Platform p = new processing.app.Platform();
        return p.resolveDeviceAttachedToNative(serial);
    }

    private String[] listSerialsNative() {
        processing.app.Platform p = new processing.app.Platform();
        return p.listSerialsNative();
    }

    public String preListAllCandidateDevices() {
        return null;
    }

    public List<String> listSerials() {
        return new ArrayList<>(Arrays.asList(listSerialsNative()));
    }

    public List<String> listSerialsNames() {
        List<String> list = new LinkedList<>();
        for (String port : listSerialsNative()) {
            list.add(port.split("_")[0]);
        }
        return list;
    }

    public static class BoardCloudAPIid {
        public BoardCloudAPIid() {
        }

        private String name;
        private String architecture;
        private String id;

        public String getName() {
            return name;
        }

        public String getArchitecture() {
            return architecture;
        }

        public String getId() {
            return id;
        }

        public void setName(String tmp) {
            name = tmp;
        }

        public void setArchitecture(String tmp) {
            architecture = tmp;
        }

        public void setId(String tmp) {
            id = tmp;
        }
    }

    public String getName() {
        return "Other platform";
    }

    public List<BoardPort> filterPorts(List<BoardPort> ports, boolean aBoolean) {
        return new LinkedList<>(ports);
    }

    public String getOsName() {
        return System.getProperty("os.name");
    }

    public String getOsArch() {
        return System.getProperty("os.arch");
    }

    public int getSystemDPI() {
        return 96;
    }

}
