/* LPas takes the filename as input and calls the parser
 * with that file. Parser will parse the contents of that 
 * file and use the scanner to do that.
 */
public class LPas {
	public static void main(String[] args) {
		Parser p = new Parser(args[0]);
		p.parse();
	}
}