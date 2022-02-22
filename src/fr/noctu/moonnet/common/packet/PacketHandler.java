package fr.noctu.moonnet.common.packet;

import fr.noctu.moonnet.client.MoonClient;
import fr.noctu.moonnet.common.crypto.Encryption;
import fr.noctu.moonnet.common.packet.enums.RateLimitAction;
import fr.noctu.moonnet.common.utils.LogUtils;
import fr.noctu.moonnet.server.MoonServer;
import fr.noctu.moonnet.server.client.ClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.ArrayList;

public class PacketHandler {
    private final MoonServer server;
    private final ArrayList<Packet> packets = new ArrayList<>(); //registered packets

    private Encryption encryption = null;

    private int maxPacketSize = 100000;
    private int maxPacketPerSecond = 35;
    private RateLimitAction rateLimitAction = RateLimitAction.SKIP;

    public PacketHandler(){
        this.server = null;
    }

    public PacketHandler(MoonServer server){
        this.server = server;
    }

    public void addPacket(Packet packet){
        for (Packet p : packets) {
            if(packet.getId() == p.getId()){
                LogUtils.logError("Packet not registered due to duplicate id: " + packet.getId());
                return;
            }
        }
        packets.add(packet);
    }

    public void handlePacketServer(ClientHandler client, int packetId, ByteBuf byteBuf) throws IOException {
        for (Packet packet : packets) {
            if(packet.getId() == packetId) {
                if(encryption != null)
                    packet.fromByteArray(Unpooled.wrappedBuffer(encryption.decrypt(byteBuf.array()))).executeServer(server, client);
                else
                    packet.fromByteArray(byteBuf).executeServer(server, client);
            }
        }
    }

    public void handlePacketClient(MoonClient client, int packetId, ByteBuf byteBuf) throws IOException {
        for (Packet packet : packets) {
            if(packet.getId() == packetId) {
                if(encryption != null)
                    packet.fromByteArray(Unpooled.wrappedBuffer(encryption.decrypt(byteBuf.array()))).executeClient(client);
                else
                    packet.fromByteArray(byteBuf).executeClient(client);
            }
        }
    }

    public Encryption getPacketEncryption() {
        return encryption;
    }

    public void setPacketEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public void removePacketEncryption(){
        this.encryption = null;
    }

    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    public void setMaxPacketSize(int maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
    }

    public int getMaxPacketPerSecond() {
        return maxPacketPerSecond;
    }

    public void setMaxPacketPerSecond(int maxPacketPerSecond) {
        this.maxPacketPerSecond = maxPacketPerSecond;
    }

    public RateLimitAction getRateLimitAction() {
        return rateLimitAction;
    }

    public void setRateLimitAction(RateLimitAction rateLimitAction) {
        this.rateLimitAction = rateLimitAction;
    }
}