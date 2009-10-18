package info.knightrcom.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ModelDescriptor {

    private String platform;

    private String lobby;

    private String room;

    private String maxPlayerNumber;

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform
     *            the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the lobby
     */
    public String getLobby() {
        return lobby;
    }

    /**
     * @param lobby
     *            the lobby to set
     */
    public void setLobby(String lobby) {
        this.lobby = lobby;
    }

    /**
     * @return the room
     */
    public String getRoom() {
        return room;
    }

    /**
     * @param room
     *            the room to set
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * @return the maxPlayerNumber
     */
    public String getMaxPlayerNumber() {
        return maxPlayerNumber;
    }

    /**
     * @param maxPlayerNumber
     *            the maxPlayerNumber to set
     */
    public void setMaxPlayerNumber(String maxPlayerNumber) {
        this.maxPlayerNumber = maxPlayerNumber;
    }

    public static ModelDescriptor loadConfigInfo() throws IOException {
        XStream s = new XStream(new DomDriver());
        s.setMode(XStream.NO_REFERENCES);
        System.out.println(s.toXML(new ModelDescriptor()));
        ModelDescriptor desc = new ModelDescriptor();
        InputStreamReader rawReader = new InputStreamReader((InputStream) ClassLoader.getSystemResource("info/knightrcom/util/model_defination.xml").getContent());
        BufferedReader reader = new BufferedReader(rawReader);
        StringBuilder content = new StringBuilder();
        String lineContent = reader.readLine();
        while (lineContent != null) {
            content.append(lineContent);
            lineContent = reader.readLine();
        }
        System.out.println(content.toString());
        desc = (ModelDescriptor) s.fromXML(content.toString());
        System.out.println(desc.platform);
        return desc;
    }
}
