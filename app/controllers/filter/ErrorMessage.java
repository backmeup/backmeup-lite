package controllers.filter;

public class ErrorMessage {
	
	public String message;
	
	public String type = "error";
	
	public ErrorMessage(String msg) {
		this.message = msg;
	}

}
