package searcher;

/**
 *
 * @author julian-chino
 */
public class Archivo {
    
    private final String file;
    private double weight;
    private int id;
    private String path;
    
    
    public Archivo(String file, int id_archivo, String path) throws Exception{
        this.file = file;
        this.weight=0;
        this.id = id_archivo;
        this.path = path;
    }
    
    public boolean isFile(String file){
        return (this.file.equals(file));
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return this.id;
    }
    
    public String getName(){
        return this.file;
    }
    
    public void setWeight(double w){
        this.weight=w;
    }
    
    public String getPath(){
        return this.path;
    }
    
    public double getWeight(){
        return this.weight;
    }
    
    public void addWeight(double x){
        this.weight+=x;
    }
//    
//    public String getTitle() throws FileNotFoundException, IOException{
//        File f = new File(this.file);
//        List<String> lines = Files.lines(Paths.get(f.getPath()), Charset.forName("ISO-8859-1")).collect(Collectors.toList());
//        return(lines.get(0));
//    }
    
    
    @Override
    public String toString(){
        return "Archivo: "+this.file+ "-- Peso ("+this.weight+")";
    }
}
