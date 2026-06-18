package ec.edu.epn.sara;

import ec.edu.epn.sara.view.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Hilo seguro para ejecutar interfaces gráficas en Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame login = new LoginFrame();
                login.setVisible(true); // Hace que la ventana aparezca en pantalla
            }
        });
    }
}