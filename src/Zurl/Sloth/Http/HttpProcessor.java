package Zurl.Sloth.Http;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by zcy on 3/24/2016.
 */
public class HttpProcessor extends SocketProcessor{

    @Override
    public void start(Selector sel, SocketChannel sc){
        HttpRequest req = new HttpRequest(sc);
        HttpResponse res = new HttpResponse(sc,sel);
        res.getWriter().print("<html><body><h>hello world</h><button>hello!</button></body></html>");
        res.ready();
    }
}
