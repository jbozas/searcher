package searcher;

// * @author Bozas-Niclis-Perez Grosso Chino
 
public class Palabra {
    
    private final String p;
    private int cantApar;
    private int id;

    public Palabra(String p, int id_palabra) {
        this.p = p;
        this.cantApar = 1;
        this.id = id_palabra;
        
    }
    
    
    //encargado de sumar una aparicion a una palabra
    public void addApar(){
        this.cantApar+=1;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setCantApar(int n){
        this.cantApar = n;
    }
    
    public int getApar(){
        return this.cantApar;
    }
    
    public String getWord(){
        return this.p;
    }
    
    @Override
    public String toString(){
        return "Palabra: <<"+this.p +">>  nr: "+this.cantApar+"\n" ;
    }
}
