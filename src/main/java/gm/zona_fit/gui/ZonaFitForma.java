package gm.zona_fit.gui;

import gm.zona_fit.modelo.Cliente;
import gm.zona_fit.servicio.ClienteServicio;
import gm.zona_fit.servicio.IClienteServicio;
//import org.hibernate.query.sqm.spi.DelegatingSqmSelectionQueryImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// se agrega "extends JFRAME" -> se extiende en Jframe para hacer un objeto de esta clase y que se muestre la ventana
// se agrega component para ser uso de spring
// ahora desactive este @Component, solo para probar este programa como web, y no como aplicacio d escritorio(swing)
//@Component
public class ZonaFitForma extends JFrame{
    private JPanel panelPrincipal;
    private JTable clientesTabla;
    private JTextField nombreTexto;
    private JTextField apellidoTexto;
    private JTextField membresiaTexto;
    private JButton guardarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    IClienteServicio clienteServicio;
    private DefaultTableModel tablaModeloCliente;
    private Integer idCliente;

    // aqui se hace depedencia de spring en el constructor, y aqui en el cosntructor se inicializa el atributo de cliente servicio,
   // y asi podemos recuperar la info de la base de datos
    @Autowired
    public ZonaFitForma(ClienteServicio clienteServicio){
        this.clienteServicio = clienteServicio;
        iniciarForma(); // metodo

        guardarButton.addActionListener(e -> guardarCliente());
        clientesTabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cargaClienteSeleccionado();
            }
        });
        eliminarButton.addActionListener(e -> eliminarCliente());
        limpiarButton.addActionListener(e -> limpiarFormulario());
    }

    //declarando metodo, de nuestra aplicacion swing
    private void iniciarForma(){
        setContentPane(panelPrincipal);// llamando con metodo setContentPane a nuestro panel principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// llamndo al metodo setDefaultCloseOperation, utilizando la clase Jframe para que se cierre una vez ejecutada muestra aplicacion
        setSize(900,700);// metodo setSize para darle un tamaño aplicacion 900 ancho y 700 alto
        setLocationRelativeTo(null); // metodo, centra la ventana de aplicacion
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //this.tablaModeloCliente = new DefaultTableModel(0,4); // aqui indicamos la creacion d tabla
        // pero aqui, la estamos creando y estamos evitado, que se pueda editar la info directo de la tabla del formulario
        this.tablaModeloCliente = new DefaultTableModel(0, 4) {
            @Override
            public boolean isCellEditable(int row, int columnn) {
                return false;
            }
        };
        String[] cabeceros = {"Id", "Nombre", "Apellido", "Membresia"};
        this.tablaModeloCliente.setColumnIdentifiers(cabeceros);
        this.clientesTabla = new JTable(tablaModeloCliente);// creando manualmente el objeto de la tabla con las columnas que se requiera
        // Restringimos la selecion de 1 a mas registro de tabla del formulario
        this.clientesTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Aqui cargamos el listado de clientes(, la info que se va mostrar en la tabla
       listarClientes(); // aqui, lo llamanos y mas abajo se declara este metodo
    }

    private void listarClientes(){
        this.tablaModeloCliente.setRowCount(0);// conteo de registro comienza en 0, por eso se paso el cero
        var clientes = this.clienteServicio.listarClientes();
        // por cada clientes,  llamamos con el uso de forich y con expresion lambda, para hacer cada reglon como un arreglo para mostrar la info d las columnas
        clientes.forEach(cliente -> {
            Object[] reglonCliente = {
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getMembresia()
            }; // por cada objeto que tenemos , se va ir agregando el renglon/arreglo con la info.
            this.tablaModeloCliente.addRow(reglonCliente);
        });
    }

    private void guardarCliente(){
        if(nombreTexto.getText().equals("")){
            mostrarMensaje("Proporciona un nombre");
            nombreTexto.requestFocusInWindow();
            return;
        }
        if(membresiaTexto.getText().equals("")){
            mostrarMensaje("Proporciona una membresia");
            membresiaTexto.requestFocusInWindow();
            return;
        }
        //Recuperamos los valores del formulario
        var nombre = nombreTexto.getText();
        var apellido = apellidoTexto.getText();
        var membresia = Integer.parseInt(membresiaTexto.getText());
        var cliente = new Cliente(this.idCliente, nombre, apellido, membresia);
        // se comento esta lineas, ya que, se puede llamar directo desde el constructor d la clase
//        cliente.setId(this.idCliente);
//        cliente.setNombre(nombre);
//        cliente.setApellido(apellido);
//        cliente.setMembresia(membresia);
        this.clienteServicio.guardarCliente(cliente); //aqui se insertar la info a la base de datos o modificar
        if(this.idCliente == null)
            mostrarMensaje("Se agrego el nuevo Cliente.");
        else
            mostrarMensaje("Se actualizo el Cliente");
        limpiarFormulario();
        listarClientes();
    }

    private void cargaClienteSeleccionado(){
        var renglon = clientesTabla.getSelectedRow();
        if(renglon != -1){// -1 significa que no se ha selecciado ningun registro
            var id = clientesTabla.getModel().getValueAt(renglon, 0).toString();
            this.idCliente = Integer.parseInt(id);//aqui esta la diferencia, si se inserta o actualiza. ya que, por jpa y hibernate hace que, si es null el dato es nuevo, d lo contrario ya existe
            var nombre = clientesTabla.getModel().getValueAt(renglon, 1).toString();
            this.nombreTexto.setText(nombre);
            var apellido = clientesTabla.getModel().getValueAt(renglon, 2).toString();
            this.apellidoTexto.setText(apellido);
            var membresia = clientesTabla.getModel().getValueAt(renglon, 3).toString();
            this.membresiaTexto.setText(membresia);
        }
    }

    private void eliminarCliente(){
        var renglon = clientesTabla.getSelectedRow();// aqui rescatamos cuando uno seleciona un dato
        if(renglon != -1){
            var idClienteStr = clientesTabla.getModel().getValueAt(renglon, 0).toString();
            this.idCliente = Integer.parseInt(idClienteStr);
            var cliente = new Cliente();
            cliente.setId(this.idCliente);
            clienteServicio.eliminarCliente(cliente);
            mostrarMensaje("Cliente con id " + this.idCliente + " eliminado");
            limpiarFormulario();
            listarClientes();
        }
        else
            mostrarMensaje("Debe seleciconar un Cliente a eliminar");
    }

    //Metodos mas genericos
    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(this,mensaje);
    }
    private void limpiarFormulario(){
        nombreTexto.setText("");
        apellidoTexto.setText("");
        membresiaTexto.setText("");
        //Limpiamos el id del cliente que se selecciono
        this.idCliente = null;
        //Deseleccionamos el registros de tabla que esta selecionado
        this.clientesTabla.getSelectionModel().clearSelection();
    }

}
