package net.acptools.suite.ide.utils;

import net.acptools.suite.ide.App;
import net.acptools.suite.ide.platform.Platform;
import net.acptools.suite.ide.App;
import net.acptools.suite.ide.platform.Platform;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SerialBoardsLister extends TimerTask {

    private final SerialDiscovery serialDiscovery;
    private final List<BoardPort> boardPorts = new LinkedList<>();
    private List<String> oldPorts = new LinkedList<>();
    public boolean uploadInProgress = false;
    public boolean pausePolling = false;
    private BoardPort oldUploadBoardPort = null;

    public SerialBoardsLister(SerialDiscovery serialDiscovery) {
        this.serialDiscovery = serialDiscovery;
    }

    public void start(Timer timer) {
        timer.schedule(this, 0, 1000);
    }

    public synchronized void retriggerDiscovery(boolean polled) {
        Platform platform = App.getPlatform();
        if (platform == null) {
            return;
        }

        if (polled && pausePolling) {
            return;
        }

        List<String> ports = platform.listSerials();
        if (ports.equals(oldPorts)) {
            return;
        }

        // if (updating) {}
        // a port will disappear, another will appear
        // use this information to "merge" the boards
        // updating must be signaled by SerialUpload class

        oldPorts.clear();
        oldPorts.addAll(ports);

        for (BoardPort board : boardPorts) {
            if (ports.contains(board.toString())) {
                if (board.isOnline()) {
                    ports.remove(ports.indexOf(board.toString()));
                }
            } else {
                if (uploadInProgress && board.isOnline()) {
                    oldUploadBoardPort = board;
                }
                board.setOnlineStatus(false);
            }
        }

        for (String newPort : ports) {

            String[] parts = newPort.split("_");

            if (parts.length < 3) {
                // something went horribly wrong
                continue;
            }

            if (parts.length > 3) {
                // port name with _ in it (like CP2102 on OSX)
                for (int i = 1; i < (parts.length - 2); i++) {
                    parts[0] += "_" + parts[i];
                }
                parts[1] = parts[parts.length - 2];
                parts[2] = parts[parts.length - 1];
            }

            String port = parts[0];

            BoardPort boardPort = null;
            boolean updatingInfos = false;
            int i = 0;
            // create new board or update existing
            for (BoardPort board : boardPorts) {
                if (board.toString().equals(newPort)) {
                    updatingInfos = true;
                    boardPort = boardPorts.get(i);
                    break;
                }
                i++;
            }
            if (!updatingInfos) {
                boardPort = new BoardPort();
            }
            boardPort.setAddress(port);
            boardPort.setProtocol("serial");
            boardPort.setOnlineStatus(true);

            String label = port;

            if (OSUtils.isWindows()) {
                boardPort.setVIDPID(parts[1], parts[2]);
                boardPort.setISerial("");
            }

            boardPort.setLabel(label);
            if (!updatingInfos) {
                boardPorts.add(boardPort);
            }
        }
        serialDiscovery.setSerialBoardPorts(boardPorts);
    }

    @Override
    public void run() {
        //if (BaseNoGui.packages == null) {
        //    return;
        //}
        retriggerDiscovery(true);
    }
}
