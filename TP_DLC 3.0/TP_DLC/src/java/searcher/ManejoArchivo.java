package searcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import searcher.db.AccesoADatos;

/**
 *
 * @author Bozas-Niclis-Perez Grosso- Chino
 */
public class ManejoArchivo {

    private HashMap<String, Palabra> hs_palabra;
    private HashMap<String, Archivo> hs_archivo;
    private String path;
    private AccesoADatos db;
    private int id_archivo;
    private int id_palabra;

    /* Constructor de la clase. Recibe un archivo y una hastable, en caso de
    * existir un archivo guardado previamente, este constructor lo levanta y
    * coloca el objeto Hashtable dentro del atributo this.table.
     */
    public ManejoArchivo(String path) throws Exception {

        this.id_archivo = 1;
        this.id_palabra = 1;
        this.path = path;
        this.hs_palabra = new HashMap();
        this.db = new AccesoADatos();
        this.recuperarHashMap_archivo();
        this.indexar();

    }

    //Cuando quiere agregar archivos a la base de datos
    public ManejoArchivo(String path, boolean agregarArchivos) throws Exception {

        this.db = new AccesoADatos();
        this.id_archivo = this.recuperarN() + 1;
        this.id_palabra = this.recuperarIDPalabra();
        this.path = path;
        this.recuperarHashMap_palabra();
        this.recuperarHashMap_archivo();
        this.indexar();
    }

    //Cuando solo quiere hacer una busqueda con la indexacion ya existente
    public ManejoArchivo() throws Exception {
        this.db = new AccesoADatos();
        if (this.hs_palabra == null) {
            this.recuperarHashMap_palabra();
        } else {
            this.hs_palabra = new HashMap();
        }
    }

    private int recuperarIDPalabra() throws Exception {
        this.db.conectar();
        String query = "SELECT COUNT(*) FROM palabra;";
        try {
            ResultSet rs = this.db.executeQuery(query);
            if (rs.next()) {
                return 1 + rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERROR AL RECUPERAR CANTIDAD DE PALABRAS");
            System.exit(0);
        }
        this.db.desconectar();
        return -1;
    }

    private int recuperarN() throws Exception {
        this.db.conectar();
        String query = "SELECT COUNT(*) FROM documento;";
        try {
            ResultSet rs = this.db.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERROR AL RECUPERAR CANTIDAD DE ARCHIVOS");
            System.exit(0);
        }
        this.db.desconectar();
        return -1;
    }

    private void recuperarHashMap_palabra() throws Exception {

        this.hs_palabra = new HashMap();

        this.db.conectar();
        String query = "SELECT idpalabra, nombre FROM palabra;";

        try {
            ResultSet rs = this.db.executeQuery(query);
            while (rs.next()) {
                int idPalabra = rs.getInt(1);
                String nomPalabra = rs.getString(2);
                Palabra aux = new Palabra(nomPalabra, idPalabra);
                this.hs_palabra.put(nomPalabra, aux);

            }
        } catch (Exception e) {
            System.out.println("ERROR AL RECUPERAR ID PALABRA Y NOMBRE PALABRA");
            System.exit(0);
        }

        this.db.desconectar();
    }

    private void recuperarHashMap_archivo() throws Exception {
        this.hs_archivo = new HashMap();

        this.db.conectar();
        String query = "SELECT iddocumento, nombre, path FROM documento;";

        try {
            ResultSet rs = this.db.executeQuery(query);
            while (rs.next()) {
                int idArchivo = rs.getInt(1);
                String nomArchivo = rs.getString(2);
                String path = rs.getString(3);
                Archivo aux = new Archivo(nomArchivo, idArchivo, path);
                this.hs_archivo.put(nomArchivo, aux);
            }
        } catch (Exception e) {
            System.out.println("ERROR AL RECUPERAR ID ARCHIVO Y NOMBRE ARCHIVO");
            System.exit(0);
        }

        this.db.desconectar();
    }

    private void indexar() throws UnsupportedEncodingException, Exception {
        this.guardarEnHash();
    }

    public void insertDocumento(int id, String nombre) throws Exception {
        try {
            db.executeUpdate("INSERT INTO documento (idDocumento, nombre, path) VALUES (" + id + ", '" + nombre + "', '" + this.path + "');");
        } //try {db.executeUpdate("INSERT INTO documento (idDocumento, nombre) VALUES ("+ id_archivo + ", '" + file.getName() + "');");}
        catch (Exception e) {
            System.out.println("Error al insertar en documento");
            db.rollback();
            System.exit(0);
        }
    }

    public void insertPalabra(String p) throws Exception {
        try {
            db.executeUpdate("INSERT INTO palabra (idPalabra, nombre) VALUES (" + id_palabra + ", '" + p + "');");
        } catch (Exception e) {
            System.out.println("Error al insertar en palabra");
            Logger.getLogger(Palabra.class.getName()).log(Level.SEVERE, null, e);
            db.rollback();
            System.exit(0);
        }
    }

    public void insertPalabraPorDocumento(int idWord, int idDoc) {
        try {
            db.executeUpdate("INSERT INTO palabraPorDocumento (idPalabra, idDocumento, cantidad) VALUES (" + idWord + ", " + idDoc + ", " + 1 + ");");
        } catch (Exception ex) {
            System.out.println("Error al insertar en palabraPorDocumento en linea 116");
            try {
                db.rollback();
                System.out.println("\nrollback realizado");
                System.exit(0);
            } catch (Exception ex1) {
                System.out.println("Falló rollback en insert palabraPorDocumento");
                Logger.getLogger(ManejoArchivo.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public void updatePalabraPorDocumento(int cantApar, int idArch, int idWord) {
        try {
            db.executeUpdate("UPDATE palabraPorDocumento set cantidad = " + cantApar + " WHERE (idPalabra = " + idWord + " AND idDocumento = " + idArch + " );");
        } catch (Exception ex) {
            System.out.println("Error al actualizar en palabraPorDocumento");
            try {
                db.rollback();
                System.out.println("\nrollback realizado");
                System.exit(0);
            } catch (Exception ex1) {
                System.out.println("Falló rollback en insert palabraPorDocumento");
                Logger.getLogger(ManejoArchivo.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public void conectarBD() {
        try {
            db.conectar();
            System.out.println("Conexión exitosa\n");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexión");
            System.exit(0);
        }
    }

    public String cleanWord(String word) {
        word = Normalizer.normalize(word, Normalizer.Form.NFD);
        word = word.replaceAll("[\\p{Punct}&&[^()]]", "");
        word = word.replaceAll("[^\\p{ASCII}]", "");
        return word.replaceAll("[^ñÑáéíóúa-zA-z]", "").toLowerCase();
    }

    public void guardarEnHash() throws FileNotFoundException, UnsupportedEncodingException, Exception {

        File dir = new File(this.path);

        this.conectarBD();
        db.beginTransaction();

        File[] files = dir.listFiles();
        int hora, minutos, segundos;
        int nro_arch = 0;
        for (File file : files) {
            if (!this.hs_archivo.containsKey(file.getName())) {
                HashMap<String, Palabra> tempPalabraPorArchivo = new HashMap();
                Archivo arch = new Archivo(file.getName(), id_archivo, this.path);

                nro_arch++;
                System.out.println("Procesando archivo: " + file.getName());
                System.out.println("Archivo número: " + nro_arch);
                Calendar calendario = new GregorianCalendar();
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);
                System.out.println("La hora es: " + hora + ":" + minutos + ":" + segundos);
                System.out.println();

                this.insertDocumento(this.id_archivo, file.getName());

                try {

                    List<String> lines = Files.lines(Paths.get(file.getPath()), Charset.forName("ISO-8859-1")).collect(Collectors.toList());

                    for (String line : lines) {
                        String[] palabras = line.split(" ");

                        for (String dirtyWord : palabras) {
                            String word = this.cleanWord(dirtyWord);
                            if (!"".equals(word)) {
                                //si ya contiene palabra, no la almacena, solo incrementa 
                                //su aparicion.
                                if (!this.hs_palabra.containsKey(word)) {
                                    //Palabra rep = (Palabra) this.hs.get(word);
                                    //rep.addApar(); 
                                    Palabra nueva = new Palabra(word, id_palabra);

                                    this.insertPalabra(word);

                                    this.hs_palabra.put(word, nueva);
                                    tempPalabraPorArchivo.put(word, nueva);

                                    this.insertPalabraPorDocumento(this.id_palabra, this.id_archivo);

                                    id_palabra++;

                                } else {

                                    if (tempPalabraPorArchivo.containsKey(word)) {
                                        tempPalabraPorArchivo.get(word).addApar();
                                    } else {
                                        tempPalabraPorArchivo.put(word, this.hs_palabra.get(word));
                                        tempPalabraPorArchivo.get(word).addApar();

                                        this.insertPalabraPorDocumento(this.hs_palabra.get(word).getId(), this.id_archivo);

                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("");
                }
                tempPalabraPorArchivo.values().forEach((p) -> {

                    this.updatePalabraPorDocumento(p.getApar(), arch.getId(), p.getId());
                    p.setCantApar(0);

                });
                id_archivo++;
            }
        }

        System.out.println("\nA punto de realizar commit");
        db.commit();
        db.desconectar();
    }

    @Override
    public String toString() {
        return this.hs_palabra.toString();
    }
}
