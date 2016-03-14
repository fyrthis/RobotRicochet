package communication;

public class ProtocolException extends Exception {
	private static final long serialVersionUID = -1052167023349015942L;

	public ProtocolException() {
		super();
	}
	
	public ProtocolException(String err) {
		super(err);
	}
}
