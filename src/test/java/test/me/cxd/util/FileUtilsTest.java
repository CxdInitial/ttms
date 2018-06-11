package test.me.cxd.util;

import me.cxd.bean.AnnexType;
import me.cxd.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class FileUtilsTest {
    private final FileUtils fileUtils;

    FileUtilsTest() {
        this.fileUtils = new FileUtils();
    }

    private static List<AnnexType> docx;

    @BeforeAll
    static void setup() {
        AnnexType type = new AnnexType();
        type.setSuffix("docx");
        type.setByteOffset(0);
        type.setHex("504B0304");
        AnnexType type1 = new AnnexType();
        type1.setSuffix("docx");
        type1.setByteOffset(0);
        type1.setHex("504B0708");
        docx = Arrays.asList(type, type1);
    }

    @ParameterizedTest
    @ValueSource(strings = "test.docx")
    void validate_valid(String filename) throws IOException {
        boolean valid = false;
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        System.out.println(file.getAbsolutePath());
        for (AnnexType type : docx) {
            if (fileUtils.hasValidSignature(Files.readAllBytes(file.toPath()), type)) {
                valid = true;
                break;
            }
        }
        Assertions.assertTrue(valid);
    }

    @ParameterizedTest
    @ValueSource(strings = "txt.docx")
    void validate_invalid(String filename) throws IOException {
        boolean valid = false;
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        System.out.println(file.getAbsolutePath());
        for (AnnexType type : docx) {
            if (fileUtils.hasValidSignature(Files.readAllBytes(file.toPath()), type)) {
                valid = true;
                break;
            }
        }
        Assertions.assertFalse(valid);
    }
}
