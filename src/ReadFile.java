/* This class is just for reading the input file. It reads
 * the input file line by line and build a string buffer 
 * and this string is returned to the caller of readFile().
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class ReadFile {
	private String everything;

	//reading an input file
	public String readFile(String filename) throws IOException{
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    everything = sb.toString(); 
		}
		return everything;
	}
}
