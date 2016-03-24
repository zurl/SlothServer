package Zurl.Sloth.Http;

import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by zcy on 3/24/2016.
 */

class HttpResponseWriter
    extends Writer implements ResponseWriter{

    ByteBuffer buffer = ByteBuffer.allocate(1024);
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
        buffer.flip();
        this.channel.register(selector, SelectionKey.OP_WRITE, this);
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

    public HttpResponse(SocketChannel ch,Selector sel){
        this.selector = sel;
        this.socketChannel = ch;
        this.httpResponseWriter = new HttpResponseWriter(ch,sel);
    }
    public PrintWriter getWriter(){
        return new HttpResponsePrintWriter(this.httpResponseWriter);
    }
    public void ready(){
        try {
            this.socketChannel.register(selector, SelectionKey.OP_WRITE, this.httpResponseWriter);
        }catch(Exception e){
            System.out.print("hi");
        }
    }
}
