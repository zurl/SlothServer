package Zurl.Sloth.Http;

import java.io.*;
import java.nio.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by zcy on 3/24/2016.
 */

class HttpResponseWriter
    extends Writer implements ResponseWriter{

    ByteBuffer buffer = ByteBuffer.allocate(4096);
    private SocketChannel channel;
    private Selector selector;

    HttpResponseWriter(SocketChannel sc,Selector sel){
        channel = sc;
        selector = sel;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        String content = new String(cbuf,off,len);
        byte[] bytes = content.getBytes();
        buffer.put(bytes);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    public ByteBuffer getBuffer(){
        return buffer;
    }
}

class HttpResponsePrintWriter
    extends PrintWriter{


    public HttpResponsePrintWriter(Writer a){
        super(a);
    }
    @Override
    public void print(String s) {
        super.print(s);
    }
}

public class HttpResponse {
    Selector selector;
    SocketChannel socketChannel;
    HttpResponseWriter httpResponseWriter;
    HashMap<String,String> HttpHeader = new HashMap<>();

    public HttpResponse(SocketChannel ch,Selector sel){
        this.selector = sel;
        this.socketChannel = ch;
        this.httpResponseWriter = new HttpResponseWriter(ch,sel);
        HttpHeader.put("Server","SlothServer Test Edition");
    }
    public PrintWriter getWriter(){
        return new HttpResponsePrintWriter(this.httpResponseWriter);
    }
    public void ready(){
        try {
            this.httpResponseWriter.getBuffer().flip();
            this.socketChannel.register(selector, SelectionKey.OP_WRITE, this.httpResponseWriter);
        }catch(Exception e){
            System.out.print("hi");
        }
    }
    public void runScript(String fileName,String args){
           // new Thread(()->{
                try{
                    fileName = fileName.substring(1);
                    args = args.replaceAll("\"","\\\"");
                    args = "\"{HttpRequest=" + args +";}\"";

                    String ret = "";
                    final Process proc = Runtime.getRuntime().exec(HttpServerConfig.getConfig("WeakScriptExecutePath")+" -n "+fileName+" "+args);
                    BufferedReader stdout = new BufferedReader(new InputStreamReader
                            (proc.getInputStream()));

                    for (String line; null != (line = stdout.readLine()); )
                        ret += line;

                    HttpHeader.put("Content-type","text/html");
                    System.out.print(ret);
                    String[] tmp = ret.split("%%%%%%");
                    if(tmp.length > 1){
                        String[] argv = tmp[0].split(" ");
                        for(String x : argv){
                            switch(x){
                                case "SENDHTML":
                                    HttpHeader.replace("Content-type","text/html");
                                    break;
                                case "SENDJSON":
                                    HttpHeader.replace("Content-type","application/json");
                                    break;
                            }
                        }
                    }
                    ret = tmp[1];
                    HttpHeader.put("Content-length",Integer.toString(ret.length()));
                    this.printHeader(200);
                    this.getWriter().print(ret);
                    this.ready();
                    ///li.executeMessage(Wang.this, question);
                }catch (Exception e){
                    e.printStackTrace();
        System.out.print("qweq");
                }
           // }).start();
    }
    private void printHeader(int code){
        this.getWriter().print("HTTP/1.1 "+Integer.toString(code)+" OK\r\n");
        for (Map.Entry<String, String> entry : HttpHeader.entrySet()) {
            this.getWriter().print(entry.getKey() +":" + entry.getValue()+"\r\n");
        }
        this.getWriter().print("\r\n");
    }
    public void sendErrorMessage(int errcode){
        switch (errcode){
            case 404:
                break;
            default:
                break;
        }
    }
}
