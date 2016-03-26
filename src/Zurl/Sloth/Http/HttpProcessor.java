package Zurl.Sloth.Http;

import Dependency.JSONObject;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by zcy on 3/24/2016.
 */
public class HttpProcessor implements SocketProcessor{

    @Override
    public void start(Selector sel, SocketChannel sc){
        HttpRequest req = new HttpRequest(sc);
        HttpResponse res = new HttpResponse(sc,sel);
        JSONObject jb = new JSONObject("{'fuck':8}");
        if(req.getMethod() == HttpRequest.Method.GET){
            String routePath = req.getPath();
            if(req.getRequestType() == HttpRequest.RequestType.STATIC){
                //res.sendPlainFile(routePath);
            }else if(req.getRequestType() == HttpRequest.RequestType.WEAKSCRIPT){
                res.runScript(routePath,req.getRequestJSON());
            }else{
                res.sendErrorMessage(0);
            }
        }else if(req.getMethod() == HttpRequest.Method.POST){
            res.sendErrorMessage(0);
        }else{
            res.sendErrorMessage(0);
        }

    }
}
