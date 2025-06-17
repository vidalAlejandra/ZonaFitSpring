package gm.zona_fit;

import com.formdev.flatlaf.FlatDarculaLaf;
import gm.zona_fit.gui.ZonaFitForma;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

// vamos a comentar, para que no se levante la aplicacion de escritorio(swing)
//@SpringBootApplication
public class ZonaFitSwing {
    public static void main(String[] args) {
        //configuramos el modo oscuro de la pantalla
        FlatDarculaLaf.setup();
        //Instanciar la fabrica de spring
        // aqui, agregamos objetos para incializar la clase, primera linea. luego se indica en la segunda linea que no es una aplicacion wen, si no de escritorio
        ConfigurableApplicationContext contextoSpring =
                new SpringApplicationBuilder(ZonaFitSwing.class)
                        .headless(false)
                        .web(WebApplicationType.NONE)
                        .run(args);
        //Crear un objeto de swing
        SwingUtilities.invokeLater(()-> {
            ZonaFitForma zonaFitForma = contextoSpring.getBean(ZonaFitForma.class);
            zonaFitForma.setVisible(true);
        });
    }
}
