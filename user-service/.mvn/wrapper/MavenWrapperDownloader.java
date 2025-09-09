/*
 * Copyright 2007-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.util.Properties;

public class MavenWrapperDownloader {

    private static final String WRAPPER_VERSION = "0.5.6";
    private static final String DEFAULT_DOWNLOAD_URL = "https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/"
        + WRAPPER_VERSION + "/maven-wrapper-" + WRAPPER_VERSION + ".jar";

    public static void main(String args[]) {
        System.out.println("- Downloading Maven Wrapper " + WRAPPER_VERSION);

        File baseDirectory = new File(args[0]);
        String mvnwPath = args[1];
        String url = args.length > 2 ? args[2] : DEFAULT_DOWNLOAD_URL;

        File mavenWrapperFile = new File(mvnwPath);
        if (mavenWrapperFile.exists()) {
            System.out.println("- Maven Wrapper JAR already exists, skipping download");
            System.exit(0);
        }

        System.out.println("- Downloading from: " + url);

        try {
            downloadFileFromURL(url, mavenWrapperFile);
            System.out.println("Downloaded " + mavenWrapperFile.getAbsolutePath());
            System.exit(0);
        } catch (Throwable e) {
            System.err.println("- Error downloading");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void downloadFileFromURL(String urlString, File destination) throws Exception {
        URL website = new URL(urlString);
        ReadableByteChannel rbc;
        rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destination);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

}
