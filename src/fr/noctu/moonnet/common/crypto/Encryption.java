package fr.noctu.moonnet.common.crypto;

public abstract class Encryption {
    public abstract byte[] encrypt(byte[] original);
    public abstract byte[] decrypt(byte[] encrypted);
}
