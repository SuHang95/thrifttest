package server;

public class HelloServiceImpl implements HelloService.Iface{
    public java.lang.String sayHello(java.lang.String username) throws org.apache.thrift.TException{
        String hello=new String("Hello World!");
        return "Hello,"+username+"!";
    }
}
