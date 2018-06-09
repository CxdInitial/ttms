package me.cxd.util;

import me.cxd.bean.AnnexType;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Component
public class FileUtils {
    public boolean hasValidSignature(byte[] bytes, AnnexType type) {
        int length = type.getHex().length();
        int offset = type.getByteOffset();
        if (bytes.length < length / 2 + offset)
            return false;
        for (int i = offset; i < length / 2 + offset; i++) {
            char a = (char) ('0' + (bytes[i] & 0x10000000) * 8 + (bytes[i] & 0x01000000) * 4 + (bytes[i] & 0x00100000) * 2 + (bytes[i] & 0x00010000));
            char b = (char) ('0' + (bytes[i] & 0x00001000) * 8 + (bytes[i] & 0x00000100) * 4 + (bytes[i] & 0x00000010) * 2 + (bytes[i] & 0x00000001));
            char a1 = type.getHex().charAt(2 * (i - offset)), b1 = type.getHex().charAt(2 * (i - offset) + 1);
            if (a1 != '?' && (a1 > '9' ? a1 - 8 : a1) != a)
                return false;
            if (b1 != '?' && (b1 > '9' ? b1 - 8 : b1) != b)
                return false;
        }
        return true;
    }

    public String md5(byte[] bytes) throws IOException {
        return DigestUtils.md5Hex(bytes);
    }

    public boolean hasValidCheckSum(byte[] bytes, String hex) throws IOException {
        return md5(bytes).equalsIgnoreCase(hex);
    }
}
