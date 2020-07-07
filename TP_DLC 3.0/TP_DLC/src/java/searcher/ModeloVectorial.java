package searcher;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import searcher.db.AccesoADatos;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author julian - Chino
 */
public class ModeloVectorial {

    private HashMap<String, Palabra> hs;
    private String[] q;
    private Palabra[] wordsOfQ;
    private HashMap<String, Archivo> R;
    private Archivo[] result;
    private AccesoADatos db;
    private String consultaInicial;

    public ModeloVectorial(String q) throws IOException, FileNotFoundException, ClassNotFoundException, Exception {

        //Guardar la consulta inicial para mostrarla finalmente
        this.consultaInicial = q;

        //Inicializar la base de datos.
        this.db = new AccesoADatos();

        //Recuperar la HashMap de palabras.
        this.recuperarHashMap();

        //Crear la HashMap que contendra los archivos resultantes.
        this.R = new HashMap();

        //Separar la consulta en palabras.
        this.q = q.split(" ");

        //Ordenar la consulta y obtener un array wordsOfQ que contiene objetos tipo Palabra.
        this.orderQ();

        //Agregar los archivos a R.
        this.addFilesToHashMap();

        this.hashToArray();

        //PROBANDOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
        System.out.println("RESULTADO ANTES DE AGREGAR EL BONUS DE PESO...");
        System.out.println(this.toString());
        //this.addBonusWeight();

        //Ordenar los archivos de R de mayor a menor teniendo en cuenta su weight.
        this.orderResult();

    }

    private void recuperarHashMap() throws Exception {

        this.hs = new HashMap();

        this.db.conectar();
        String query = "SELECT idpalabra, nombre FROM palabra;";

        try {
            ResultSet rs = this.db.executeQuery(query);
            while (rs.next()) {
                int idPalabra = rs.getInt(1);
                String nomPalabra = rs.getString(2);
                Palabra aux = new Palabra(nomPalabra, idPalabra);
                this.hs.put(nomPalabra, aux);

            }
        } catch (Exception e) {
            System.out.println("ERROR AL RECUPERAR ID PALABRA Y NOMBRE PALABRA");
            System.exit(0);
        }

        this.db.desconectar();
    }

    public int getNr(int id) throws ClassNotFoundException, SQLException, java.lang.Exception {
        this.db.conectar();
        int nr = -1;
        ResultSet aux = this.db.executeQuery("SELECT COUNT (*) FROM palabraPorDocumento WHERE idPalabra= " + id + ";");
        if (aux.next()) {
            nr = aux.getInt(1);
        }
        this.db.desconectar();
        return nr;
    }

    public int getTf(int idWord, int idDoc) throws Exception {
        String query = "SELECT d.cantidad FROM palabraPorDocumento d WHERE idDocumento=" + idDoc + " AND idPalabra=" + idWord + ";";
        int tf = -1;
        this.db.conectar();
        ResultSet aux = this.db.executeQuery(query);
        if (aux.next()) {
            tf = aux.getInt(1);
        }
        this.db.desconectar();
        return tf;
    }

    public Archivo[] getArchivos(int idWord) throws Exception {

        int nr = this.getNr(idWord);
        Archivo[] archivos = new Archivo[nr];
        this.db.conectar();
        String query = "SELECT d.iddocumento, d.nombre, d.path FROM documento d INNER JOIN palabraPorDocumento p ON p.idPalabra=" + idWord + " AND d.iddocumento=p.iddocumento;";

        try {
            ResultSet rs = this.db.executeQuery(query);
            int index = 0;
            while (rs.next()) {
                int idArchivo = rs.getInt(1);
                String nomArchivo = rs.getString(2);
                String pathArchivo = rs.getString(3);
                Archivo aux = new Archivo(nomArchivo, idArchivo, pathArchivo);
                archivos[index] = aux;
                index++;

            }
        } catch (Exception e) {
            System.out.println("ERROR AL RECUPERAR ID Y NOMBRE.");
            System.exit(0);
        }
        this.db.desconectar();
        return archivos;
    }

    public double getDi(double logaritmo, Archivo[] toArrayArchivos, int idWord) throws Exception {

        double acumulador = 0;
        for (int i = 0; i < toArrayArchivos.length; i++) {
            //Pido la cantidad de veces que aparece la Palabra en el archivo.
            Archivo aux = toArrayArchivos[i];
            int tfs = this.getTf(idWord, aux.getId());
            acumulador += Math.pow((tfs * logaritmo), 2);
        }

        return Math.pow(acumulador, 0.5);
    }

    public void calculateWeight(double N, String word) throws SQLException, Exception {

        int id = this.hs.get(word).getId();
        Archivo[] toArrayArchivos = this.getArchivos(id);

        //N se recibe por parametro, es la cantidad de documentos.
        //Cantidad de veces que la Palabra aparece en otros documentos.
        double nr = (double) this.getNr(id);

        //logaritmo de la cantidad de archivos sobre la cantidad de archivos en los que
        //existe la Palabra
        double logaritmo = Math.log10(N / nr);
        if (logaritmo == 0) {
            return;
        }

        double di = this.getDi(logaritmo, toArrayArchivos, id);

        for (int i = 0; i < toArrayArchivos.length; i++) {
            Archivo fileArray = toArrayArchivos[i];
            int tfr = this.getTf(id, fileArray.getId());

            toArrayArchivos[i].setWeight((tfr * logaritmo) / di);

            if (this.R.containsKey(fileArray.getName())) {
                Archivo fileHash = this.R.get(fileArray.getName());
                fileHash.addWeight(fileArray.getWeight());
            } else {
                this.R.put(fileArray.getName(), fileArray);
            }
        }

    }

    private void orderQ() throws Exception {
        this.wordsOfQ = new Palabra[q.length];
        int actual = 0;
        for (int i = 0; i < q.length; i++) {
            String word = q[i].toLowerCase();
            if (this.exists(word)) {
                this.wordsOfQ[actual] = this.hs.get(word);
                actual++;
            }

        }
        Palabra[] temp = new Palabra[actual];
        System.arraycopy(wordsOfQ, 0, temp, 0, actual);
        this.wordsOfQ = temp;
        this.ordenarQuick();
    }

    public boolean exists(String word) {
        return this.hs.containsKey(word);
    }

    public int recuperarN() throws Exception {
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

    private void addFilesToHashMap() throws Exception {
        for (int i = 0; i < this.wordsOfQ.length; i++) {
            String word = this.wordsOfQ[i].getWord();
            this.calculateWeight((double) this.recuperarN(), word);
        }
    }

    public void ordenarQuick() throws Exception {

        if (this.wordsOfQ.length > 0) {
            ordenarQuick(0, this.wordsOfQ.length - 1);
        }
//            else{
//                System.out.println("No hay resultados para la busqueda");
//                System.exit(0);
//            }
    }

    private void ordenarQuick(int izq, int der) throws Exception {
        if (der == 0) {
            return;
        }
        int i = izq, j = der;
        Palabra y;
        Palabra aux = this.wordsOfQ[(izq + der) / 2];
        int x = this.getNr(aux.getId());
        do {
            while (this.getNr(this.wordsOfQ[i].getId()) < x && i < der) {
                i++;
            }
            while (x < this.getNr(this.wordsOfQ[j].getId()) && j > izq) {
                j--;
            }
            if (i <= j) {
                y = this.wordsOfQ[i];
                this.wordsOfQ[i] = this.wordsOfQ[j];
                this.wordsOfQ[j] = y;
                i++;
                j--;
            }
        } while (i <= j);
        if (izq < j) {
            ordenarQuick(izq, j);
        }
        if (i < der) {
            ordenarQuick(i, der);
        }
    }

    private void orderResult() {

        if (this.result.length == 1) {
            return;
        }
        if (this.result.length != 0) {
            orderResult(0, this.result.length - 1);
        }
//        else{System.out.println("No hay resultados para la busqueda");
//        System.exit(0);
//        }
    }

    private void orderResult(int izq, int der) {
        int i = izq, j = der;
        Archivo y;
        double x = this.result[(izq + der) / 2].getWeight();
        do {
            while (this.result[i].getWeight() > x && i < der) {
                i++;
            }
            while (x > this.result[j].getWeight() && j > izq) {
                j--;
            }
            if (i <= j) {
                y = this.result[i];
                this.result[i] = this.result[j];
                this.result[j] = y;
                i++;
                j--;
            }
        } while (i <= j);
        if (izq < j) {
            orderResult(izq, j);
        }
        if (i < der) {
            orderResult(i, der);
        }
    }

    private void hashToArray() {
        this.result = this.R.values().toArray(new Archivo[0]);
        //this.orderResult();
    }

    @Override
    public String toString() {
        String s = "Results for <" + this.consultaInicial + ">";
        s += "\nArchivos: " + Arrays.toString(this.result);
        return s;
    }

    public Archivo[] getResult() {
        return this.result;
    }

//    public int contieneConsulta(String completePath, String consulta) throws FileNotFoundException, IOException{
//        String texto="";
//        String cadena;
//        FileReader f = new FileReader(completePath);
//        BufferedReader b = new BufferedReader(f);
//        while((cadena = b.readLine())!=null) {
//            texto += cadena;
//        }
//        int contador = 0;
//        System.out.println("TEXTO : "+ texto);
//        System.out.println("CONSULTA INICIAL : "+ consulta);
//        if(texto.contains(consulta)){return 1;}
//        return contador;
//    }
    public int contieneConsulta(String completePath, String consulta) throws FileNotFoundException, IOException {
        String texto = "";
        String cadena;
        FileReader f = new FileReader(completePath);
        BufferedReader b = new BufferedReader(f);
        while ((cadena = b.readLine()) != null) {
            texto += cadena;
        }
        int contador = 0;
        while (texto.toLowerCase().contains(consulta)) {
            texto = texto.substring(texto.toLowerCase().indexOf(consulta) + consulta.length(), texto.length());
            contador++;
        }
        return contador;
    }

    private void addBonusWeight() throws IOException {
        if (q.length > 1) {
            String consulta = "";
            for (int i = 0; i < q.length; i++) {
                String word = q[i].toLowerCase();
                consulta += " " + word;
            }
            consulta = consulta.substring(1);
            for (int i = 0; i < this.result.length; i++) {
                Archivo arch = this.result[i];
                String nameOfFile = arch.getName();
                int count = this.contieneConsulta(arch.getPath() + "/" + nameOfFile, consulta);
                System.out.println("COUNT :" + count);
                if (count != 0) {
                    System.out.println("ENTRO AL ADDWEIGHT");
                    arch.addWeight(0.5 * count);
                }

            }
        }
    }
}
