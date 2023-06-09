package com.este.promptmaker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

public class FileManipulator {

    private String path;
    private String filename;

    public FileManipulator() {
    }

    public FileManipulator(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    private String toCamelCase(String name) {
        name = Normalizer.normalize(CaseUtils.toCamelCase(name, false, ' ', '_'), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9]+", "");

        if (name.isEmpty()) {
            name = "untitled";
        }

        return name;
    }

    private void setFilename(String name) {
        String convertedName = toCamelCase(StringUtils.left(name, 123));

        int num = 1;
        filename = convertedName + "_" + num;
        File file = makeFile("json");
        if (file.getParentFile().mkdirs()) {
            System.out.println();
        }
        while (file.exists() && num < 1001) {
            filename = convertedName + "_" + (num++);
            file = makeFile("json");
        }
    }

    private File makeFile(String ext) {
        return new File(path + "/" + filename + "." + ext);
    }

    public void writeToFile(File file, String text) throws IOException {
        PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
        writer.println(text);
        writer.close();
    }

    public void saveTags(String tagsFile, String text) throws IOException {
        File file = new File("config/" + tagsFile);
        if (file.exists() || file.getParentFile().mkdirs()) {
            writeToFile(file, text);
        }
    }

    public void makeJson(String name, String text) throws IOException {
        setFilename(name);
        writeToFile(makeFile("json"), text);
    }

    public void copyImage(File file, String ext) throws IOException {
        FileUtils.copyFile(file, makeFile(ext));
    }

    public void copyResizedImage(BufferedImage bufferedImage) throws IOException {
        ImageIO.write(bufferedImage, "png", makeFile("png"));
    }
}
