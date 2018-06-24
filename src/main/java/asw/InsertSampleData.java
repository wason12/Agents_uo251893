package asw;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asw.dbManagement.model.Agent;
import asw.dbManagement.repository.AgentRepository;

@Service
public class InsertSampleData {
	
	@Autowired
	private AgentRepository inciRepo;
	
	@PostConstruct
	public void init() {
		
		Agent agente = new Agent("pepe","pepe","pepe@hotmail.es","pepe","loca","agente",1);
		
		inciRepo.save(agente);
	}

}
