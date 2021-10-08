package tests.client;

import fr.noctu.moonnet.client.MoonClient;
import fr.noctu.moonnet.common.crypto.impl.AESEncryption;
import tests.packets.TestPacket;

import java.io.IOException;

public class SimpleTest {
    public static void main(String[] args) throws IOException {
        MoonClient client = new MoonClient("localhost", 4003);
        client.getPacketHandler().setPacketEncryption(new AESEncryption("password")); //set packet encryption

        client.getPacketHandler().addPacket(new TestPacket()); //register a packet

        client.connect(); //connect to server

        client.sendPacket(new TestPacket(69)); //send packet to the server
        client.sendPacket(new TestPacket(667));
        client.sendPacket(new TestPacket(1412));
    }
}
