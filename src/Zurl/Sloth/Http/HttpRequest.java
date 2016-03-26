package Zurl.Sloth.Http;

import Dependency.JSONArray;
import Dependency.JSONObject;
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
    enum Method{
        GET,POST
    }

    enum RequestType{
        UNKNOWN,STATIC,WEAKSCRIPT
    }

    //final String[] StaticFileExtendName = {"html","htm","jpg","js","png","gif","ttf"};

    private SocketChannel channel;

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getContext() {
        return context;
    }

    public Method getMethod() {
        return method;
    }

    private Method method;
    private String path;
    private String httpVersion;
    private HashMap<String,String> header= new HashMap<>();
    private HashMap<String,String> QueryString= new HashMap<>();
    private String context;

    public RequestType getRequestType() {
        return requestType;
    }

    private RequestType requestType;


    public boolean isError() {
        return isError;
    }

    private boolean isError = false;

    public HttpRequest(SocketChannel ch){
        this.channel = ch;
        this.initialize();
    }
    private void initialize(){
        try {
            String receive = receive(this.channel);
            if(receive.equalsIgnoreCase(""))return;
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
            method = tmp[0].equals("GET")?Method.GET:Method.POST;
            path = tmp[1];
            httpVersion = tmp[2];
            //process query str
            tmp = path.split("\\?");
            if(tmp.length > 1){
                path = tmp[0];
                String[] args = tmp[1].split("&");
                for(String arg : args){
                    String[] t = arg.split("=");
                    if(t.length >= 2){
                        QueryString.put(t[0],t[1]);
                    }
                }
            }else{

            }
            tmp = path.split("\\.");
            String tmpType = "ws";
            if(tmp.length > 1)tmpType = tmp[tmp.length - 1];
            //isStaticType
            boolean isStatic = false;
            for(Object x : (JSONArray)HttpServerConfig.getConfig("StaticFileType")) {
                if(((String)x).equalsIgnoreCase(tmpType)){
                    isStatic = true;
                    break;
                }
            }
            if(isStatic){
                requestType = RequestType.STATIC;
            }else if(tmpType.equalsIgnoreCase("ws")){
                requestType = RequestType.WEAKSCRIPT;
            }else{
                requestType = RequestType.UNKNOWN;
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.print("fuck i ");
            isError = true;
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
    public String getRequestJSON(){
        JSONObject HttpRequest = new JSONObject();
        JSONArray GET = new JSONArray().put(QueryString);
        HttpRequest.put("GET",GET);

        return HttpRequest.toString();
    }
    public static void main(String[] args){

    }
}
