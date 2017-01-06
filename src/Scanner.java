/*
 * Scanner is based on the DFA. first it reads the buffer from the file.
 * whenever the getToken() is called by the parser then it will scan the
 * buffer to find the next token. After getting the next token, it returns
 * that token to parser. Token contains lexeme and token numbers in it. 
 * In the next token request from parser, it continues the scan from last 
 * position to find the next token. 
 * 
 * Token numbers are constants in the language and TokNum.java
 * includes all the token number constants. I used this approach to 
 * enhance readability of program and for ease in correction.
 */
import java.io.IOException;
public class Scanner {

	int t, totalBlocks=-1;
	char[] buffer;
	int bufferPointer=0; //to keep track of the last token 
	String fileContent=null;
	public Scanner(String inputFileName){
		ReadFile rf = new ReadFile(); 
		try {
			fileContent = rf.readFile(inputFileName); //reading file
			buffer=fileContent.toCharArray(); //create a buffer of char array
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Prints the input file
		System.out.println("Input File:\n"+fileContent+"\nOutput:");
	}
	
	/*
	 * getToken() gets the next token from the input buffer and assigns a
	 * token number to that token and return that complete token.  	 
	 */
	public Token getToken(){
		boolean endOfToken = false;	//flag for indicating end of token
		int currentState=1;
		char ch;
		String token=null;
		/*
		 * this while loop scans the next token and after getting one token
		 * it halts. When the getToken() would be called next time then it 
		 * will start scanning from the last position which is stored in the 
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
				}else if(ch=='\"'){
					bufferPointer++;
					if(token==null)
						token=ch+"";
					else
						token=token+ch;
					currentState=20;
				}else if(ch=='='){
					bufferPointer++;
					currentState=22;
				}else if(ch==' ' || ch=='\r' || ch=='\t' || ch=='\n'){
					currentState=1;
					bufferPointer++;
				}else{
					endOfToken=true;
					System.out.println("invalid");
					currentState=-1; //-1 is a sink State      
				}
				if(buffer.length<=bufferPointer){
					return new Token(token,TokNum.EOFTOK);
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
					token=null;
					currentState=1;
					//endOfToken=true;
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
				if(ch!='\"'){
					currentState=20;
					if(token==null)
						token=ch+"";
					else
						token=token+ch;
					bufferPointer++;
				}else{
					currentState=21;
					bufferPointer++;
				}
			}	//end of switch statement
			
			if(!endOfToken && ch!=' ' && ch!='\n' && ch!='\t' && ch!='\r' && currentState!=20)
				if(token==null)
					token=ch+"";
				else
					token=token+ch;
		}	//end of while loop
		
		//For returning relevant Token with their Token numbers
		switch(currentState){
		case 2:
			/*
			 * If the token is ID then it further checks for keywords 
			 * and assigns different token ids to each and every keyword.
			 */
			if(token.equals("if"))
				return new Token(token,TokNum.IFTOK);
			else if(token.equals("then"))
				return new Token(token,TokNum.THENTOK);
			else if(token.equals("while"))
				return new Token(token,TokNum.WHILETOK);
			else if(token.equals("do"))
				return new Token(token,TokNum.DOTOK);
			else if(token.equals("const"))
				return new Token(token,TokNum.CONSTTOK);
			else if(token.equals("begin"))
				return new Token(token,TokNum.BEGTOK);
			else if(token.equals("end"))
				return new Token(token,TokNum.ENDTOK);
			else if(token.equals("read"))
				return new Token(token,TokNum.READTOK);
			else if(token.equals("write"))
				return new Token(token,TokNum.WRITETOK);
			else if(token.equals("var"))
				return new Token(token,TokNum.VARTOK);
			else if(token.equals("program"))
				return new Token(token,TokNum.PROGTOK);
			else if(token.equals("true"))
				return new Token(token,TokNum.LITTOK);
			else if(token.equals("false"))
				return new Token(token,TokNum.LITTOK);
			else if(token.equals("int") || token.equals("float") || 
					token.equals("boolean"))
				return new Token(token,TokNum.BASTYPETOK);
			else if(token.equals("not"))
				return new Token(token,TokNum.NOTTOK);
			else if(token.equals("or"))
				return new Token(token,TokNum.ADDOPTOK);
			else if(token.equals("and"))
				return new Token(token,TokNum.MULOPTOK);
			else 
				return new Token(token,TokNum.IDTOK);
		case 3:                                
		case 5:
			return new Token(token,TokNum.LITTOK);
		case 6:
			return new Token(token,TokNum.ADDOPTOK);	//+ -
		case 7:
			return new Token(token,TokNum.MULOPTOK);	//* /
		case 8:
			return new Token(token,TokNum.RELOPTOK);	//< >
		case 9:
			return new Token(token,TokNum.LPAREN); 		//(
		case 13:
			return new Token(token,TokNum.COLON);		//:
		case 14:
			return new Token(token,TokNum.ASTOK);		//:=
		case 15:
			return new Token(token,TokNum.DOT);			//.
		case 16:
			return new Token(token,TokNum.RPAREN); 		//)
		case 17:
			return new Token(token,TokNum.SEMICOLON);	//;
		case 18:
			return new Token(token,TokNum.COMMA);		//,
		case 21:
			return new Token(token,TokNum.STRLITTOK);	//"xyz"
		case 22:
			return new Token(token,TokNum.EQTOK);		//=
		case -1:
			return new Token(token,TokNum.INVALID_TOKEN);      
		}	
		return null;
	}
}
