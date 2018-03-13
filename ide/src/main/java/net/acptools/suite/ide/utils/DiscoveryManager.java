package net.acptools.suite.ide.utils;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryManager {

    private final List<Discovery> discoverers;
    private final SerialDiscovery serialDiscoverer = new SerialDiscovery();
    //private final NetworkDiscovery networkDiscoverer = new NetworkDiscovery();

    public DiscoveryManager() {
        discoverers = new ArrayList<>();
        discoverers.add(serialDiscoverer);
        //discoverers.add(networkDiscoverer);

        // Start all discoverers
        for (Discovery d : discoverers) {
            try {
                d.start();
            } catch (Exception e) {
                System.err.println("Error starting discovery method: " + d.getClass());
                e.printStackTrace();
            }
        }

        Thread closeHook = new Thread(() -> {
            for (Discovery d : discoverers) {
                try {
                    d.stop();
                } catch (Exception e) {
                    e.printStackTrace(); //just printing as the JVM is terminating
                }
            }
        });
        closeHook.setName("DiscoveryManager closeHook");
        Runtime.getRuntime().addShutdownHook(closeHook);
    }

    public SerialDiscovery getSerialDiscoverer() {
        return serialDiscoverer;
    }

    public List<BoardPort> discovery() {
        List<BoardPort> res = new ArrayList<>();
        for (Discovery d : discoverers) {
            res.addAll(d.listDiscoveredBoards());
        }
        return res;
    }

    public List<BoardPort> discovery(boolean complete) {
        List<BoardPort> res = new ArrayList<>();
        for (Discovery d : discoverers) {
            res.addAll(d.listDiscoveredBoards(complete));
        }
        return res;
    }

    public BoardPort find(String address) {
        for (BoardPort boardPort : discovery()) {
            if (boardPort.getAddress().equals(address)) {
                return boardPort;
            }
        }
        return null;
    }

    public BoardPort find(String address, boolean complete) {
        for (BoardPort boardPort : discovery(complete)) {
            if (boardPort.getAddress().equals(address)) {
                return boardPort;
            }
        }
        return null;
    }

}
