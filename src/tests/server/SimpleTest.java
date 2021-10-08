package tests.server;

import fr.noctu.moonnet.common.crypto.impl.AESEncryption;
import fr.noctu.moonnet.common.packet.enums.RateLimitAction;
import fr.noctu.moonnet.server.MoonServer;
import tests.packets.TestPacket;

import java.io.IOException;

public class SimpleTest {
    public static void main(String[] args) throws IOException {
        MoonServer server = new MoonServer("localhost", 4003, 1);
        server.getPacketHandler().setPacketEncryption(new AESEncryption("password")); //set packet encryption
        server.getPacketHandler().setRateLimitAction(RateLimitAction.DISCONNECT); //set packet rate limit action

        server.getPacketHandler().addPacket(new TestPacket(69)); //register a packet in the server

        server.startServer();
    }
}
