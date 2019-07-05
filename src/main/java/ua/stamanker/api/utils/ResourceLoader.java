package ua.stamanker.api.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class ResourceLoader {

    public String load(String path) {
        return new String(loadBinary(path));
    }

    public byte[] loadBinary(String path) {
        try {
            try(InputStream inputStream = ResourceLoader.class.getResourceAsStream(path)) {
                return readFromInputStreamBinary(inputStream);
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private byte[] readFromInputStreamBinary(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int b;
            while ((b = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, b);
            }
            return result.toByteArray();
        }
    }

}