package cloud.godof.lambda.tags.exception;

/**
 * Exception to alert that there was an error connecting with the database.
 * @author aguilar
 *
 */
public class MacroTagsException extends RuntimeException {

	private static final long serialVersionUID = -788689540860979324L;

	public MacroTagsException() {
		super();
	}

	public MacroTagsException(String message, Throwable cause) {
		super(message, cause);
	}

	public MacroTagsException(String message) {
		super(message);
	}
	
	public MacroTagsException(Throwable cause) {
		super(cause);
	}
	
}
