package fr.noctu.moonnet.client;

import fr.noctu.moonnet.common.packet.Packet;
import fr.noctu.moonnet.common.packet.PacketHandler;
import fr.noctu.moonnet.common.utils.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MoonClient {
    private Socket socket;
    private DataInputStream inputStream; //here we receive data
    private DataOutputStream outputStream; //here we send data

    private final PacketHandler packetHandler;

    private final String host;
    private final int port;

    private boolean connected = false;

    private long lastMs = System.currentTimeMillis();
    private int packetSent = 0;

    public MoonClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.packetHandler = new PacketHandler();
    }

    public void connect() throws IOException {
        if(!connected){
            this.socket = new Socket(host, port);
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            connected = true;
            listenForPacket();
        }else
            LogUtils.logError("Already connected to server !");
    }

    public void disconnect() throws IOException {
        if(connected){
            if(inputStream != null)
                inputStream.close();
            if(outputStream != null)
                outputStream.close();
            if(socket != null)
                socket.close();
        }else
            LogUtils.logError("Client is not connected to server !");
    }

    private void listenForPacket(){
        new Thread(() -> {
            while (socket.isConnected()){
                if(System.currentTimeMillis() - 1000 > lastMs) {
                    lastMs = System.currentTimeMillis();
                    packetSent = 0;
                }

                try {
                    if(inputStream.available() != 0){
                        if(!(packetSent >= packetHandler.getMaxPacketPerSecond())){
                            int packetLength = inputStream.readInt();
                            if(packetLength < packetHandler.getMaxPacketSize()) {
                                packetHandler.handlePacketClient(this, inputStream.readInt(), Unpooled.wrappedBuffer(inputStream.readNBytes(packetLength)));
                                packetSent++;
                            }else
                                inputStream.skipBytes(packetLength);
                        }else{
                            switch (packetHandler.getRateLimitAction()){
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

                }
            }
        }).start();
    }

    public void sendPacket(Packet packet) throws IOException {
        if(packetHandler.getPacketEncryption() != null)
            outputStream.writeInt(packetHandler.getPacketEncryption().encrypt(packet.toByteArray()).length);
        else
            outputStream.writeInt(packet.toByteArray().length);

        outputStream.writeInt(packet.getId());

        if(packetHandler.getPacketEncryption() != null){
            outputStream.write(packetHandler.getPacketEncryption().encrypt(packet.toByteArray()));
        }else{
            outputStream.write(packet.toByteArray());
        }
        outputStream.flush();
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }
}
