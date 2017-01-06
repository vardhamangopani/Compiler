/*
 * Scanner is based on the DFA. first it reads the buffer from the file.
 * whenever the getToken() is called by the parser then it will scan the
 * buffer to find the next token. After having getting a single token it
 * assigns a token number to that token and return that token number to 
 * parser. In the next token request from parser, it continues the scan 
 * from last position to find the next token. 
 * 
 * Token numbers are constants int the language and TokenNumbers.java
 * includes all the token number constants. I used this approach to 
 * enhance redability of program and for ease in correction.
 */
import java.io.IOException;
public class Scanner1 {

	int t;
	char[] buffer;
	int bufferPointer=0;
	String fileContent=null;
	public Scanner1(String inputFileName){
		ReadFile rf = new ReadFile();
		try {
			fileContent = rf.readFile(inputFileName);
			buffer=fileContent.toCharArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/************************************************************/
		/* this area is for testing the scanner for the whole input file
		 * this 4 lines are not needed while parsing. Because Parser
		 * calls getToken() method itself to get the next token. 
		 */
		System.out.println("Input File: \n"+fileContent+"\nOutput:(token -> tokenNumber)");
		while((t=getToken())!=TokNum.EOFTOK){
			System.out.println(t);
		}
		System.out.println(t);
		
		/************************************************************/
	}
	
	/*
	 * getToken() gets the next token from the input buffer and assigns a
	 * token number to that token and return that token number. 
	 */
	public int getToken(){
		boolean endOfToken = false;	//flag for indicating end of token
		int currentState=1;
		char ch;
		String token=null;
		
	/*
	 * this while loop scans the next token and after getting one token
	 * it halts. When the getToken() would be called next time then it 
	 * will start scanning from the last position shich is stored in the 
	 * bufferPointer variable. 
	 */
		while(!endOfToken){
			
			ch = buffer[bufferPointer];
			
			switch(currentState){
			case 1:      
				if(Character.isAlphabetic(ch)){
					bufferPointer++;
					currentState=2;
				}else if(Character.isDigit(ch)){
					bufferPointer++;
					currentState=3;
				}else if(ch=='+' || ch=='-'){
					bufferPointer++;
					currentState=6;
				}else if(ch=='*' || ch=='/'){
					bufferPointer++;
					currentState=7;
				}else if(ch=='<' || ch=='>'){
					bufferPointer++;
					currentState=8;
				}else if(ch=='('){
					bufferPointer++;
					currentState=9;
				}else if(ch==':'){
					bufferPointer++;
					currentState=13;
				}else if(ch=='.'){
					bufferPointer++;
					currentState=15;
				}else if(ch==')'){
					bufferPointer++;
					currentState=16;
				}else if(ch==';'){
					bufferPointer++;
					currentState=17;
				}else if(ch==','){
					bufferPointer++;
					currentState=18;
				}else if(ch=='\''){
					bufferPointer++;
					currentState=20;
				}else if(ch=='='){
					bufferPointer++;
					currentState=22;
				}else if(ch=='$'){
					bufferPointer++;
					currentState=23;
				}else if(ch==' ' || ch=='\r' || ch=='\t' || ch=='\n'){
					currentState=1;
					bufferPointer++;
				}else{
					endOfToken=true;
					System.out.println("invalid");
					currentState=-1; //-1 is a sink State      
				}
				break;
				
			case 2:
				if(Character.isAlphabetic(ch) || Character.isDigit(ch)){
					currentState=2;
					bufferPointer++;
				}else{
					endOfToken=true;
				}
				break;
				
			case 3:
				if(Character.isDigit(ch)){
					currentState=3;
					bufferPointer++;
				}else if(ch=='.'){
					currentState=4;
					bufferPointer++;
				}else{
					endOfToken=true;
				}
				break;
				
			case 4:
				if(Character.isDigit(ch)){
					currentState=5;
					bufferPointer++;
				}else{
					endOfToken=true;
				}
				break;
				
			case 5:
				if(Character.isDigit(ch)){
					currentState=5;
					bufferPointer++;
				}else{
					endOfToken=true;
				}
				break;
				
			case 6:
			case 7:
			case 8: 
					endOfToken=true;
				break;
			
			case 9:
				if(ch=='*'){
					currentState=10;
					bufferPointer++;
				}else{
					endOfToken=true;
				}
				break;
				
			case 10:
				if(ch!='*'){
					currentState=10;
					bufferPointer++;
				}else if(ch=='*'){
					currentState=11;
					bufferPointer++;
				}
				break;
				
			case 11:
				if(ch!=')'){
					currentState=10;
					bufferPointer++;
				}else{
					currentState=12;
					bufferPointer++;
				}
				break;
				
			case 12:
					endOfToken=true;
				break;
				
			case 13:
				if(ch=='='){
					currentState=14;
					bufferPointer++;
				}else{
					endOfToken=true;
				}
				break;
				
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 21:
			case 22:
			case 23:
					endOfToken=true;
				break;
			case 20:
				if(ch!='\''){
					currentState=20;
					bufferPointer++;
				}else{
					currentState=21;
					bufferPointer++;
				}
			}
			
			if(!endOfToken && ch!=' ' && ch!='\n' && ch!='\t' && ch!='\r')
				if(token==null)
					token=ch+"";
				else
					token=token+ch;
		}
		System.out.print(token+" -> ");
		
		//For returning relevant Token numbers
		switch(currentState){
		case 2:
			/*
			 * If the token is ID then it further checks for keywords 
			 * and assigns different token ids to each and every keyword.
			 */
			if(token.equals("if"))
				return TokNum.IFTOK;
			else if(token.equals("then"))
				return TokNum.THENTOK;
			else if(token.equals("else"))
				return TokNum.ELSETOK;
			else if(token.equals("while"))
				return TokNum.WHILETOK;
			else if(token.equals("do"))
				return TokNum.DOTOK;
			/*else if(token.equals("done"))
				return TokenNumbers.DONETOK;
		*/	else if(token.equals("const"))
				return TokNum.CONSTTOK;
			else if(token.equals("begin"))
				return TokNum.BEGTOK;
			else if(token.equals("end"))
				return TokNum.ENDTOK;
			else if(token.equals("read"))
				return TokNum.READTOK;
			else if(token.equals("write"))
				return TokNum.WRITETOK;
			else if(token.equals("var"))
				return TokNum.VARTOK;
			else if(token.equals("program"))
				return TokNum.PROGTOK;
			else if(token.equals("int") || token.equals("float"))
				return TokNum.BASTYPETOK;
			else if(token.equals("not"))
				return TokNum.NOTTOK;
			else 
				return TokNum.IDTOK;
		case 3:                                
		case 5:
			return TokNum.LITTOK;
		case 6:
			return TokNum.ADDOPTOK;	//+ -
		case 7:
			return TokNum.MULOPTOK;	//* /
		case 8:
			return TokNum.RELOPTOK;	// < >
		case 9:
			return TokNum.LPAREN; // (
		case 13:
			return TokNum.COLON;		// :
		case 14:
			return TokNum.ASTOK;		// :=
		case 15:
			return TokNum.DOT;		// .
		case 16:
			return TokNum.RPAREN; // )
		case 17:
			return TokNum.SEMICOLON;	// ;
		case 18:
			return TokNum.COMMA;		// ,
		case 21:
			return TokNum.STRLITTOK;	// 'xyz'
		case 22:
			return TokNum.EQTOK;		// =
		case 23:
			return TokNum.EOFTOK;		// $
		case -1:
			return TokNum.INVALID_TOKEN;      
		}	
		return 0;
	}
}
