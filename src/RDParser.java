/*
 *  GRAMMER
 *  S->aABa            	(1)
 *  A->aaA|b			(2,3)
 *  B->bB|epsilon		(4,5)
 *  
 *  Number refers to the rule number.
 *  
 */
public class RDParser {     

	int i;					//for reading input character by character
	char[] inputString;		
	String output=null;	
	boolean error = false;	//flag variable for error
	public RDParser(){
	}
	
	/*
	 * parseString function takes the string as an input and find the parse
	 * tree for given string if possible otherwise print the error.
	 */
	
	public void parseString(String input){
		i=0;
		error=false;
		output=null;
		
		inputString = input.toCharArray();
		System.out.println("\nInput : "+input);
		System.out.print("Parse Tree is : ");
		try {
			S(inputString[i]);  //giving the input to start state 'S'
		} catch (ArrayIndexOutOfBoundsException e) {
			error=true;
		}
		
		/*
		 * If there is an error while parsing the string then that means 
		 * String is not in the language. So i take a flag variable 'error'
		 * which will become true whenever a mismatch occur in the 
		 * matchToken() function.
		 * 
		 * If the string is parsed successfully then it the parsing order will
		 * be printed as an output. Numbers in output represents the rule number
		 * of the grammar.
		 */
		if(error){
			System.out.println("String not in Language");
		}else{
			System.out.println(output);  //Prints the parse order
		}
	}
	
	/*
	 * matchToken() takes a character token and match it with the input
	 * if it is same then index is increased so next token is processed.
	 * if it is not same then it means that string is not in language
	 * so it would raise an error.
	 */
	public void matchToken(char token) throws ArrayIndexOutOfBoundsException{
		if(token==inputString[i]){
			i++;
		}else{
			error=true;
		}
	}
	
	// Function for S->aABa
	private void S(char token) throws ArrayIndexOutOfBoundsException{
		switch(token){
		case 'a':
			print('1');			//Rule number of S->aABa
			matchToken('a');
			A(inputString[i]);
			B(inputString[i]);
			matchToken('a');
			break;
			
		default:
			error = true;
		}
	}

	/*
	 * Function for A->aaA | b
	 */
	private void A(char token) throws ArrayIndexOutOfBoundsException{
		switch(token){
		case 'a':
			print('2');			//Rule number of A->aaA
			matchToken('a');
			matchToken('a');
			A(inputString[i]);
			break;
			
		case 'b':
			print('3');			//Rule number of A->b
			matchToken('b');
			break;
			
		default:
			error = true;
		}
	}
	
	private void B(char token) throws ArrayIndexOutOfBoundsException{
		switch(token){
		case 'b':
			print('4');			//Rule number of B->bB
			matchToken('b');
			B(inputString[i]);
			break;
		
		default:
			print('5');			//Rule number of B->epsilon
			break;
		}
	}
	
	/*
	 * print() builds the output string.
	 * every time when print function is called a rule number is given to it as
	 * a parameter and it is appended to the output sequence.
	 */
	public void print(char ruleNumber){
		if(output==null){			
			output=ruleNumber+"";
		}else{
			output=output+ruleNumber;
		}
	}
}
