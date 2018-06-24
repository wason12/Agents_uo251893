package asw;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asw.dbManagement.model.User;
import asw.dbManagement.repository.AgentRepository;

@Service
public class InsertSampleData {
	
	@Autowired
	private AgentRepository inciRepo;
	
	@PostConstruct
	public void init() {
		
		User agente = new User("pepe", "4.0&4.0", "pepe@gmail.com", "pepedni", "agente" );
		
		inciRepo.save(agente);
	}

}
