package me.cxd.util;

import me.cxd.bean.AnnexType;
import org.apache.commons.codec.binary.Hex;
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
        char[] realHex = Hex.encodeHex(Arrays.copyOfRange(bytes, offset, length / 2 + offset));
        char[] hex = type.getHex().toLowerCase().toCharArray();
        for (int i = 0; i < hex.length; i++)
            if (hex[i] != '?' && hex[i] != realHex[i])
                return false;
        return true;
    }

    public String md5(byte[] bytes) throws IOException {
        return DigestUtils.md5Hex(bytes);
    }

    public boolean hasValidCheckSum(byte[] bytes, String hex) throws IOException {
        return md5(bytes).equalsIgnoreCase(hex);
    }
}
