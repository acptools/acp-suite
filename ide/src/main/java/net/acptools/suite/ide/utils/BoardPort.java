package net.acptools.suite.ide.utils;

public class BoardPort {

    private String address;
    private String protocol;
    private String boardName;
    private String vid;
    private String pid;
    private String iserial;
    private String label;
    private boolean online;

    public BoardPort() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }


    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setOnlineStatus(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public void setVIDPID(String vid, String pid) {
        this.vid = vid;
        this.pid = pid;
    }

    public String getVID() {
        return vid;
    }

    public String getPID() {
        return pid;
    }

    public void setISerial(String iserial) {
        this.iserial = iserial;
    }

    public String getISerial() {
        return iserial;
    }

    @Override
    public String toString() {
        return this.address + "_" + this.vid + "_" + this.pid;
    }
}
