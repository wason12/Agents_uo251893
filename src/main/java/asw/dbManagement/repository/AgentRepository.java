package asw.dbManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import asw.dbManagement.model.User;

@Repository
public interface AgentRepository extends JpaRepository<User, Long> {
	
	/**
	 * Método que devuelve el Agente el cual es buscado por email
	 * en la base de datos
	 * @param email del identificador
	 * @return El Agente con dicho identificador
	 */
	public User findByIdentificador(String identificador);
	
}
