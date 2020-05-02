package cloud.godof.lambda.tags.exception;

/**
 * Exception to alert that there was an error connecting with the database.
 * @author aguilar
 *
 */
public class TagsException extends RuntimeException {

	private static final long serialVersionUID = -788689540860979324L;

	public TagsException() {
		super();
	}

	public TagsException(String message, Throwable cause) {
		super(message, cause);
	}

	public TagsException(String message) {
		super(message);
	}
	
	public TagsException(Throwable cause) {
		super(cause);
	}
	
}
