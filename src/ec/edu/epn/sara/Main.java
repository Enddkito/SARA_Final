package ec.edu.epn.sara;

import ec.edu.epn.sara.view.WelcomeFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Hilo seguro para ejecutar la interfaz gráfica
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WelcomeFrame bienvenida = new WelcomeFrame();
                bienvenida.setVisible(true); // Arranca con el selector de roles "Soy..."
            }
        });
    }
}