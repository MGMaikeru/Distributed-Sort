import com.zeroc.Ice.*;

public class MotherServer {
    
    public static void main(String[] args) {
        int status = 0;
        try (Communicator communicator = Util.initialize(args,"MotherServer.cfg")) {
            ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Server");         
            adapter.add(new SenderI(), Util.stringToIdentity("callbackSender"));
            adapter.activate();
            communicator.waitForShutdown();
        } 
        System.exit(status);
    }
}
