package client;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import lombok.extern.slf4j.Slf4j;
//import server.HelloService;


//服务端的协议和客户端的协议要一致
@Slf4j(topic="Client")
public class client {
    public static void main(String[] args) {

        TTransport tTransport = new TFastFramedTransport(new TSocket("localhost",8899),600);
        TProtocol tProtocol = new TCompactProtocol(tTransport);
        server.HelloService.Client client = new server.HelloService.Client(tProtocol);
        try{
            //log.debug("Before open the tTRansport!");
            tTransport.open();
            String hello=client.sayHello("suhang");
            //log.debug("Get a response:",hello);

            System.out.println(hello);
        }catch (Exception ex){
            throw new  RuntimeException(ex.getMessage(),ex);
        }finally {
            tTransport.close();
        }
    }
}


