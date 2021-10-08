package tests.packets;

import fr.noctu.moonnet.client.MoonClient;
import fr.noctu.moonnet.common.packet.Packet;
import fr.noctu.moonnet.server.MoonServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;

public class TestPacket extends Packet {
    private int value;

    public TestPacket(){
        super(1);
        this.value = 0;
    }

    public TestPacket(int value) {
        super(1);
        this.value = value;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuf byteBuffer = Unpooled.buffer();
        byteBuffer.writeInt(value);
        return byteBuffer.array();
    }

    @Override
    public Packet fromByteArray(ByteBuf byteBuf) throws IOException {
        value = byteBuf.readInt();
        return this;
    }

    @Override
    public void executeClient(MoonClient client) {
        System.out.println("Executed on client");
    }

    @Override
    public void executeServer(MoonServer server) {
        System.out.println(value);
        try {
            server.sendPacketToAll(new TestPacket(58));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
