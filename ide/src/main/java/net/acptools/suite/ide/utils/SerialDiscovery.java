package net.acptools.suite.ide.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

public class SerialDiscovery implements Discovery {

    private Timer serialBoardsListerTimer;
    private final List<BoardPort> serialBoardPorts;
    private SerialBoardsLister serialBoardsLister = new SerialBoardsLister(this);

    public SerialDiscovery() {
        this.serialBoardPorts = new LinkedList<>();
    }

    @Override
    public List<BoardPort> listDiscoveredBoards() {
        return getSerialBoardPorts(false);
    }

    @Override
    public List<BoardPort> listDiscoveredBoards(boolean complete) {
        return getSerialBoardPorts(complete);
    }

    private List<BoardPort> getSerialBoardPorts(boolean complete) {
        if (complete) {
            return new LinkedList<>(serialBoardPorts);
        }
        List<BoardPort> onlineBoardPorts = new LinkedList<>();
        for (BoardPort port : serialBoardPorts) {
            if (port.isOnline() == true) {
                onlineBoardPorts.add(port);
            }
        }
        return onlineBoardPorts;
    }

    public void setSerialBoardPorts(List<BoardPort> newSerialBoardPorts) {
        serialBoardPorts.clear();
        serialBoardPorts.addAll(newSerialBoardPorts);
    }

    public void forceRefresh() {
        serialBoardsLister.retriggerDiscovery(false);
    }

    public void setUploadInProgress(boolean param) {
        serialBoardsLister.uploadInProgress = param;
    }

    public void pausePolling(boolean param) {
        serialBoardsLister.pausePolling = param;
    }

    @Override
    public void start() {
        this.serialBoardsListerTimer = new Timer(SerialBoardsLister.class.getName());
        serialBoardsLister.start(serialBoardsListerTimer);
    }

    @Override
    public void stop() {
        this.serialBoardsListerTimer.purge();
    }
}
