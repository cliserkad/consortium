package xyz.cliserkad.consortium;

public class ImpossibleTransformException extends Exception {
	private static final String MESSAGE_TEMPLATE_1 = "Impossible to transform from class ";
	private static final String MESSAGE_TEMPLATE_2 = " to class ";


	public ImpossibleTransformException(Class<?> from, Class<?> to) {
		super(MESSAGE_TEMPLATE_1 + from.getName() + MESSAGE_TEMPLATE_2 + to.getName());
	}

}
