package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ChatMessageNetEvent extends NetEvent {
    public String message;

    public ChatMessageNetEvent( String message) {
        this.message = message;
    }

    public ChatMessageNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeUTF(message);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        message = dis.readUTF();

    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.CHAT_MESSAGE;
    }

    @Override
    public String toString() {
        return "ChatMessage[authorID="+authorID+", message=\""+message+"\"]";
    }
}
