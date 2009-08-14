package info.knightrcom.util;

import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.data.metadata.LogInfo;

import java.util.UUID;

public class SystemLogger {

    /**
     * @param message
     */
    public static void log(String message) {
        
    }

    /**
     * @param name
     * @param message
     * @param info
     * @param type
     */
    public static LogInfo createLog(String caption, String message, String info, LogType type) {
        LogInfo logInfo = new LogInfo();
        logInfo.setLogId(UUID.randomUUID().toString());
        logInfo.setCaption(caption);
        if (StringHelper.isEmpty(message)) {
            logInfo.setInfo(info);
        } else {
            logInfo.setInfo(message + "\n" + info);
        }
        logInfo.setType(type.name());
        return logInfo;
    }
}
