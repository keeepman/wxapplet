package com.ymt.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EncryptionClassLoader extends ClassLoader {
    private final String key;

    public EncryptionClassLoader(String key, ClassLoader parent) {
        super(parent);
        this.key = key;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] encryptedBytes = loadEncryptedClassBytes(name);
        byte[] decryptedBytes = decrypt(encryptedBytes);
        return defineClass(name, decryptedBytes, 0, decryptedBytes.length);
    }

    private byte[] loadEncryptedClassBytes(String name) throws ClassNotFoundException {
        Path path = Paths.get(name.replace('.', '/') + ".encrypted");
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new ClassNotFoundException("Could not load encrypted class " + name, e);
        }
    }

    private byte[] decrypt(byte[] encryptedBytes) {
        // 实现解密算法，此处略去
        return encryptedBytes;
    }


}