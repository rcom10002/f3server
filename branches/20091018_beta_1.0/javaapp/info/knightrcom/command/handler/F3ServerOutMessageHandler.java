package info.knightrcom.command.handler;

import info.knightrcom.command.message.EchoMessage;

import static info.knightrcom.util.HandlerDispatcher.respondMessage;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;



public class F3ServerOutMessageHandler implements MessageHandler<EchoMessage> {

    public void handleMessage(IoSession session, EchoMessage message) throws Exception {
        System.out.println(respondMessage(message));
    }

}
