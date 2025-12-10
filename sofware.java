import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación del esquema conceptual pedido.
 * - Las fechas usan LocalDateTime.
 * - Muchos a uno y uno a muchos se mantienen con listas + referencias simples.
 * - No hay lógica compleja (solo helpers para mantener las asociaciones).
 */

/* 1) Reclamo */
public class Reclamo {
    private String idReclamo;
    private LocalDateTime fecha;
    private String descripcion;
    private String estado;
    private String tipo;

    // Conexiones: Solo uno con Empresa y Hogar
    private Empresa empresa; // puede ser null
    private Hogar hogar;     // puede ser null

    public Reclamo(String idReclamo, LocalDateTime fecha, String descripcion, String estado, String tipo) {
        this.idReclamo = idReclamo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
        this.tipo = tipo;
    }

    // Getters / setters
    public String getIdReclamo() { return idReclamo; }
    public LocalDateTime getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getTipo() { return tipo; }

    public Empresa getEmpresa() { return empresa; }
    public Hogar getHogar() { return hogar; }

    // Asociaciones (helpers para mantener consistencia)
    public void setEmpresa(Empresa empresa) {
        // quitar de la empresa anterior si existe
        if (this.empresa != null) {
            this.empresa.removeReclamo(this);
        }
        this.empresa = empresa;
        if (empresa != null && !empresa.getReclamos().contains(this)) {
            empresa.addReclamo(this);
        }
    }

    public void setHogar(Hogar hogar) {
        if (this.hogar != null) {
            this.hogar.removeReclamo(this);
        }
        this.hogar = hogar;
        if (hogar != null && !hogar.getReclamos().contains(this)) {
            hogar.addReclamo(this);
        }
    }

    @Override
    public String toString() {
        return "Reclamo{" + idReclamo + ", fecha=" + fecha + ", estado=" + estado + "}";
    }
}

/* 2) Empresa */
class Empresa {
    private String idEmpresa;
    private String nombre;
    private String contacto;
    private String direccion;

    // Conexión: cero a muchos con Reclamo
    private List<Reclamo> reclamos = new ArrayList<>();

    public Empresa(String idEmpresa, String nombre, String contacto, String direccion) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.contacto = contacto;
        this.direccion = direccion;
    }

    // Getters / setters
    public String getIdEmpresa() { return idEmpresa; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public String getDireccion() { return direccion; }
    public List<Reclamo> getReclamos() { return Collections.unmodifiableList(reclamos); }

    // helpers
    public void addReclamo(Reclamo r) {
        if (r == null) return;
        if (!reclamos.contains(r)) {
            reclamos.add(r);
            if (r.getEmpresa() != this) r.setEmpresa(this);
        }
    }

    public void removeReclamo(Reclamo r) {
        if (reclamos.remove(r)) {
            if (r.getEmpresa() == this) r.setEmpresa(null);
        }
    }

    @Override
    public String toString() {
        return "Empresa{" + nombre + "}";
    }
}

/* 3) Hogar */
class Hogar {
    private String idHogar;
    private String nombre;
    private String contacto;
    private String direccion;

    // Conexiones: cero a muchos con Reclamo y Pedido
    private List<Reclamo> reclamos = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();

    public Hogar(String idHogar, String nombre, String contacto, String direccion) {
        this.idHogar = idHogar;
        this.nombre = nombre;
        this.contacto = contacto;
        this.direccion = direccion;
    }

    // Getters
    public String getIdHogar() { return idHogar; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public String getDireccion() { return direccion; }

    public List<Reclamo> getReclamos() { return Collections.unmodifiableList(reclamos); }
    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }

    // helpers para Reclamo
    public void addReclamo(Reclamo r) {
        if (r == null) return;
        if (!reclamos.contains(r)) {
            reclamos.add(r);
            if (r.getHogar() != this) r.setHogar(this);
        }
    }

    public void removeReclamo(Reclamo r) {
        if (reclamos.remove(r)) {
            if (r.getHogar() == this) r.setHogar(null);
        }
    }

    // helpers para Pedido
    public void addPedido(Pedido p) {
        if (p == null) return;
        if (!pedidos.contains(p)) {
            pedidos.add(p);
            if (p.getHogar() != this) p.setHogar(this);
        }
    }

    public void removePedido(Pedido p) {
        if (pedidos.remove(p)) {
            if (p.getHogar() == this) p.setHogar(null);
        }
    }

    @Override
    public String toString() {
        return "Hogar{" + nombre + "}";
    }
}

/* 4) Pedido */
class Pedido {
    private String idPedido;
    private LocalDateTime fecha;
    private String estado;
    private double monto;
    private double tEstimado;

    // Conexiones: Solo uno a Conductor, Hogar y Ubicación, y de cero a muchos con Ruta
    private Conductor conductor; // puede ser null
    private Hogar hogar;         // debe apuntar al Hogar que solicitó
    private Ubicacion ubicacion; // dirección de entrega; puede ser null
    private List<Ruta> rutas = new ArrayList<>(); // rutas asociadas (0..*)

    public Pedido(String idPedido, LocalDateTime fecha, String estado, double monto, double tEstimado) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.estado = estado;
        this.monto = monto;
        this.tEstimado = tEstimado;
    }

    // Getters / setters
    public String getIdPedido() { return idPedido; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public double getMonto() { return monto; }
    public double gettEstimado() { return tEstimado; }

    public Conductor getConductor() { return conductor; }
    public Hogar getHogar() { return hogar; }
    public Ubicacion getUbicacion() { return ubicacion; }
    public List<Ruta> getRutas() { return Collections.unmodifiableList(rutas); }

    // asociación Conductor (solo uno)
    public void setConductor(Conductor c) {
        if (this.conductor != null) {
            this.conductor.removePedido(this);
        }
        this.conductor = c;
        if (c != null && !c.getPedidos().contains(this)) {
            c.addPedido(this);
        }
    }

    // asociación Hogar (solo uno)
    public void setHogar(Hogar h) {
        if (this.hogar != null) {
            this.hogar.removePedido(this);
        }
        this.hogar = h;
        if (h != null && !h.getPedidos().contains(this)) {
            h.addPedido(this);
        }
    }

    // asociación Ubicacion (solo uno)
    public void setUbicacion(Ubicacion u) {
        if (this.ubicacion != null) {
            this.ubicacion.removePedido(this);
        }
        this.ubicacion = u;
        if (u != null && !u.getPedidos().contains(this)) {
            u.addPedido(this);
        }
    }

    // rutas (0..*)
    public void addRuta(Ruta r) {
        if (r == null) return;
        if (!rutas.contains(r)) {
            rutas.add(r);
            if (r.getPedidos() == null || !r.getPedidos().contains(this)) {
                r.addPedido(this);
            }
        }
    }

    public void removeRuta(Ruta r) {
        if (rutas.remove(r)) {
            r.removePedido(this);
        }
    }

    @Override
    public String toString() {
        return "Pedido{" + idPedido + ", monto=" + monto + "}";
    }
}

/* 5) Conductor */
class Conductor {
    private String idConductor;
    private String nombre;
    private Ubicacion ubicacionActual; // Solo uno
    private String estado;
    private String clasificacion;

    // Conexiones: De cero a muchos con Ruta y pedido, y Solo uno a Ubicación
    private List<Ruta> rutas = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();

    public Conductor(String idConductor, String nombre, String estado, String clasificacion) {
        this.idConductor = idConductor;
        this.nombre = nombre;
        this.estado = estado;
        this.clasificacion = clasificacion;
    }

    // Getters / setters
    public String getIdConductor() { return idConductor; }
    public String getNombre() { return nombre; }
    public Ubicacion getUbicacionActual() { return ubicacionActual; }
    public String getEstado() { return estado; }
    public String getClasificacion() { return clasificacion; }

    public List<Ruta> getRutas() { return Collections.unmodifiableList(rutas); }
    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }

    public void setUbicacionActual(Ubicacion u) {
        if (this.ubicacionActual != null) {
            this.ubicacionActual.removeConductor(this);
        }
        this.ubicacionActual = u;
        if (u != null && !u.getConductores().contains(this)) {
            u.addConductor(this);
        }
    }

    // rutas
    public void addRuta(Ruta r) {
        if (r == null) return;
        if (!rutas.contains(r)) {
            rutas.add(r);
            if (r.getConductor() != this) r.setConductor(this);
        }
    }

    public void removeRuta(Ruta r) {
        if (rutas.remove(r)) {
            if (r.getConductor() == this) r.setConductor(null);
        }
    }

    // pedidos
    public void addPedido(Pedido p) {
        if (p == null) return;
        if (!pedidos.contains(p)) {
            pedidos.add(p);
            if (p.getConductor() != this) p.setConductor(this);
        }
    }

    public void removePedido(Pedido p) {
        if (pedidos.remove(p)) {
            if (p.getConductor() == this) p.setConductor(null);
        }
    }

    @Override
    public String toString() {
        return "Conductor{" + nombre + "}";
    }
}

/* 6) Ruta */
class Ruta {
    private String idRuta;
    private double distancia;
    private double tEstimado;
    private String estado;
    private String tipo;

    // Conexiones: de uno a muchos con Conductor (interpreto: cada Ruta apunta a un Conductor; un Conductor puede tener muchas Rutas)
    private Conductor conductor; // solo uno

    // Cero a muchos con RutaUbicación
    private List<RutaUbicacion> rutaUbicaciones = new ArrayList<>();

    // Solo uno con Mapa
    private Mapa mapa;

    // Además: para Pedido: "de cero a muchos con Ruta" -> Permito que Ruta conozca los pedidos asociados
    private List<Pedido> pedidos = new ArrayList<>();

    public Ruta(String idRuta, double distancia, double tEstimado, String estado, String tipo) {
        this.idRuta = idRuta;
        this.distancia = distancia;
        this.tEstimado = tEstimado;
        this.estado = estado;
        this.tipo = tipo;
    }

    // Getters / setters
    public String getIdRuta() { return idRuta; }
    public double getDistancia() { return distancia; }
    public double gettEstimado() { return tEstimado; }
    public String getEstado() { return estado; }
    public String getTipo() { return tipo; }

    public Conductor getConductor() { return conductor; }
    public List<RutaUbicacion> getRutaUbicaciones() { return Collections.unmodifiableList(rutaUbicaciones); }
    public Mapa getMapa() { return mapa; }
    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }

    // Conductor (solo uno)
    public void setConductor(Conductor c) {
        if (this.conductor != null) {
            this.conductor.removeRuta(this);
        }
        this.conductor = c;
        if (c != null && !c.getRutas().contains(this)) {
            c.addRuta(this);
        }
    }

    // RutaUbicacion (0..*)
    public void addRutaUbicacion(RutaUbicacion ru) {
        if (ru == null) return;
        if (!rutaUbicaciones.contains(ru)) {
            rutaUbicaciones.add(ru);
            if (ru.getRuta() != this) ru.setRuta(this);
        }
    }

    public void removeRutaUbicacion(RutaUbicacion ru) {
        if (rutaUbicaciones.remove(ru)) {
            if (ru.getRuta() == this) ru.setRuta(null);
        }
    }

    // Mapa (solo uno)
    public void setMapa(Mapa m) {
        if (this.mapa != null) {
            this.mapa.removeRuta(this);
        }
        this.mapa = m;
        if (m != null && !m.getRutas().contains(this)) {
            m.addRuta(this);
        }
    }

    // Pedidos (0..*)
    public void addPedido(Pedido p) {
        if (p == null) return;
        if (!pedidos.contains(p)) {
            pedidos.add(p);
            if (!p.getRutas().contains(this)) p.addRuta(this);
        }
    }

    public void removePedido(Pedido p) {
        if (pedidos.remove(p)) {
            p.removeRuta(this);
        }
    }

    @Override
    public String toString() {
        return "Ruta{" + idRuta + ", distancia=" + distancia + "}";
    }
}

/* 7) Mapa */
class Mapa {
    private String idMapa;
    private String nombre;
    private String proveedor;
    private String tipo;

    // Conexiones: de cero a muchos con Ruta y solo uno con API
    private List<Ruta> rutas = new ArrayList<>();
    private API api; // solo uno

    public Mapa(String idMapa, String nombre, String proveedor, String tipo) {
        this.idMapa = idMapa;
        this.nombre = nombre;
        this.proveedor = proveedor;
        this.tipo = tipo;
    }

    // Getters
    public String getIdMapa() { return idMapa; }
    public String getNombre() { return nombre; }
    public String getProveedor() { return proveedor; }
    public String getTipo() { return tipo; }

    public List<Ruta> getRutas() { return Collections.unmodifiableList(rutas); }
    public API getApi() { return api; }

    public void addRuta(Ruta r) {
        if (r == null) return;
        if (!rutas.contains(r)) {
            rutas.add(r);
            if (r.getMapa() != this) r.setMapa(this);
        }
    }

    public void removeRuta(Ruta r) {
        if (rutas.remove(r)) {
            if (r.getMapa() == this) r.setMapa(null);
        }
    }

    public void setApi(API api) {
        if (this.api != null) {
            this.api.setMapa(null);
        }
        this.api = api;
        if (api != null && api.getMapa() != this) api.setMapa(this);
    }

    public void removeApi() {
        if (api != null) {
            API old = api;
            this.api = null;
            old.setMapa(null);
        }
    }

    @Override
    public String toString() {
        return "Mapa{" + nombre + "}";
    }
}

/* 8) API */
class API {
    private String idAPI;
    private String proveedor;
    private String version;
    private String estado;

    // Conexiones: Solo uno con Mapa
    private Mapa mapa; // puede ser null

    public API(String idAPI, String proveedor, String version, String estado) {
        this.idAPI = idAPI;
        this.proveedor = proveedor;
        this.version = version;
        this.estado = estado;
    }

    public String getIdAPI() { return idAPI; }
    public String getProveedor() { return proveedor; }
    public String getVersion() { return version; }
    public String getEstado() { return estado; }

    public Mapa getMapa() { return mapa; }

    public void setMapa(Mapa m) {
        if (this.mapa != null) {
            this.mapa.removeApi();
        }
        this.mapa = m;
        if (m != null && m.getApi() != this) m.setApi(this);
    }

    @Override
    public String toString() {
        return "API{" + proveedor + " v" + version + "}";
    }
}

/* 9) Ubicación */
class Ubicacion {
    private String idUbicacion;
    private double latitud;
    private double longitud;
    private String nombre;
    private boolean valido;
    private String tipo;

    // Conexiones: De cero a muchos con Conductor, Pedido y RutaUbicación
    private List<Conductor> conductores = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    private List<RutaUbicacion> rutaUbicaciones = new ArrayList<>();

    public Ubicacion(String idUbicacion, double latitud, double longitud, String nombre, boolean valido, String tipo) {
        this.idUbicacion = idUbicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
        this.valido = valido;
        this.tipo = tipo;
    }

    // Getters
    public String getIdUbicacion() { return idUbicacion; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getNombre() { return nombre; }
    public boolean isValido() { return valido; }
    public String getTipo() { return tipo; }

    public List<Conductor> getConductores() { return Collections.unmodifiableList(conductores); }
    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }
    public List<RutaUbicacion> getRutaUbicaciones() { return Collections.unmodifiableList(rutaUbicaciones); }

    // helpers
    public void addConductor(Conductor c) {
        if (c == null) return;
        if (!conductores.contains(c)) {
            conductores.add(c);
            if (c.getUbicacionActual() != this) c.setUbicacionActual(this);
        }
    }

    public void removeConductor(Conductor c) {
        if (conductores.remove(c)) {
            if (c.getUbicacionActual() == this) c.setUbicacionActual(null);
        }
    }

    public void addPedido(Pedido p) {
        if (p == null) return;
        if (!pedidos.contains(p)) {
            pedidos.add(p);
            if (p.getUbicacion() != this) p.setUbicacion(this);
        }
    }

    public void removePedido(Pedido p) {
        if (pedidos.remove(p)) {
            if (p.getUbicacion() == this) p.setUbicacion(null);
        }
    }

    public void addRutaUbicacion(RutaUbicacion ru) {
        if (ru == null) return;
        if (!rutaUbicaciones.contains(ru)) {
            rutaUbicaciones.add(ru);
            if (ru.getUbicacion() != this) ru.setUbicacion(this);
        }
    }

    public void removeRutaUbicacion(RutaUbicacion ru) {
        if (rutaUbicaciones.remove(ru)) {
            if (ru.getUbicacion() == this) ru.setUbicacion(null);
        }
    }

    @Override
    public String toString() {
        return "Ubicacion{" + nombre + " (" + latitud + "," + longitud + ")}";
    }
}

/* 10) RutaUbicación */
class RutaUbicacion {
    // Parámetros:{Orden, T_Estimado, Distancia}
    private int orden;
    private double tEstimado;
    private double distancia;

    // Conexiones: Solo uno con Ruta y Ubicación
    private Ruta ruta;         // solo uno
    private Ubicacion ubicacion; // solo uno

    public RutaUbicacion(int orden, double tEstimado, double distancia) {
        this.orden = orden;
        this.tEstimado = tEstimado;
        this.distancia = distancia;
    }

    // Getters / setters
    public int getOrden() { return orden; }
    public double gettEstimado() { return tEstimado; }
    public double getDistancia() { return distancia; }

    public Ruta getRuta() { return ruta; }
    public Ubicacion getUbicacion() { return ubicacion; }

    public void setRuta(Ruta r) {
        if (this.ruta != null) {
            this.ruta.removeRutaUbicacion(this);
        }
        this.ruta = r;
        if (r != null && !r.getRutaUbicaciones().contains(this)) {
            r.addRutaUbicacion(this);
        }
    }

    public void setUbicacion(Ubicacion u) {
        if (this.ubicacion != null) {
            this.ubicacion.removeRutaUbicacion(this);
        }
        this.ubicacion = u;
        if (u != null && !u.getRutaUbicaciones().contains(this)) {
            u.addRutaUbicacion(this);
        }
    }

    @Override
    public String toString() {
        return "RutaUbicacion{orden=" + orden + ", distancia=" + distancia + "}";
    }
}

/* Ejemplo rápido de uso (puedes mover a una clase Main separada) */
class EjemploUso {
    public static void main(String[] args) {
        // Crear entidades
        Hogar hogar = new Hogar("H1", "Casa Perez", "9-1234-5678", "Calle Falsa 123");
        Empresa empresa = new Empresa("E1", "Aguas S.A.", "2-3333-4444", "Av. Principal 50");
        Reclamo r = new Reclamo("R1", LocalDateTime.now(), "Fuga de agua", "Abierto", "Infraestructura");

        // Asociar reclamo con hogar y empresa
        r.setHogar(hogar);
        r.setEmpresa(empresa);

        System.out.println(hogar.getReclamos());
        System.out.println(empresa.getReclamos());

        // Crear ubicación, conductor, pedido y ruta
        Ubicacion u = new Ubicacion("U1", -36.82, -73.04, "Casa Perez", true, "Residencial");
        Conductor c = new Conductor("C1", "Juan", "Disponible", "4.9");
        c.setUbicacionActual(u);

        Pedido p = new Pedido("P1", LocalDateTime.now(), "En camino", 12000.0, 30.0);
        p.setHogar(hogar);
        p.setUbicacion(u);
        p.setConductor(c);

        Ruta ruta = new Ruta("RT1", 12.5, 25.0, "Activa", "Entrega");
        ruta.setConductor(c);
        ruta.setMapa(new Mapa("M1", "MapaBase", "ProveedorX", "Vector"));

        RutaUbicacion ru1 = new RutaUbicacion(1, 5.0, 2.0);
        ru1.setRuta(ruta);
        ru1.setUbicacion(u);

        ruta.addPedido(p);

        System.out.println("Pedido asociado a conductor: " + p.getConductor());
        System.out.println("Ruta del conductor: " + c.getRutas());
        System.out.println("Ubicaciones en la ruta: " + ruta.getRutaUbicaciones());
    }
}