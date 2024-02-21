package bg.bulsi.bfsa.util;

public class ImageValidator {
    public static boolean isPng(byte[] fileBytes) {

        // Check for PNG signature (first 8 bytes)
        if (fileBytes != null && fileBytes.length >= 8 && fileBytes[0] == (byte) 0x89 &&
                fileBytes[1] == (byte) 0x50 && fileBytes[2] == (byte) 0x4E &&
                fileBytes[3] == (byte) 0x47 && fileBytes[4] == (byte) 0x0D &&
                fileBytes[5] == (byte) 0x0A && fileBytes[6] == (byte) 0x1A &&
                fileBytes[7] == (byte) 0x0A) {
            return true; // It's a PNG image
        }

        return false; // It's not a PNG or JPG image
    }

    public static boolean isJpg(byte[] fileBytes) {

        // Check for JPG signature (first 2 bytes)
        if (fileBytes != null && fileBytes.length >= 2 && fileBytes[0] == (byte) 0xFF &&
                fileBytes[1] == (byte) 0xD8) {
            return true; // It's a JPG (JPEG) image
        }

        return false; // It's not a JPG image
    }
}
