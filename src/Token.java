/*Token object  contains two things
 * 1) token name
 * 2) token number
 */
public class Token {
	
	private String tokenName;
	private int tokenNumber;
	
	public Token(String token, int tokenNum) {
		this.tokenName = token;
		this.tokenNumber = tokenNum;
	}
	public String getTokenName() {
		return tokenName;
	}
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	public int getTokenNumber() {
		return tokenNumber;
	}
	public void setTokenNumber(int tokenNumber) {
		this.tokenNumber = tokenNumber;
	}
}
