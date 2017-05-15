package sample.printserv.messages;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

public class PrintableConfig {
    public String Host;
    public int HostPort;
    public String FileCert;
    public String PrinterPNG;
    public String PassCert;
    public String PassJKS;
    public String EncodingConsole;
    public String EncodingGet;
    public String EncodingPrint;
    public Map<String, String> Printers;

    public PrintableConfig(){
        String confJson = "config.json";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(confJson), Charset.forName("UTF-8")));

        }catch (Exception e){
            System.out.println(e);
            System.err.println(new Date().toString()+" - "+e);
            return;
        }

        confJson = "";
        try {
            String nulls = null;
            while ((nulls = reader.readLine()) != null) {
                confJson+=nulls;
            }
            nulls=null;
        } catch (IOException e) {
            System.err.println(new Date().toString()+" - "+e);
            e.printStackTrace();
        }

        PrintableConfig sPrint;
        try{
            sPrint = JSON.parseObject(confJson, PrintableConfig.class);
        }catch (Exception e){
            System.err.println(e);
            System.err.println(new Date().toString()+" - "+e);
            return;
        }

        this.Host = sPrint.Host;
        this.HostPort = sPrint.HostPort;
        this.FileCert = sPrint.FileCert;
        this.PrinterPNG = sPrint.PrinterPNG;
        this.PassCert = sPrint.PassCert;
        this.PassJKS = sPrint.PassJKS;
        this.EncodingConsole = sPrint.EncodingConsole;
        this.EncodingGet = sPrint.EncodingGet;
        this.EncodingPrint = sPrint.EncodingPrint;
        this.Printers = sPrint.Printers;
    }

}