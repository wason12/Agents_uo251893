package asw.agents.util;

import asw.agents.factory.ErrorFactory;
import asw.agents.factory.ErrorFactory.Errors;
import asw.dbManagement.model.User;

public class Assert {

	/**
	 * 
	 * @param email
	 * @return excepcion si esta vacio
	 */
	public static boolean isEmailEmpty(String email) {
		if(email.trim().isEmpty())
			throw ErrorFactory.getError(Errors.REQUIRED_EMAIL);
		else
			return false;
	}
	
	public static boolean isPasswordEmpty(String password) {
		if(password.trim().isEmpty())
			throw ErrorFactory.getError(Errors.REQUIRED_PASSWORD);
		else
			return false;
	}

	/**
	 * Comprobacion de si el correo es valido
	 * @param email
	 * @return true si es valido.
	 */
	public static boolean isEmailValid(String email) {
		
		return true;
	}
	
	/**
	 * 
	 * @param agent
	 * @return devuelve false si no es null o excepcion
	 */
	public static boolean isAgentNull(User agent){
		if (agent == null) {
			throw ErrorFactory.getError(Errors.USER_NOT_FOUND);
		}
		return false;		
	}
	
	public static boolean isPasswordCorrect(String password,User agent){
		if (!password.equals(agent.getPassword())) {
			throw ErrorFactory.getError(Errors.INCORRECT_PASSWORD_DO_NOT_MATCH);
		}
		return true;
	}
	
	public static boolean isSamePassword(String password, String password2){
		if (password.equals(password2)) {
			throw ErrorFactory.getError(Errors.INCORRECT_PASSWORD);
		}
		return true;
	}

	public static boolean isSameEmail(String email, String email2){
		if (email.equals(email2)) {
			throw ErrorFactory.getError(Errors.SAME_EMAIL);
		}
		return true;
	}
	
	public static boolean isKindNull(String kind){
		if (kind == null) {
			throw ErrorFactory.getError(Errors.REQUIRED_KIND);
		}
		return false;		
	}

	public static boolean isKindCorrect(String kind, User agent) 
	{
		if (!kind.equals(agent.getKind())) {
			throw ErrorFactory.getError(Errors.INCORRECT_KIND);
		}
		return true;
	}
}
