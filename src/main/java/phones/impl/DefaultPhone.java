package phones.impl;

import phones.PhoneStatus;
import phones.system.*;

public class DefaultPhone implements ConnectedPhone {

    private final PhoneSocket socket;
    private PhoneStatus status = PhoneStatus.IDLE;
    private String lastMessage = null;
    private CallOutgoing outgoing = null;
    private CallIncoming incoming = null;
    private Call call = null;

    public DefaultPhone(PhoneSocket socket) {
        this.socket = socket;
    }

    @Override
    public String getNumber() {
        return socket.getNumber();
    }

    @Override
    public void dial(String number) {
        outgoing = socket.call(number, this::onCallReject, this::onCallAccept, this::onMessageReceive, this::onCallEnd);
    }

    @Override
    public void pushGreen() {
        if (status == PhoneStatus.RINGING) {
            call = incoming.accept(this::onMessageReceive, this::onCallEnd);
            onCallAccept(call);
        } else {
            status = PhoneStatus.CALLING;
        }
    }

    @Override
    public void pushRed() {
    }

    @Override
    public void send(String message) {
        call.send(message);
    }

    @Override
    public void receive(CallIncoming request) {
        status = PhoneStatus.RINGING;
        incoming = request;
    }

    @Override
    public void canceled(CallIncoming request) {
    }

    protected void onCallReject(RejectReason reason) {
    }

    protected void onCallAccept(Call call) {
        status = PhoneStatus.IN_CALL;
        this.call = call;
    }

    protected void onMessageReceive(String message) {
        lastMessage = message;
    }

    protected void onCallEnd() {
    }

    @Override
    public PhoneStatus getStatus() {
        return status;
    }

    @Override
    public String getLastMessage() {
        String m = lastMessage;
        lastMessage = null;
        return m;
    }

    @Override
    public String toString() {
        return "Phone[" + getNumber() + "]";
    }
}
