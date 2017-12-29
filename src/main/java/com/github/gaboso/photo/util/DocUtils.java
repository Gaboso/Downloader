package com.github.gaboso.photo.util;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Gaboso
 * @since 06/07/2017
 * <p>
 * DocUtils
 * </p>
 */
public class DocUtils {

    private static final Logger LOGGER = Logger.getLogger(DocUtils.class);

    private DocUtils() {
    }

    /**
     * Metodo para pegar extensao do arquivo
     *
     * @param fileName - Nome do arquivo
     * @return extensao do arquivo
     */
    public static String getFileExtension(String fileName) {
        String[] parts = fileName.split("\\.");
        int last = parts.length - 1;
        return parts[last];
    }

    public static void write(BufferedImage img, String filePath, String name, String extension) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                ImageIO.write(img, extension, file);
                LOGGER.info("Arquivo: " + name + " gravado!");
            } else {
                LOGGER.info("Arquivo: " + name + " já existente!");
            }
        } catch (IOException e) {
            LOGGER.error("Erro durante gravação do arquivo: " + name, e);
        }
    }

}