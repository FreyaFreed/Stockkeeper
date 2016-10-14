package stockkeeper.network;

public class RegisterReturnMessage extends StockkeeperReturnMessage {

	String registrationMessage;
	boolean success;
	public RegisterReturnMessage(String registrationMessage, boolean success) {
		super(MessageType.REGISTER);
		this.registrationMessage = registrationMessage;
		this.success = success;

	}


}
