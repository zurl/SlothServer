package Zurl.Sloth.Http;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by zcy on 3/20/2016.
 */


interface SocketProcessor{
    void start(Selector sel,SocketChannel sc);
    //public abstract void stop();
}

interface ResponseWriter{
    ByteBuffer getBuffer();
}

public class SocketServer {
    public static void work(SocketProcessor socketProcessor) throws Exception{
        Selector sel = Selector.open();
        //Selector:: Multi-Req-In-One-Port
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); //Non-Blocking Mode
        ssc.register(sel, SelectionKey.OP_ACCEPT);
        ssc.socket().setReuseAddress(true); //Reuse Address
        ssc.socket().bind(new InetSocketAddress(1234));
        while(true){
            int select = sel.select();
            if (select == 0) {
                continue;
            }
            Iterator<SelectionKey> selectedKeys = sel.selectedKeys() .iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey selKey = selectedKeys.next();
                if(selKey.isAcceptable()){
                    //function I => accept
                    ServerSocketChannel acceptSsc = (ServerSocketChannel)selKey.channel();
                    SocketChannel sc = acceptSsc.accept();
                    if(sc != null){
                        sc.configureBlocking(false);
                        sc.register(sel,SelectionKey.OP_READ);
                    }
                }else if(selKey.isReadable()){
                    //function II => Read
                    SocketChannel sc = (SocketChannel) selKey.channel();
                    sc.configureBlocking(false);
                    socketProcessor.start(sel,sc);
                }else if(selKey.isWritable()){
                    //function III => WriteBack
                    SocketChannel channel = (SocketChannel) selKey.channel();
                    ResponseWriter responseWriter = (ResponseWriter) selKey.attachment();
                    channel.write(responseWriter.getBuffer());
                    channel.shutdownOutput();
                    channel.close();
                }
                selectedKeys.remove();
            }
        }
    }

    public static void main(String args[]) {
        try{
            HttpServerConfig.initialize("./src/config.json");
            work(new HttpProcessor());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("fuck");
        }
    }
}
