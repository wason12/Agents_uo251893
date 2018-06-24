package asw.dbManagement;

import asw.dbManagement.model.User;

public interface UpdateInfo {
	/**
	 * Permite la solicitud de cambio de contraseña
	 */
	public void updatePassword(User agent, String password, String newPassword);
	
	public void updateEmail(User agent, String email);
}
