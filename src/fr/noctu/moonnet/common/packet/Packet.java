package fr.noctu.moonnet.common.packet;

import fr.noctu.moonnet.client.MoonClient;
import fr.noctu.moonnet.server.MoonServer;
import fr.noctu.moonnet.server.client.ClientHandler;
import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Packet {
    private final int id;

    public Packet(int id) {
        this.id = id;
    }

    public abstract byte[] toByteArray();
    public abstract Packet fromByteArray(ByteBuf buffer) throws IOException;

    public abstract void executeClient(MoonClient client); //what packet is going to do on client
    public abstract void executeServer(MoonServer server, ClientHandler from); //what packet is going to do on server

    public int getId() {
        return id;
    }
}
