package com.delmark.portfoilo.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLConnection;

@Slf4j
@UtilityClass
public class ImageUtils {
    public byte[] getDefaultAvatar() {
        try {
            File placeholderImage = new File("src/main/resources/static/images/img-placeholder.png");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(placeholderImage));
            return bis.readAllBytes();
        }
        catch (IOException e) {
            log.error("Could not load default avatar! {}", e.getMessage());
            return null;
        }
    }

    public boolean isValidImage(byte[] imageData) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            String fileMimeType = URLConnection.guessContentTypeFromStream(inputStream);

            if (imageData.length > 10 * 1024 * 1024) {
                return false;
            }

            return fileMimeType != null && fileMimeType.startsWith("image");
        }
        catch (Exception e) {
            return false;
        }
    }
}
