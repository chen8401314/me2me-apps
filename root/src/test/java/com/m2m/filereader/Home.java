package com.m2m.filereader;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;


public class Home {
    public static String HOME = "home";
    private static String REGEX_FILE_SEPARATOR = "/";

    public static File findInHome(String relativePath) throws Exception {
        File directory = home();
        System.out.println(directory.getAbsoluteFile());
        File rtn = findInDirectory(directory, relativePath);
        return rtn;
    }

    public static String source(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static File home4MavenTest() throws Exception {
        File pwd = pwd();
        String path = pwd.getAbsolutePath();
        String regex = "test-classes";
        String replacement = "classes";
        path = path.replaceAll(regex, replacement);
        File rtn = new File(path);
        return rtn;
    }

    private static File findInDirectory(File directory, String relativePath) {
        File rtn = directory;
        String[] names = relativePath.split(REGEX_FILE_SEPARATOR);
        for (String name : names) {
            rtn = exist(rtn, name);
            if (null == rtn) {
                return null;
            }
        }
        return rtn;
    }

    private static File exist(File directory, String name) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().equals(name)) {
                return file;
            }
        }
        return null;
    }

    private static File home() throws Exception {
        String overridden = System.getProperty(HOME);
        if (overridden != null) {
            File file = new File(overridden);
            return file;
        }
        File pwd = pwd();
        return pwd;
    }

    private static URL getURL(String fileName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL rtn = cl.getResource(fileName);
        return rtn;
    }

    private static File pwd() throws Exception {
        URL url = getURL("");
        File rtn = null;
        try {
            rtn = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new Exception(e);
        }
        return rtn;
    }
}
