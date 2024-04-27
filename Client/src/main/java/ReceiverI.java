import com.zeroc.Ice.Current;

public class ReceiverI implements Caller.Receiver{

    @Override
    public void receiveMessage(String msg, Current current) {
        // Imprimir el mensaje recibido en la consola
        System.out.println("Message received: " + msg);
    }

    @Override
    public void startSubServer(int from, int to, String filename, String basepath, Current current) {

    }

    @Override
    public String getListSorted(Current current) {
        return null;
    }

}