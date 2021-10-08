package fr.noctu.moonnet.server;

import fr.noctu.moonnet.common.utils.LogUtils;
import fr.noctu.moonnet.common.packet.Packet;
import fr.noctu.moonnet.common.packet.PacketHandler;
import fr.noctu.moonnet.server.client.ClientHandler;
import fr.noctu.moonnet.server.managers.ClientsManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class MoonServer {
    private final ServerSocket serverSocket;
    private final String host;
    private final int port, maxClientCount;

    private final ClientsManager clientsManager;
    private final PacketHandler packetHandler;

    private boolean traceBack;

    private boolean runningState = false;

    //CONSTRUCTORS//
    public MoonServer(String host, int port, int maxClientCount, boolean traceBack) throws IOException {
        this(host, port, maxClientCount);
        this.traceBack = traceBack;
    }

    public MoonServer(String host, int port, int maxClientCount) throws IOException {
        this.serverSocket = new ServerSocket(port, maxClientCount, InetAddress.getByName(host));
        this.host = host;
        this.port = port;
        this.maxClientCount = maxClientCount;
        this.clientsManager = new ClientsManager(this, serverSocket);
        this.traceBack = true;
        this.packetHandler = new PacketHandler(this);
    }

    //SERVER FUNCTIONS//
    public void startServer() throws IOException {
        if(runningState)
            LogUtils.logError("Server is already running !");
        else{
            runningState = true;
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                if(traceBack)
                    LogUtils.logSuccess("MoonClient connected !");

                ClientHandler client = new ClientHandler(this, socket);
                clientsManager.getClients().add(client);
                new Thread(client).start();
            }
        }
    }

    public void stopServer() throws IOException {
        if(!runningState)
            LogUtils.logError("Server is not running !");
        else{
            runningState = false;
            serverSocket.close();
        }
    }

    public void sendPacketTo(UUID client, Packet packet) throws IOException {
        clientsManager.getClient(client).sendPacket(packet);
    }

    public void sendPacketToAll(Packet packet) throws IOException {
        for (ClientHandler client : clientsManager.getClients()) {
            client.sendPacket(packet);
        }
    }

    //UTILITIES//
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getMaxClientCount() {
        return maxClientCount;
    }

    public ClientsManager getClientsManager() {
        return clientsManager;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public boolean isTraceBackEnabled() {
        return traceBack;
    }

    public void setTraceBackEnabled(boolean state){
        this.traceBack = state;
    }

    public boolean isRunning() {
        return runningState;
    }
}
