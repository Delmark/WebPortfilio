package com.delmark.portfoilo.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

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
}
