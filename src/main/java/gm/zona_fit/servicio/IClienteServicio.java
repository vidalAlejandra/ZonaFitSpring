package gm.zona_fit.servicio;

import gm.zona_fit.modelo.Cliente;

import java.util.List;

public interface IClienteServicio {
    public List<Cliente> listarClientes();

    public Cliente buscarClientePorId(Integer idCliente);

    // este metodo se va encargar de insertar o modificar el dato, ya que, segun el parmetro de entrada
    // se diferencia, si el id es null entonces es inserta registro null, d lo contrario es una
    // modificacion de id existente
    public void guardarCliente(Cliente cliente);

    public void eliminarCliente(Cliente cliente);

}
