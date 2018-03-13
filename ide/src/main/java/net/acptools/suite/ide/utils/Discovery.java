package net.acptools.suite.ide.utils;

import java.util.List;

public interface Discovery {

    /**
     * Start discovery service
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * Stop discovery service
     */
    void stop() throws Exception;

    /**
     * Return the list of discovered ports.
     *
     * @return
     */
    List<BoardPort> listDiscoveredBoards();

    List<BoardPort> listDiscoveredBoards(boolean complete);

}
