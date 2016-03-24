package Zurl.Sloth.Http;

import com.sun.javafx.collections.MappingChange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zcy on 3/24/2016.
 */
public class HttpRequest {
    private SocketChannel channel;
    private String method;
    private String path;
    private String httpVersion;
    private HashMap<String,String> header;
    private String context;

    public HttpRequest(SocketChannel ch){
        this.channel = ch;
        header = new HashMap<>();
        this.initialize();
    }
    private void initialize(){
        try {
            String receive = receive(this.channel);
            System.out.print(receive);
            //process
            String[] tmp = receive.split("\r\n\r\n");
            if(tmp.length == 1)context = "";
            else context = tmp[1];
            String BigHead = tmp[0];
            tmp = BigHead.split("\r\n");
            String ins = tmp[0];
            for(int i=1 ; i<=tmp.length -1; i++){
                String[] arg = tmp[i].split(" ");
                header.put(arg[0],arg[1]);
            }
            tmp = ins.split(" ");
            method = tmp[0];
            path = tmp[1];
            httpVersion = tmp[2];

        }catch(Exception e){
            e.printStackTrace();
            System.out.print("fuck i ");
        }

//        BufferedReader br = new BufferedReader(new StringReader(receive));
//        this.head = new Head(br);
//        this.head.parseHead();
//        br.close();
    }
    public void debug(){
        System.out.print("hello");
    }
    private String receive(SocketChannel socketChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] bytes = null;
        int size = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((size = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            bytes = new byte[size];
            buffer.get(bytes);
            baos.write(bytes);
            buffer.clear();
        }
        bytes = baos.toByteArray();
        return new String(bytes);
    }

    public static void main(String[] args){

    }
}
