/* SymTableEntry is an entry in the symbol table
 *  it contains following information about symbol
 *  1) name
 *  2) block number in which it is defined
 *  3) if it is a variable(true) or constant(false)
 *  4) its data type(i->int, f->float, b->boolean)
 *  5) its offset on the frame pointer in data memory used
 *  	in Code Generation.
 */
class SymTableEntry {
	String ident;
	int blockNumber;
	boolean isVariable;
	char type;
	int location;
	
	public SymTableEntry(String s, int b, boolean isVariable, char c, int currOffset) {
		ident = s;
		blockNumber = b;
		this.isVariable = isVariable;
		this.type=c;
		location = currOffset;
	}
	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public boolean isVariable() {
		return isVariable;
	}

	public void setVariable(boolean variable) {
		this.isVariable = variable;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String symbol) {
		this.ident = symbol;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
	}
}
