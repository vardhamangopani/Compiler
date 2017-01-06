/*SymTable class maintains the symbol table.
 * It performs the different operations on symbol table like
 * insert a symbol in symbol table, find a symbol in table
 * find a symbol in all active scopes and display the symbol 
 * table. 
 */
import java.util.Stack;

public class SymTable {

	private static final int SIZE = 13;
	Stack<SymTableEntry>[] st;

	@SuppressWarnings("unchecked")
	public SymTable() {
		st = new Stack[SIZE];
	}

	/* find function find the symbol in its local block
	 * it takes symbol to find and block number as parameters and
	 * it search through the symbol table for that symbol. if it 
	 * finds the symbol in the current block then it will return
	 * that symbol table entry. 
	 */
	public SymTableEntry find(String str, int block) {
		int hash = str.hashCode();
		if (hash < 0)
			hash = -hash;
		Stack<SymTableEntry> s = st[hash % SIZE];
		SymTableEntry sc;

		if (s == null)
			return null;
		else {
			for (int i = 0; i < s.size(); i++) {
				sc = s.get(i);
				if (sc.getIdent().equals(str) && sc.getBlockNumber() == block) {
					return sc;
				}
			}
		}
		return null;
	}

	/* findAll function finds the symbol in all the active blocks and 
	 * returns that entry if it finds it. activeBlock is a stack of
	 * integers which contains the active block numbers. 
	 */
	public SymTableEntry findAll(String str, Stack<Integer> activeBlock) {
		int hash = str.hashCode();
		if (hash < 0)
			hash = -hash;
		Stack<SymTableEntry> s;
		SymTableEntry sc;
		s = st[hash % SIZE];
		if (s != null)
			for (int j = activeBlock.size()-1; j>=0; j--)
				for (int i = 0; i < s.size(); i++) {
					sc = s.get(i);
					if (sc.getIdent().equals(str) && sc.getBlockNumber() == activeBlock.elementAt(j)) {
						return sc;
					}
				}

		return null;
	}

	/* insert function insert a symbol in symbol table. 
	 * it creates an entry in symbol table with that symbol,
	 * its location(block number), a boolean value to determine 
	 * if the symbol is a variable or a constant and its 
	 * offset in memory which is used in code generation.
	 * if it's a variable then isVariable = true
	 * if it's a constang then isVariable = false
	 * currOffset will be the location in the stack in memory
	 * and it will be used in code generation. 
	 */
	public void insert(String str, int block, boolean isVariable, char c, int currOffset) {
		int hash = str.hashCode();
		if (hash < 0)
			hash = -hash;
		int index = hash % SIZE;

		if (st[index] == null) {
			st[index] = new Stack<SymTableEntry>();
			st[index].push(new SymTableEntry(str, block, isVariable, c, currOffset));
		} else {
			st[index].push(new SymTableEntry(str, block, isVariable, c, currOffset));
		}
	}
	
	/*display function is used to display the complete symbol
	 * table. It prints the entire symbol table which includes
	 * 1) symbol
	 * 2) its type
	 * 3) block number in which that symbol is active
	 */
	public String display(int maxBlock) {
		int block = 0;
		String output = " ";

		Stack<SymTableEntry> s;
		for (int k = 0; k <= maxBlock; k++) {
			output = output + "\nScope " + block + ": ";
			for (int i = 0; i < st.length; i++) {
				s = st[i];
				if (s != null) {
					for (int j = 0; j < s.size(); j++) {
						if (s.get(j).getBlockNumber() == block)
							output = output +" lexeme:"+s.get(j).getIdent() + " Type:"
							+ s.get(j).getType()+" location:"+s.get(j).getLocation();
					}
				}
			}
			block++;
		}
		return output;
	}
}