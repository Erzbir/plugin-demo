package com.demo.plugin.internal.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileTypeDetector {
    private static final Map<Magic, FileType> FILE_TYPE_MAP = new HashMap<>(5);

    static {
        for (FileType fileType : FileType.values()) {
            byte[][] magic = fileType.magic;
            for (byte[] bytes : magic) {
                FILE_TYPE_MAP.put(new Magic(bytes), fileType);
            }
        }
    }

    public static boolean isClassFile(final Path path) {
        if (!Files.isRegularFile(path) || !path.toString().toLowerCase().endsWith(".class")) {
            return false;
        }
        FileType detect;
        detect = FileTypeDetector.detect(path);
        return detect.equals(FileType.CLASS);
    }

    public static boolean isJarFile(final Path path) {
        if (!Files.isRegularFile(path) || !path.toString().toLowerCase().endsWith(".jar")) {
            return false;
        }
        FileType detect;
        detect = FileTypeDetector.detect(path);
        return detect.equals(FileType.JAR) || detect.equals(FileType.ZIP);
    }

    public static FileType detect(File file) {
        try {
            return detect(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return FileType.UNKNOWN;
        }
    }

    public static FileType detect(Path path) {
        if (!Files.isRegularFile(path)) {
            return FileType.UNKNOWN;
        }
        byte[] header;
        try {
            header = getFileHeader(Files.newInputStream(path, StandardOpenOption.READ));
        } catch (IOException e) {
            return FileType.UNKNOWN;
        }
        return detect(header);
    }

    public static FileType detect(InputStream in) {
        byte[] header = getFileHeader(in);
        return detect(header);
    }

    public static FileType detect(byte[] bytes) {
        FileType fileType = FILE_TYPE_MAP.get(new Magic(bytes));
        if (fileType == null) {
            return FileType.UNKNOWN;
        }
        return fileType;
    }

    private static boolean compare(byte[] a, byte[] b) {
        return compare(a, 0, a.length, b, 0, b.length);
    }

    private static boolean compare(byte[] a, int aFrom, int aTo, byte[] b, int bFrom, int bTo) {
        return Arrays.equals(a, aFrom, aTo, b, bFrom, bTo);
    }

    public static boolean compare(byte[] bytes, FileType type) {
        byte[][] magics = type.magic;
        for (byte[] magic : magics) {
            if (magic.length > bytes.length) {
                return false;
            }
            if (magic.length < bytes.length) {
                return compare(magic, 0, magics.length, bytes, 0, magics.length);
            }
            if (compare(magic, bytes)) {
                return true;
            }
        }
        return false;
    }

    private static byte[] getFileHeader(InputStream in) {
        byte[] header = new byte[4];
        try {
            int read = in.read(header);
            in.close();
            if (read < 4) {
                header = new byte[]{0x00};
            }
        } catch (IOException e) {
            return new byte[]{0x00};
        }
        return header;
    }

    public enum FileType {
        CLASS(new byte[][]{{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE}}),
        JAR(new byte[][]{{0x50, 0x4B, 0x03, 0x04}}),
        ZIP(new byte[][]{{0x50, 0x4B, 0x03, 0x04}}),
        PDF(new byte[][]{{0x25, 0x50, 0x44, 0x46}}),
        UNKNOWN(new byte[][]{{0x00}});

        public final byte[][] magic;

        FileType(byte[][] magic) {
            this.magic = magic;
        }
    }

    private record Magic(byte[] magic) {

        @Override
        public int hashCode() {
            return Arrays.hashCode(magic);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Magic)) {
                return false;
            }
            return this.hashCode() == obj.hashCode();
        }
    }
}