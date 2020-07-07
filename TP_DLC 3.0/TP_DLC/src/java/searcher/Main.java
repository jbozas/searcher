/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author julian - Chino
 */
public class Main {
    
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, Exception {
        
        //INDEXAR.
        new ManejoArchivo("/home/dlcusr/Documents/DocumentosTP1", true);
        
        //BUSCAR.
        String consulta = "el diesel";
        ModeloVectorial mv = new ModeloVectorial(consulta);
        System.out.println("Finalización correcta");
        System.out.println(mv.toString());
        
        
    }
}
