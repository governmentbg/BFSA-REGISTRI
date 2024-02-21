package bg.bulsi.bfsa.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TextFileUtil {

    private TextFileUtil() {
        // empty constructor, because this is "utility" class with only static methods
    }

    private static final String TAB_DELIMITER = "\\t";

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final Consumer<List<String>> lineConsumer
    ) {
        processFileByLine(
                filename,
                in,
                delimiter,
                0,
                null,
                lineConsumer
        );
    }

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final List<String> headerNames,
            final Consumer<List<String>> lineConsumer
    ) {
        processFileByLine(
                filename,
                in,
                delimiter,
                0,
                headerNames,
                lineConsumer
        );
    }

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final int limit,
            final Consumer<List<String>> lineConsumer
    ) {
        processFileByLine(
                filename,
                in,
                delimiter,
                limit,
                null,
                lineConsumer
        );
    }

    public static void processFileByLine(
            final String filename,
            final InputStream in,
            final String delimiter,
            final int limit,
            final List<String> headerNames,
            final Consumer<List<String>> lineConsumer
    ) {
        if (null == in || null == lineConsumer) {
            return;
        }
        try(final Scanner scanner = new Scanner(in, "utf8")) {
            while (scanner.hasNext()) {
                final String originalLine = scanner.nextLine();
                final List<String> line = List.of(originalLine.split(delimiter, limit))
                        .stream().map(s -> {
                            if (TAB_DELIMITER.equals(delimiter)) {
                                return s;
                            }
                            return s.replaceAll("\"", "");
                        })
                        .collect(Collectors.toList());
    
                final long notEmptyItemsCount = line.stream()
                        .filter(item -> !item.trim().isEmpty())
                        .distinct()
                        .count();
    
                if (originalLine.isEmpty()
                        || null != headerNames && headerNames.equals(line)
                        || notEmptyItemsCount < 1) {
                    continue;
                }
    
                try {
                    lineConsumer.accept(line);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException(
                            ex.getMessage() + "\n"
                            + " filename=" + filename + "\n"
                            + " delimiter=" + delimiter + "\n"
                            + " original line=" + originalLine + "",
                            ex
                    );
                }
            }
        }
    }

    public static String valueAtIndex(final List<String> line, int index) {
        return (null != line && line.size() > index) ? line.get(index).trim() : "";
    }

    public static byte[] getFileMD5CheckSum(InputStream is) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");

            final DigestInputStream dis = new DigestInputStream(is, md);
            while (dis.read() != -1) {
                //do nothing
            }
            dis.close();
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new TextFileUtilException("MD5 is not known MessageDigest algorithm.", e);
        } catch (IOException e) {
            throw new TextFileUtilException(e);
        }
        
    }

    public static String getFileMD5CheckSumAsString(InputStream is) {
        byte[] bytes = getFileMD5CheckSum(is);
        StringBuilder sb = new StringBuilder();

        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
    
    public static class TextFileUtilException extends RuntimeException {

        @Serial
        private static final long serialVersionUID = -9055950025514332220L;

        TextFileUtilException(String message, Throwable cause) {
            super(message, cause);
        }

        TextFileUtilException(Throwable cause) {
            super(cause);
        }
    }
}
