/* We have the lexemes in the form of a String array.
 * so for every each lexeme is scanned here and token numbers will be assigned
 * to each token
 * token numbers are listed in the Token number class as constants
 */

public class TokenOLD { 
	public TokenOLD(String[] lexemes){
		for(int i=0;i<lexemes.length;i++){
			int tokenNumber = assignTokenNumber(lexemes[i]);
			switch(tokenNumber){
			case TokNum.IDTOK: 
				System.out.println(lexemes[i]+" is an Identifier");
			break;
			case TokNum.LITTOK: 
				System.out.println(lexemes[i]+" is a Number");
			break;
			/*case TokenNumbers.COMMENT:
				System.out.println(lexemes[i]+" is a Comment");
			break;  */
			case TokNum.ASTOK: 
				System.out.println(lexemes[i]+" is an Assignment");
			break;
			case TokNum.LPAREN: 
				System.out.println(lexemes[i]+" is an Opening Brace");
			break;
			case TokNum.RPAREN: 
				System.out.println(lexemes[i]+" is a Closing Brace");
			break;
			case TokNum.COLON:
				System.out.println(lexemes[i]+" is a Colon");
			break;
			case TokNum.SEMICOLON:
				System.out.println(lexemes[i]+" is a Semicolon");
			break;
			case TokNum.DOT:
				System.out.println(lexemes[i]+" is a Dot");
			break;
			case TokNum.ADDOPTOK:
				System.out.println(lexemes[i]+" is a Add/Sub Operator");
			break;
			case TokNum.MULOPTOK:
				System.out.println(lexemes[i]+" is a Mul/Div Operator");
			break;
			case TokNum.RELOPTOK:
				System.out.println(lexemes[i]+" is a Relational Operator");
			break;
			case TokNum.SINGLE_INVERTED_COMMA:
				System.out.println(lexemes[i]+" is a Single Inverted Comma");
			break;
			case TokNum.DOUBLE_INVERTED_COMMA:
				System.out.println(lexemes[i]+" is a Double Inverted Comma");
			break;
			default:
				System.out.println(lexemes[i]+" is Invalid Token");
			break;		
			}
		}
	}

/* checkLexeme(lexeme) checks the lexeme passed to it as a parameter if it
 * is valid or not 
 * if yes, then it will assign the relevant token number to the lexeme
 * Token numbers constants are defined in TokenNumbers.java file
 */
	
	private int assignTokenNumber(String lexeme) {
		int currentState=1;
		char ch;
		for(int i=0;i<lexeme.length();i++){
			ch=lexeme.charAt(i);
			switch(currentState){
			case 1:
				if(Character.isAlphabetic(ch)){
					currentState=2;
				}else if(Character.isDigit(ch)){
					currentState=3;
				}else if(ch=='+' || ch=='-'){
					currentState=6;
				}else if(ch=='*' || ch=='/'){
					currentState=7;
				}else if(ch=='<' || ch=='>' || ch=='='){
					currentState=8;
				}else if(ch=='('){
					currentState=9;
				}else if(ch==':'){
					currentState=13;
				}else if(ch=='.'){
					currentState=15;
				}else if(ch==')'){
					currentState=16;
				}else if(ch==';'){
					currentState=17;
				}else if(ch=='\''){
					currentState=18;
				}else if(ch=='\"'){
					currentState=19;
				}else{
					currentState=-1; //-1 is a sink State      
				}
				break;
				
			case 2:
				if(Character.isAlphabetic(ch) || Character.isDigit(ch)){
					currentState=2;
				}else{
					currentState=-1;
				}
				break;
				
			case 3:
				if(Character.isDigit(ch)){
					currentState=3;
				}else if(ch=='.'){
					currentState=4;
				}else{
					currentState=-1;
				}
				break;
				
			case 4:
				if(Character.isDigit(ch)){
					currentState=5;
				}else{
					currentState=-1;
				}
				break;
				
			case 5:
				if(Character.isDigit(ch)){
					currentState=5;
				}else{
					currentState=-1;
				}
				break;
				
			case 6:
			case 7:
			case 8: 
				currentState=-1;
			break;
			
			case 9:
				if(ch=='*'){
					currentState=10;
				}else{
					currentState=-1;
				}
				break;
				
			case 10:
				if(ch!='*'){
					currentState=10;
				}else if(ch=='*'){
					currentState=11;
				}
				break;
				
			case 11:
				if(ch!=')'){
					currentState=10;
				}else{
					currentState=12;
				}
				break;
				
			case 12:
				currentState=-1;
				break;
				
			case 13:
				if(ch=='='){
					currentState=14;
				}else{
					currentState=-1;
				}
				break;
				
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19: 
				currentState=-1;
				break;
			}
			
		}
		
		switch(currentState){
		case 2:
			
			
			
			return TokNum.IDTOK;
		case 3:
		case 5:
			return TokNum.LITTOK;
		case 6:
			return TokNum.ADDOPTOK;
		case 7:
			return TokNum.MULOPTOK;
		case 8:
			return TokNum.RELOPTOK;
		case 9:
			return TokNum.LPAREN;
	/*	case 12:
			return TokenNumbers.COMMENT;	*/
		case 13:
			return TokNum.COLON;
		case 14:
			return TokNum.ASTOK;
		case 15:
			return TokNum.DOT;
		case 16:
			return TokNum.RPAREN;
		case 17:
			return TokNum.SEMICOLON;
		case 18:
			return TokNum.SINGLE_INVERTED_COMMA;
		case 19:
			return TokNum.DOUBLE_INVERTED_COMMA;
		case -1:
			return TokNum.INVALID_TOKEN;
		}
		
		return -1;
	}

	
}
