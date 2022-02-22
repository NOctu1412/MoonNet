package fr.noctu.moonnet.server.client;

import fr.noctu.moonnet.common.packet.Packet;
import fr.noctu.moonnet.common.utils.LogUtils;
import fr.noctu.moonnet.server.MoonServer;
import io.netty.buffer.Unpooled;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final UUID uuid;

    private final MoonServer server;
    private final Socket socket;
    private final DataInputStream inputStream; //here we receive data
    private final DataOutputStream outputStream; //here we send data

    private long lastMs = System.currentTimeMillis();
    private int packetSent = 0;

    public ClientHandler(MoonServer server, Socket socket) throws IOException {
        this.uuid = UUID.randomUUID();
        this.server = server;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while(socket.isConnected()){
            if(socket.isClosed())
                break;

            if(System.currentTimeMillis() - 1000 > lastMs) {
                lastMs = System.currentTimeMillis();
                packetSent = 0;
            }

            try {
                if(inputStream.available() != 0){
                    //listen for packet//
                    if(!(packetSent >= server.getPacketHandler().getMaxPacketPerSecond())){ //packet rating
                        int packetLength = inputStream.readInt();
                        if(packetLength < server.getPacketHandler().getMaxPacketSize()) { //packet size filter
                            int packetId = inputStream.readInt();
                            byte[] buf = new byte[packetLength];
                            inputStream.read(buf,0, packetLength);
                            server.getPacketHandler().handlePacketServer(this, packetId, Unpooled.wrappedBuffer(buf));
                            packetSent++;
                        } else {
                            inputStream.skipBytes(packetLength);
                            if(server.isTraceBackEnabled())
                                LogUtils.logInfo("Blocked a too big packet !");
                        }
                    }else{ //packet rate action
                        switch (server.getPacketHandler().getRateLimitAction()){
                            case SKIP:
                                inputStream.skipBytes(inputStream.readInt()); //skip packet length
                                break;
                            case SUSPEND:
                                break;
                            case DISCONNECT:
                                disconnect();
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void disconnect() throws IOException {
        server.getClientsManager().getClients().remove(this);
        if(inputStream != null)
            inputStream.close();
        if(outputStream != null)
            outputStream.close();
        if(socket != null)
            socket.close();

        if(server.isTraceBackEnabled())
            LogUtils.logInfo("Client disconnected: " + uuid);
    }

    public void sendPacket(Packet packet) throws IOException {
        if(server.getPacketHandler().getPacketEncryption() != null)
            outputStream.writeInt(server.getPacketHandler().getPacketEncryption().encrypt(packet.toByteArray()).length);
        else
            outputStream.writeInt(packet.toByteArray().length);

        outputStream.writeInt(packet.getId());

        if(server.getPacketHandler().getPacketEncryption() != null){
            outputStream.write(server.getPacketHandler().getPacketEncryption().encrypt(packet.toByteArray()));
        }else{
            outputStream.write(packet.toByteArray());
        }
        outputStream.flush();
    }

    public UUID getUuid() {
        return uuid;
    }
}
