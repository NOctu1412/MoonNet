package fr.noctu.moonnet.server.managers;

import fr.noctu.moonnet.server.MoonServer;
import fr.noctu.moonnet.server.client.ClientHandler;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.UUID;

public class ClientsManager {
    private final MoonServer server;
    private final ServerSocket serverSocket;

    private final ArrayList<ClientHandler> clients;

    public ClientsManager(MoonServer server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
        clients = new ArrayList<>();
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }

    public ClientHandler getClient(UUID uuid){
        for (ClientHandler client : clients) {
            if(client.getUuid() == uuid)
                return client;
        }
        return null;
    }
}
