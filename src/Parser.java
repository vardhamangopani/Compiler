/* VARDHAMAN GOPANI (gz5842)
 *************************************************************************
 * This is a Recursive Descent Parser for Little Pascal Language.
 * Parser parse the input file by using token numbers which are assigned
 * by the scanner. with every step it get the new token from scanner to 
 * work with. It get the new token after matching the current token. 
 * matchToken() matches the current token and get the new token from 
 * parser and return it to the parser and parser continues parsing.
 * At any stage, if a mismatch occur then 'error' flag's value gets
 * changed to true. At the end of the parsing if value of 'error' is
 * false, then the parsing is done successfully and there is no parsing
 * error in the program.
 * If there occurs a parsing error in the program then it will terminate 
 * displaying a message "Error while parsing" 
 * But if non fatal errors are detected, like variable declaration missing
 * or multiple declarations of variables or cannot change constant's value, 
 * then it will continue parsing but will give non fatal error.
 * 
 **********************************Rules**********************************
 * 1:program->PROGTOK IDTOK() constpart varpart BEGTOK statmt morestats ENDTOK.
 * 2,3: constpart -> CONSTTOK constdecl moreconstdecls | epsilon
 * 4,5: varpart -> VARTOK vardecl morevardecls | epsilon
 * 6,7: moreconstdecls -> constdecl moreconstdecls | epsilon
 * 8,9: morevardecls -> vardecl morevardecls | epsilon
 * 10: vardecl -> IDTOK : BASTYPETOK ;
 * 11: constdecl -> IDTOK = LITTOK ;
 * 12,13: morestats -> ; statmt morestats | epsilon
 * 14,15,16,17,18,19:statmt->assignstat|ifstat|readstat|writestat|blockst|whilest
 * 20: assignstat -> idnonterm ASTOK express
 * 21: ifstat -> IFTOK express THENTOK statmt
 * 22: readstat -> READTOK ( idnonterm )
 * 23: writestat -> WRITETOK ( writeexp )
 * 24: whilest -> WHILETOK express DOTOK statmt
 * 25: blockst -> varpart BEGTOK statmt morestats ENDTOK
 * 26,27: writeexp -> STRLITTOK | express
 * 28: express -> term expprime
 * 29,30: expprime -> ADDOPTOK term expprime | epsilon
 * 31: term -> relfactor termprime
 * 32,33: termprime -> MULOPTOK relfactor termprime | epsilons
 * 34: relfactor -> factor factorprime
 * 35,36: factorprime -> RELOPTOK factor | epsilon
 * 37,38,39,40: factor -> NOTTOK factor | idnonterm | LITTOK | ( express )
 * 41: idnonterm -> IDTOK
 ************************************************************************/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class Parser {
	
	public static boolean VARIABLE=true;
	public static boolean CONSTANT=false;
	Scanner scanner;
	boolean error = false;	//flag variable for error
	Token currentToken;
	int totalBlocks=0;
	Stack<Integer> activeBlock = new Stack<>(); //stack for active block
	SymTable symTable;
	String errDescription = "";
	File file;
	FileWriter fw;
	/*these are global variables used in code Generation
	  currOffset -> location in frame pointer
	  lable_no -> for printing the Strings of write()
	  jumpLableNo -> for conditions (if)
	  loopNo -> used in while loops. ex Loop1, Loop2
	  lbl -> for checking the equality of 2 operands in expression
	*/
	int currOffset=0,lable_no=0, jumpLableNo=1, loopNo=1, lbl=1;
	ExpRec er; //used to keep track of location and type of expressions
	
	/*strToPrint will make a buffer of all strings to be printed with their
	 * lables and it will be attached at the end in .data section of code */
	String strToPrint="";
	public Parser(String inputFileName){
		scanner = new Scanner(inputFileName);
		symTable = new SymTable(); //Symbol table initialization
		activeBlock.push(totalBlocks);
		file = new File("code.asm");
		er=new ExpRec();
		try {
			fw = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean parse(){
		error = false;
		currentToken=scanner.getToken();
		program(currentToken);
		//now it checks for eof. only comments are allowed after '.'
		if(currentToken.getTokenNumber()==TokNum.EOFTOK)	
			if(error){
				System.out.println("\nNon Fatal Error.");
				System.out.print(errDescription);
			}else
				System.out.println("\nSuccess.");
		else
			error();		
		System.out.println(symTable.display(totalBlocks));
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return error;
	}

	//program->PROGTOK IDTOK () constpart varpart BEGTOK statmt morestats ENDTOK.
	private void program(Token currentTok) {
		System.out.print("1 ");
		currentToken=matchToken(currentToken, TokNum.PROGTOK);
		currentToken=matchToken(currentToken, TokNum.IDTOK);
		currentToken=matchToken(currentToken, TokNum.LPAREN); //'('
		currentToken=matchToken(currentToken, TokNum.RPAREN); //')'
		writeProlog();
		constpart(currentToken);
		varpart(currentToken);
		currentToken=matchToken(currentToken, TokNum.BEGTOK); //begin
		statmt(currentToken);
		morestats(currentToken);
		currentToken=matchToken(currentToken, TokNum.ENDTOK); //end
		currentToken=matchToken(currentToken, TokNum.DOT);//.
		writePostlog();
	}
	
	// constpart -> CONSTTOK constdecl moreconstdecls | epsilon
	private void constpart(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.CONSTTOK:
			System.out.print("2 ");
			currentToken=matchToken(currentToken, TokNum.CONSTTOK);
			constdecl(currentToken);
			moreconstdecls(currentToken);
			break;
				
		default:
			System.out.print("3 ");
			break;
		}
	}

	// varpart -> VARTOK vardecl morevardecls | epsilon
	private void varpart(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.VARTOK:
			System.out.print("4 ");
			currentToken=matchToken(currentToken, TokNum.VARTOK);
			vardecl(currentToken);
			morevardecls(currentToken);
			break;
			
		default:
			System.out.print("5 ");
			break;
		}
	}
	
	// moreconstdecls -> constdecl moreconstdecls | epsilon
	private void moreconstdecls(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.IDTOK:
			System.out.print("6 ");
			constdecl(currentToken);
			moreconstdecls(currentToken);
			break;
		
		default:
			System.out.print("7 ");
			break;
		}
	}

	// morevardecls -> vardecl morevardecls | epsilon
	private void morevardecls(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.IDTOK:
			System.out.print("8 ");
			vardecl(currentToken);
			morevardecls(currentToken);
			break;
			
		default:
			System.out.print("9 ");
			break;
		}
	}

	// vardecl -> IDTOK : BASTYPETOK ;
	private void vardecl(Token currentTok) {
		System.out.print("10 ");
		SymTableEntry symTblEntry = symTable.find(currentTok.getTokenName(), 
				activeBlock.peek());
		if(symTblEntry!=null){
			error = true;
			errDescription+="Multiple Declaration Found For : "
					+currentToken.getTokenName()+"\n";
		}
		String lexeme=currentToken.getTokenName();
		currentToken=matchToken(currentToken, TokNum.IDTOK);
		currentToken=matchToken(currentToken, TokNum.COLON);
		if(symTblEntry==null){
			symTable.insert(lexeme, activeBlock.peek(), VARIABLE,
					currentToken.getTokenName().charAt(0), currOffset);
			currOffset-=4;
		}
		currentToken=matchToken(currentToken, TokNum.BASTYPETOK);
		currentToken=matchToken(currentToken, TokNum.SEMICOLON);
	}

	// constdecl -> IDTOK = LITTOK ;
	private void constdecl(Token currentTok) {
		System.out.print("11 ");
		SymTableEntry symTblEntry = symTable.find(currentTok.getTokenName(), 
				activeBlock.peek());
		if(symTblEntry==null){ 
		}else{
			error = true;
			errDescription+="Multiple Declaration Found For : "
					+currentToken.getTokenName()+"\n";
		}
		String lexeme = currentToken.getTokenName();
		currentToken=matchToken(currentToken, TokNum.IDTOK);
		currentToken=matchToken(currentToken, TokNum.EQTOK);
		if(symTblEntry==null){
			symTable.insert(lexeme, activeBlock.peek(), CONSTANT,
					currentToken.getTokenName().charAt(0), currOffset);
			currOffset-=4;
		}
		currentToken=matchToken(currentToken, TokNum.LITTOK);
		currentToken=matchToken(currentToken, TokNum.SEMICOLON);
	}
	
	// morestats -> ; statmt morestats | epsilon
	private void morestats(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.SEMICOLON:
			System.out.print("12 ");
			currentToken=matchToken(currentToken, TokNum.SEMICOLON);
			statmt(currentToken);
			morestats(currentToken);
			break;
			
		default:
			System.out.print("13 ");
			break;
		}
	}
	
	// statmt -> assignstat|ifstat|readstat|writestat|blockst|whilest 
	private void statmt(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.IDTOK:
			System.out.print("14 ");
			assignstat(currentToken);
			break;
		
		case TokNum.IFTOK:
			System.out.print("15 ");
			ifstat(currentToken);
			break;
			
		case TokNum.READTOK:
			System.out.print("16 ");
			readstat(currentToken);
			break;
			
		case TokNum.WRITETOK:
			System.out.print("17 ");
			writestat(currentToken);
			break;
			
		case TokNum.VARTOK:
		case TokNum.BEGTOK:
			System.out.print("18 ");
			blockst(currentToken);
			break;
			
		case TokNum.WHILETOK:
			System.out.print("19 ");
			whilest(currentToken);
			break;
			
		default:
			error();
		}
	}

	// assignstat -> idnonterm ASTOK express
	private void assignstat(Token currentTok) {
		int lhsLoc=1;
		char lhsType='a';
		System.out.print("20 ");
		SymTableEntry symTblEntry = symTable.findAll(currentTok.getTokenName(),
				activeBlock);
		if(symTblEntry!=null){
			lhsType = symTblEntry.getType();
			lhsLoc = symTblEntry.getLocation();
			if(!symTblEntry.isVariable()){ //check if constant
			error=true;
			errDescription+="Cannot modify value of constant : "
							+symTblEntry.getIdent()+"\n";
			}
		}
		idnonterm(currentToken);
		currentToken=matchToken(currentToken, TokNum.ASTOK);
		express(currentToken);
		if(symTblEntry!=null && lhsType!=er.type && symTblEntry.isVariable()){
			if(lhsType=='f' && er.type=='i'){
				codeGen("lw $t0 "+er.loc+"($fp)");
				codeGen("sw $t0 "+lhsLoc+"($fp)");
			}
			else{
			error=true;
			errDescription+="Type missmatch : "
					+lhsType +"->"+ er.type+"\n";
			}
		}else{ //if lhs->rhs is anything illegal except f->i
			codeGen("lw $t0 "+er.loc+"($fp)");
			codeGen("sw $t0 "+lhsLoc+"($fp)");
		}
	}

	// ifstat -> IFTOK express THENTOK statmt
	private void ifstat(Token currentTok) {
		System.out.print("21 ");
		String jumpLbl=jumpLable("IF");
		currentToken=matchToken(currentToken, TokNum.IFTOK);
		express(currentToken);
		if(er.type!='b'){
			error=true;
			errDescription+="Not a Boolean expression in IF condition\n";
		}else{
			codeGen("lw $t0 "+(currOffset+4)+"($fp)");
			codeGen("beq $t0 $0 "+jumpLbl);
		}
		currentToken=matchToken(currentToken, TokNum.THENTOK);
		statmt(currentToken);
		codeGen(jumpLbl+":");
	}

	// readstat -> READTOK ( idnonterm )
	private void readstat(Token currentTok) {
		System.out.print("22 ");
		currentToken=matchToken(currentToken, TokNum.READTOK);
		currentToken=matchToken(currentToken, TokNum.LPAREN);
		idnonterm(currentToken);
		codeGen("li $v0 5");
		codeGen("syscall");
		codeGen("sw $v0 "+er.loc+"($fp)");
		currentToken=matchToken(currentToken, TokNum.RPAREN);
	}

	// writestat -> WRITETOK ( writeexp )
	private void writestat(Token currentTok) {
		System.out.print("23 ");
		currentToken=matchToken(currentToken, TokNum.WRITETOK);
		currentToken=matchToken(currentToken, TokNum.LPAREN);
		writeexp(currentToken);
		currentToken=matchToken(currentToken, TokNum.RPAREN);
	}
	
	// whilest -> WHILETOK express DOTOK statmt
	private void whilest(Token currentTok) {
		System.out.print("24 ");
		String jumpLbl=jumpLable("WHILE");
		currentToken=matchToken(currentToken, TokNum.WHILETOK);
		codeGen("loop"+ loopNo++ +":");
		express(currentToken);
		if(er.type!='b'){
			error=true;
			errDescription+="Not a Boolean expression in WHILE condition\n";
		}else{
			if(error==false){
			codeGen("lw $t0 "+(er.loc)+"($fp)");
			codeGen("beq $t0 $0 "+jumpLbl);
		}
		}
		currentToken=matchToken(currentToken, TokNum.DOTOK);
		statmt(currentToken);
		codeGen("j loop"+(loopNo-1));
		codeGen(jumpLbl+":");
	}

	// blockst -> varpart BEGTOK statmt morestats ENDTOK
	private void blockst(Token currentTok) {
		System.out.print("25 ");
		varpart(currentToken);
		currentToken=matchToken(currentToken, TokNum.BEGTOK);
		statmt(currentToken);
		morestats(currentToken);
		currentToken=matchToken(currentToken, TokNum.ENDTOK);
	}
	
	// writeexp -> STRLITTOK | express
	private void writeexp(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.STRLITTOK:
			System.out.print("26 ");
			strToPrint+="lable"+lable_no+": .asciiz "
						+currentTok.getTokenName()+"\n";
			codeGen("la $a0 lable"+lable_no);
			codeGen("li $v0 4"); //4 for printing strings
			codeGen("syscall");
			lable_no++;
			currentToken=matchToken(currentToken, TokNum.STRLITTOK);
			break;
			
		case TokNum.IDTOK:
		case TokNum.NOTTOK:
		case TokNum.LITTOK:
		case TokNum.LPAREN:
			/*If any expression in write() then it this will generate a 
			 * code to print that value on screen.
			 */
			System.out.print("27 ");
			express(currentToken);
			codeGen("lw $a0 "+er.loc+"($fp)");
			codeGen("li $v0 1"); //1 for printing integers
			codeGen("syscall");
			break;
			
		default:
			error();
		}
	}

	// express -> term expprime
	private void express(Token currentTok) {
		System.out.print("28 ");
		term(currentToken);
		expprime(currentToken);
	}
	
	// expprime -> ADDOPTOK term expprime | epsilon
	private void expprime(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.ADDOPTOK:
			System.out.print("29 ");
			int op1Loc=er.loc;
			char op1Type=er.type;
			String operator=null;
			if(currentToken.getTokenName().equals("+"))
				operator="add ";
			else if(currentToken.getTokenName().equals("-"))
				operator="sub ";
			else if(currentToken.getTokenName().equals("or"))
				operator="or ";
			currentToken=matchToken(currentToken, TokNum.ADDOPTOK);
			term(currentToken);
			if(op1Type!=er.type){
				error=true;
				errDescription+="Different Data Types in one Expression\n";
			}else{
				if(operator.equals("or ")){
					if(op1Type!='b' || er.type!='b'){
						error = true;
						errDescription+="OR is not having boolean operands\n";
					}
				}
				if(error==false){
				codeGen("lw $t0 "+op1Loc+"($fp)");
				codeGen("lw $t1 "+er.loc+"($fp)");
				codeGen(operator+"$t0 $t0 $t1");
				codeGen("sw $t0 "+currOffset+"($fp)");
				er.loc=currOffset;
				currOffset-=4;
				}
			}
			expprime(currentToken);
			break;
			
		default:
			System.out.print("30 ");
			break;
		}
	}
	
	// term -> relfactor termprime
	private void term(Token currentTok) {
		System.out.print("31 ");
		relfactor(currentToken);
		termprime(currentToken);
	}
	
	// termprime -> MULOPTOK relfactor termprime | epsilons
	private void termprime(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.MULOPTOK:
			System.out.print("32 ");
			int op1Loc=er.loc;
			char op1Type=er.type;
			String operator=null;
			if(currentToken.getTokenName().equals("*"))
				operator="mul ";
			else if(currentToken.getTokenName().equals("/"))
				operator="div ";
			else if(currentToken.getTokenName().equals("and"))
				operator="and ";
			currentToken=matchToken(currentToken, TokNum.MULOPTOK);
			relfactor(currentToken);
			if(op1Type!=er.type){
				error=true;
				errDescription+="Different Data Types in one Expression\n";
			}else{
				if(operator.equals("and ")){
					if(op1Type!='b' || er.type!='b'){
						error = true;
						errDescription+="AND is not having boolean operands\n";
					}
				}
				codeGen("lw $t0 "+op1Loc+"($fp)");
				codeGen("lw $t1 "+er.loc+"($fp)");
				codeGen(operator+"$t0 $t0 $t1");
				codeGen("sw $t0 "+currOffset+"($fp)");
				er.loc=currOffset;
				currOffset-=4;
			}
			termprime(currentToken);
			break;
			
		default:
			System.out.print("33 ");
			break;
		}
	}
	
	// relfactor -> factor factorprime
	private void relfactor(Token currentTok) {
		System.out.print("34 ");
		factor(currentToken);
		factorprime(currentToken);
	}
	
	// factorprime -> RELOPTOK factor | epsilon
	private void factorprime(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.RELOPTOK:
			System.out.print("35 ");
			int op1Loc=er.loc;
			char op1Type=er.type;
			String operator=currentToken.getTokenName();
			currentToken=matchToken(currentToken, TokNum.RELOPTOK);
			factor(currentToken);
			if(op1Type!=er.type){
				error=true;
				errDescription+="Different Data Types in one Expression\n";
			}else{
				codeGen("lw $t0 "+op1Loc+"($fp)");
				codeGen("lw $t1 "+er.loc+"($fp)");
				if(operator.equals("<"))
					codeGen("slt $t2 $t0 $t1");
				else if(operator.equals(">"))
					codeGen("slt $t2 $t1 $t0");
				else if(operator.equals("=")){
					codeGen("beq $t0 $t1 Lbl"+lbl++);
					codeGen("li $t2 0");
					codeGen("j Lbl"+lbl++);
					codeGen("Lbl"+(lbl-2)+":\nli $t2 1");
					codeGen("Lbl"+(lbl-1)+":");
				}
				else 
					codeGen(operator+" $t2 $t0 $t1");
				codeGen("sw $t2 "+currOffset+"($fp)");
				er.loc=currOffset;
				er.type='b';
				currOffset-=4;
			}
			break;
			
		default:
			System.out.print("36 ");
			break;
		}
	}
	
	// factor -> NOTTOK factor | idnonterm | LITTOK | ( express )
	private void factor(Token currentTok) {
		switch(currentTok.getTokenNumber()){
		case TokNum.NOTTOK:
			System.out.print("37 ");
			currentToken=matchToken(currentToken, TokNum.NOTTOK);
			factor(currentToken);
			codeGen("lw $t0 "+er.loc+"($fp)");
			codeGen("not $t1 $t0");  //for not operation
			codeGen("sw $t1 "+currOffset+"($fp)");
			currOffset-=4;
			break;
			
		case TokNum.IDTOK:
			System.out.print("38 ");
			idnonterm(currentToken);
			break;
			
		case TokNum.LITTOK: //If Literal, then load immediate and store
			System.out.print("39 ");
			if(getType(currentTok.getTokenName())=='i'){
				codeGen("li $t0 "+currentTok.getTokenName());
				codeGen("sw $t0 "+currOffset+"($fp)");
				er.type=getType(currentTok.getTokenName());
				er.loc=currOffset;
				currOffset-=4;
			}else{
			/*this will set the expression type and location to current 
			 * token's type and location. */
			er.type=getType(currentTok.getTokenName()); 
			er.loc=currOffset;
			}
			currentToken=matchToken(currentToken, TokNum.LITTOK);
			break;
			
		case TokNum.LPAREN:
			System.out.print("40 ");
			currentToken=matchToken(currentToken, TokNum.LPAREN);
			express(currentToken);
			currentToken=matchToken(currentToken, TokNum.RPAREN);
			break;
			
		default:
			error();
			break;
		}
	}
	
	// idnonterm -> IDTOK
	private void idnonterm(Token currentTok) {
		System.out.print("41 ");
		SymTableEntry symTblEntry = symTable.findAll(currentTok.getTokenName(),activeBlock);
		if(symTblEntry!=null){
			er.type=symTblEntry.getType();
			er.loc=symTblEntry.getLocation();
			currentToken=matchToken(currentToken, TokNum.IDTOK);
		}else{
			error = true;
			errDescription+="No Declaration Found For : "
							+currentToken.getTokenName()+"\n";
			currentToken=matchToken(currentToken, TokNum.IDTOK);	
		}
			
	}
	
	/*this function returns the type of lexeme. return values are
	 * i: integers
	 * f: floats
	 * b: booleans 
	 */
	public char getType(String lexeme){
		if(lexeme.equals("true") || lexeme.equals("false"))
			return 'b';
		else if(lexeme.contains("."))
			return 'f';
		else 
			return 'i';
	}

	//returns the next lable for code generation for if/while statement
	public String jumpLable(String str){
		String lable=null;
		if(str.equals("IF")){
			lable = "Lable"+str+jumpLableNo;
			jumpLableNo++;
		}else if(str.equals("WHILE")){
			lable = "Lable"+str+jumpLableNo;
			jumpLableNo++;
		}
		return lable;
	}
	
	//writes the prolog to output file
	private void writeProlog() {
		codeGen("#Prolog:");
		codeGen(".text");
		codeGen(".globl main");
		codeGen("main:");
		codeGen("move $fp $sp #frame ptr will be start of active stack");
		codeGen("la $a0 ProgBegin");
		codeGen("li $v0 4");
		codeGen("syscall");
		codeGen("#End of Prolog\n");
	}
	
	//generates the actual code between prolog and postlog
	private void codeGen(String code){
		try {
			fw.write(code+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//generate postlog
	private void writePostlog() {
		codeGen("\n#PostLog:");
		codeGen("la $a0 ProgEnd");
		codeGen("li $v0 4");
		codeGen("syscall");
		codeGen("li $v0 10");
		codeGen("syscall");
		codeGen(".data");
		codeGen("ProgBegin: .asciiz \"Program Begin \\n\"");
		codeGen("ProgEnd: .asciiz \"\\nProgram End  \"");
		codeGen(strToPrint);//for printing all lables with their strings
	}
	
	// matchToken matches the current token return the next token
	private Token matchToken(Token token, int expectedToken){
		if(token.getTokenNumber()==TokNum.BEGTOK){
			totalBlocks++;					
			activeBlock.push(totalBlocks);//new block after BEGTOK
		}else if(token.getTokenNumber()==TokNum.ENDTOK)
			activeBlock.pop();
		try{
		if(token.getTokenNumber()==expectedToken){
			return scanner.getToken(); 
		}else
			error();
		}catch(ArrayIndexOutOfBoundsException e){
		}
		return null;
	}
	
	//error() is called whenever a parsing error occurs. After getting
	//a parsing error program stops parsing.
	private void error() {
		System.out.print("\nError while parsing");
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}

/*ExpRec is used for keeping track of type and location of the terms
 * in Expression or of the expression itself. */
class ExpRec{
	public int loc;
	public char type;
}