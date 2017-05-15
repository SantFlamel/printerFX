package sample.printserv;

import com.alibaba.fastjson.JSON;
import sample.printserv.cut.PrintWithoutDialog;
import sample.printserv.messages.PrintableConfig;
import sample.printserv.messages.StructPrintable;

import javax.net.ssl.*;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerTLS implements Runnable{
    //java  -Dfile.encoding="UTF-8" -jar PrintWithJava.jar
    public static PrintService findPrintService(String printerName)
    {
        System.out.println("Search printer: " + printerName);
        try {
            for (PrintService service : PrinterJob.lookupPrintServices()) {
                if (service.getName().equalsIgnoreCase(printerName))
                    return service;
            }
        }catch (Exception e){
            System.err.println("Error find printer: "+e.toString());
        }
        return null;
    }

    public void run() {

        // Для получения текущего системного времени достаточно выполнить:
        long curTime = System.currentTimeMillis();

        // Хотите значение типа Date, с этим временем?
        Date curDate = new Date(curTime);

        // Хотите строку в формате, удобном Вам?
        String curStringDate = new SimpleDateFormat("dd.MM.yyyy").format(curTime);
        String curStringTime = new SimpleDateFormat("hh-mm-ss").format(curTime);

        File file;
        try {
            file = new File(curStringDate + "_" + curStringTime + ".log");
            System.setErr(new PrintStream(file));
        } catch(Exception e){
            System.out.println("Error"+e.toString());
        }

        System.err.println("------------------------------------");
        System.err.println(new Date().toString()+" - RUN SERVER");


        PrintableConfig prConf = new PrintableConfig();

        // CREATE SSLContext
        // create key store
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(prConf.FileCert),prConf.PassCert.toCharArray());

            // create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, prConf.PassJKS.toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            // create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            // init SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(km, tm, null);

            // create socket
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(7730);

            String exit = "";
            System.out.println("SSL server started");

            while (true){

                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

                InputStream inputStream = sslSocket.getInputStream();


                OutputStream outputStream = sslSocket.getOutputStream();

                DataInputStream bufferedReader = new DataInputStream(inputStream);

                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));


                byte[] buf = new byte[4];
                bufferedReader.read(buf);
                Integer i = Integer.valueOf(new String(buf));
                String string = "";
                try {
                    buf = new byte[i];
                    bufferedReader.readFully(buf);
                    System.out.println(new String(buf,prConf.EncodingGet));
                }catch (Exception e){
                    System.err.println(new Date().toString()+" - "+e);
                    string = String.valueOf(("00:"+e.toString()).getBytes().length);
                    while(string.length()<4){string="0" + string;}
                    printWriter.print(string+"00:"+e.toString());
                    continue;
                }
                string = new String(buf,"UTF-8");
                StructPrintable sPrint;
                try {
                    sPrint = JSON.parseObject(string, StructPrintable.class);
                }catch (Exception e){
                    System.err.println(new Date().toString()+"  - "+e);
                    string = String.valueOf(("00:"+e.toString()).getBytes().length);
                    while(string.length()<4){string="0"+string;}
                    printWriter.print(string+"00:"+e.toString());
                    continue;
                }

                PrintService service;
                try {
                    service = findPrintService(prConf.Printers.get(sPrint.OrgHash));
                }catch (Exception e){
                    System.err.println("findPrintService: "+e.toString());
                    service = null;
                }

                if (service==null){
                    System.out.println("Not found printer "+sPrint.OrgHash);
                    service = PrintServiceLookup.lookupDefaultPrintService();
                    if (service==null){
                        System.out.println("Not found default printer");
                    }{
                        System.out.println("Found default printer: "+service.getName());
                    }
                }else {
                    System.out.println("Found printer: " + service.getName());
                }

                if (service!=null){
                    //PrintService service = PrintServiceLookup.lookupDefaultPrintService();
                    DocPrintJob job = service.createPrintJob();

                    DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

                    Doc doc;
                    try{

                        string = "------------------------------------------\n" +
                                sPrint.Header + "\n" + sPrint.InfoOrg + "\n";
                        if (!sPrint.InfoCheck.isEmpty()) {
                            string += sPrint.InfoCheck + "\n";
                        }
                        string += "   ************************************\n";
                        for (String entry : sPrint.Body) {
                            if (entry!=null) {
                                string += entry+"\n";
                            }
                        }
                        string += "   ************************************\n";

                        string+=sPrint.Thanks+"\n";
                        string+="   ************************************\n";
                        string+=sPrint.Footer+"\n";



                        doc = new SimpleDoc(string.getBytes(prConf.EncodingPrint), flavor, null);

                    }catch (Exception e ){
                        System.err.println(new Date().toString()+" - "+e);
                        System.out.println("Exception  doc = new SimpleDoc: "+e);
                        string = String.valueOf(("00:"+e.toString()).getBytes().length);
                        while(string.length()<4){string="0"+string;}
                        printWriter.print(string+"00:"+e.toString());
                        continue;
                    }
                    PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
                    attrs.add(new Copies(1));

                    try {
                        job.print(doc, attrs);
                    } catch (PrintException e) {
                        System.err.println(new Date().toString()+" - "+e);
                        System.out.println("Exception job.print(doc, attrs);: " + e);
                        string = String.valueOf(("00:"+e.toString()).getBytes().length);
                        while(string.length()<4){string="0"+string;}
                        printWriter.print(string+"00:"+e.toString());
                        continue;
                    }
                }
                PrintWithoutDialog pr = new PrintWithoutDialog(service);
                buf = new byte[4096];


                string = String.valueOf("01:OK".getBytes().length);
                while(string.length()<4){string="0"+string;}
                printWriter.print(string+"01:OK");
                printWriter.flush();

            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

//windows-1251
//UTF-8
//CP1251
//KOI8_R
//Cp866 - working
                        /*
                                Cp1251:
                        Windows-1251
                        Cp866:
                        IBM866
                        IBM-866
                        866
                        CP866
                        CSIBM866
                                KOI8_R:
                        KOI8-R
                        KOI8
                        CSKOI8R
                                ISO8859_5:
                        ISO8859-5
                        ISO-8859-5
                        ISO_8859-5
                        ISO_8859-5:1988
                        ISO-IR-144
                        8859_5
                        Cyrillic
                        CSISOLatinCyrillic
                        IBM915
                        IBM-915
                        Cp915
                        915
                        */