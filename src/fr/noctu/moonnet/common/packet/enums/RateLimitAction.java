package fr.noctu.moonnet.common.packet.enums;

public enum RateLimitAction {
    SUSPEND, //wait next second to process the packet
    SKIP, //skip the packet
    DISCONNECT //disconnect the client
}
