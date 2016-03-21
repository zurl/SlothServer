package Sloth.Http;

import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zcy on 3/20/2016.
 */
public class Http {
    public static void work() throws Exception{
        Selector sel = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel .open();
        ssc.configureBlocking(false);
        ssc.register(sel, SelectionKey.OP_ACCEPT);
        ssc.socket().setReuseAddress(true);
        ssc.socket().bind(new InetSocketAddress(1234));
        while(true){
            while(sel.select() > 0){
                for(SelectionKey selKey : sel.selectedKeys()){
                    if(selKey.isAcceptable()){
                        ServerSocketChannel acceptSsc = (ServerSocketChannel)selKey.channel();
                        SocketChannel sc = acceptSsc.accept();
                        if(sc != null){
                            sc.configureBlocking(false);
                            sc.register(sel,SelectionKey.OP_READ);
                        }
                    }else if(selKey.isReadable()){
                        SocketChannel sc = (SocketChannel) selKey.channel();
                        sc.configureBlocking(false);
                        String ret = receive(sc);
                        BufferedReader br = new BufferedReader(new StringReader(ret));

                        String ans = br.readLine();
                        while( ans != null){
                            System.out.println(ans);
                            ans = br.readLine();
                        }
                        br.close();
                        sc.register(sel,SelectionKey.OP_WRITE);

                    }else if(selKey.isWritable()){
                        SocketChannel channel = (SocketChannel)selKey.channel();
                        String hello = "hello world...";
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        byte[] bytes = hello.getBytes();
                        buffer.put(bytes);
                        buffer.flip();
                        channel.write(buffer);
                        channel.shutdownInput();
                        channel.close();
                    }
                }
            }
        }
    }
    private static String receive(SocketChannel socketChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] bytes = null;
        int size = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((size = socketChannel.read(buffer))>0){
            buffer.flip();
            bytes = new byte[size];
            buffer.get(bytes);
            baos.write(bytes);
            buffer.clear();
        }
        bytes = baos.toByteArray();
        return new String(bytes);
    }
    public static void main(String args[]) {
        try{
            work();
        }catch (Exception e){
            System.out.println("fuck");
        }
    }
}
