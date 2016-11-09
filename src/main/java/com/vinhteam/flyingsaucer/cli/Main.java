/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinhteam.flyingsaucer.cli;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;

import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextFontResolver;

/**
 *
 * @author tbvinh
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, DocumentException, IOException, ParseException {

        //Get Option
        Options options = new Options();
        options.addOption("url", true, "URL");
        options.addOption("html", true, "HTML");
        options.addOption("file", true, "HTML file");
        options.addOption("pdf", true, "PDF path");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        String url = cmd.getOptionValue("url");
        String html = cmd.getOptionValue("html");
        String file = cmd.getOptionValue("file");
        String pdfFile = cmd.getOptionValue("pdf");

        if(url != null && !url.equals("")){
            urlToPdf(url, pdfFile);
            System.out.println("OK");
        }else if(html != null && !html.equals("")){
            htmlToPdf(html, pdfFile);
            System.out.println("OK");
        }else if(file != null && !file.equals("")){
            htmlFileToPdf(file, pdfFile);
            System.out.println("OK");
        }else{
            System.out.println("Params not found. :( Ex: flyingsoucer-cli.jar -jar --url http://example.com/index.html --pdf example.pdf");
        }
    }

    public static void urlToPdf(String url, String pdfFile) {
        try {
            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.90 Safari/537.36");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            htmlToPdf(response.toString(), pdfFile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void htmlFileToPdf(String htmlFile, String pdfFile) {
        try {
            String content = "";
            content = IOUtils.toString(new FileReader(htmlFile));

            htmlToPdf(content, pdfFile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void htmlToPdf(String html, String pdfFile) {
        try {

            OutputStream pdfOut = new FileOutputStream(pdfFile);

            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver resolver = renderer.getFontResolver();

            Main main = new Main();
            File fontFile = main.getFileFont();
            try {
                resolver.addFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            renderer.setDocumentFromString(html);
            renderer.layout();

            renderer.createPDF(pdfOut);

            pdfOut.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getFileFont() throws IOException {

        File tempFile = null;
        tempFile = File.createTempFile("ARIALUNI", ".TTF");
        tempFile.deleteOnExit();

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(classLoader.getResourceAsStream("fonts/ARIALUNI.TTF"), out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile;
    }

}
