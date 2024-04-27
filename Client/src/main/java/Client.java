import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import Caller.ReceiverPrx;
import Caller.SenderPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class Client {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "client.cfg")) {
            run(communicator);
        }
    }
    private static void run(Communicator communicator) {
        // Obtener el proxy del sender para enviar mensajes al servidor
        SenderPrx sender = SenderPrx.checkedCast(communicator.propertyToProxy("CallbackSender.Proxy"))
                .ice_twoway().ice_timeout(-1).ice_secure(false);
        if (sender == null) {
            System.err.println("Proxy invalid.");
            return;
        }
        // Configuración del Cliente y lógica de interacción con el usuario
        configureAndInteract(sender, communicator);
    }
    private static void configureAndInteract(SenderPrx sender, Communicator communicator) {
        // Configuración inicial del Cliente
        // Creación del adapter para el Cliente
        ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
        adapter.add(new ReceiverI(), Util.stringToIdentity("callbackReceiver"));
        adapter.activate();

        // Obtener el proxy del receptor para recibir mensajes del servidor
        ReceiverPrx receiver = ReceiverPrx
                .uncheckedCast(adapter.createProxy(Util.stringToIdentity("callbackReceiver")));

        // Obtener información del sistema para identificación
        String username = System.getProperty("user.name");
        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Interacción con el usuario
        interactWithUser(sender, receiver, username, hostname);
    }

    private static void interactWithUser(SenderPrx sender, ReceiverPrx receiver, String username, String hostname) {
        Scanner scanner = new Scanner(System.in);
        String input;
        while (true) {
            // Mostrar menú de opciones al usuario
            displayMenu();

            // Obtener la entrada del usuario
            input = scanner.nextLine();

            // Procesar la entrada del usuario
            if (input.equals("exit")) {
                sender.shutdown();
                break;
            } else {
                // Enviar un mensaje al servidor
                String message = username + "-" + hostname + "-" + input;
                asyncTask(message, sender, receiver);
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("========== MENU ==========");
        System.out.println("sort:<filename>");
        System.out.println("exit");
    }

    public static void asyncTask(String msg, SenderPrx sender, ReceiverPrx receiver) {
        // Enviar el mensaje de forma asíncrona
        CompletableFuture.runAsync(() -> {
            sender.sendMessage(receiver, msg);
        });
    }

}