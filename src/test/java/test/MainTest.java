package test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import asw.Application;
import asw.agents.webService.request.PeticionChangeEmailREST;
import asw.agents.webService.request.PeticionChangePasswordREST;
import asw.agents.webService.request.PeticionInfoREST;
import asw.dbManagement.GetAgent;
import asw.dbManagement.model.User;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest {

	@Value("${local.server.port}")
	private int port;

	private URL base;
	private RestTemplate template;

	@Autowired
	private GetAgent getAgent;

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/");
		template = new TestRestTemplate();
	}

	@Test
	public void T1domainModelEqualsTest() {
		User agent1 = getAgent.getAgent("paco@hotmail.com");
		User agent2 = getAgent.getAgent("pac@hotmail.com");
		User agent3 = getAgent.getAgent("paco@hotmail.com");
		User agent4 = getAgent.getAgent("pepe@gmail.com");
		assertFalse(agent1.equals(agent2));
		assertFalse(agent1.equals(4));
		assertTrue(agent1.equals(agent3));
		assertTrue(agent1.equals(agent1));
		assertFalse(agent1.equals(agent4));
	}

	@Test
	public void T2domainModelToString() {
		User agent1 = getAgent.getAgent("paco@hotmail.com");
		assertEquals(agent1.toString(),
				"Agent [id=" + agent1.getIdentificador() + ", nombre=" + agent1.getNombre() 
				+ ", password=" + agent1.getPassword() + ", email=" + agent1.getEmail()
				+ ", ubicacion=" + agent1.getLocalizacion()
				+ ", identificador=" + agent1.getIdentificador()
				+ ", kind=" + agent1.getKind() 
				+ ", kindCode=" + agent1.getKind() + "]");
	}

	@Test
	public void T3domainModelHashCodeTest() {
		User agent1 = getAgent.getAgent("paco@hotmail.com");
		User agent3 = getAgent.getAgent("paco@hotmail.com");
		assertEquals(agent1.hashCode(), agent3.hashCode());
	}

	@Test
	public void T4agentExistAndCorrectPasssword() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/user";

		response = template.postForEntity(userURI, new PeticionInfoREST("paco@hotmail.com", "123456"), String.class);
		assertThat(response.getBody(), equalTo(
				"{\"firstName\":\"Paco\",\"email\":\"paco@hotmail.com\",\"id\":\"12345678A\"}"));

		response = template.postForEntity(userURI, new PeticionInfoREST("pepe@gmail.com", "123456"), String.class);
		assertThat(response.getBody(), equalTo(
				"{\"firstName\":\"Pepe\",\"email\":\"pepe@gmail.com\",\"id\":\"87654321B\"}"));

		response = template.postForEntity(userURI, new PeticionInfoREST("carmen@yahoo.com", "123456"), String.class);
		assertThat(response.getBody(), equalTo(
				"{\"firstName\":\"Carmen\",\"email\":\"carmen@yahoo.com\",\"id\":\"11223344C\"}"));
	}

	@Test
	public void T5agentDoNotExist() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/user";
		String userNotFound = "{\"reason\": \"User not found\"}";

		response = template.postForEntity(userURI, new PeticionInfoREST("ofelia@hotmail.com", "ajksdkje"),
				String.class);
		assertThat(response.getBody(), equalTo(userNotFound));

		response = template.postForEntity(userURI, new PeticionInfoREST("martin@hotmail.com", "shcxhqw"), String.class);
		assertThat(response.getBody(), equalTo(userNotFound));
	}

	@Test
	public void T6incorrectPassword() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/user";
		String incorrectPassword = "{\"reason\": \"Password do not match\"}";
		response = template.postForEntity(userURI, new PeticionInfoREST("paco@hotmail.com", "12356"), String.class);
		assertThat(response.getBody(), equalTo(incorrectPassword));

		response = template.postForEntity(userURI, new PeticionInfoREST("pepe@gmail.com", "12346"), String.class);
		assertThat(response.getBody(), equalTo(incorrectPassword));

		response = template.postForEntity(userURI, new PeticionInfoREST("carmen@yahoo.com", "13456"), String.class);
		assertThat(response.getBody(), equalTo(incorrectPassword));

		response = template.postForEntity(userURI, new PeticionInfoREST("isabel@gmail.com", "23456"), String.class);
		assertThat(response.getBody(), equalTo(incorrectPassword));
	}

	@Test
	public void T7emptyEmail() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/user";
		String emptyEmail = "{\"reason\": \"User email is required\"}";
		response = template.postForEntity(userURI, new PeticionInfoREST("", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionInfoREST("", "1223"), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionInfoREST("", "iewgs"), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionInfoREST("   ", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));
	}

	@Test
	public void T8invalidEmail() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/user";
		String wrongEmailStyle = "{\"reason\": \"Wrong mail style\"}";
		response = template.postForEntity(userURI, new PeticionInfoREST("ajsjc", ""), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionInfoREST("jxjsjd@", ""), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionInfoREST("chdgetc@chhsy", ""), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionInfoREST("sjhwuwsc", ""), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));
	}

	@Test
	public void T9emptyPassword() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/user";
		String emptyPassword = "{\"reason\": \"User password is required\"}";

		response = template.postForEntity(userURI, new PeticionInfoREST("paco@hotmail.com", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyPassword));

		response = template.postForEntity(userURI, new PeticionInfoREST("pepe@gmail.com", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyPassword));

		response = template.postForEntity(userURI, new PeticionInfoREST("carmen@yahoo.com", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyPassword));

		response = template.postForEntity(userURI, new PeticionInfoREST("isabel@gmail.com", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyPassword));
	}

	@Test
	public void T10emailRequiredChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		String emptyEmail = "{\"reason\": \"User email is required\"}";

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("", "", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("	", "", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("", "", "shfhs"), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));
	}

	@Test
	public void T12newEmailRequiredChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		String emptyEmail = "{\"reason\": \"User email is required\"}";

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "", "   "),
				String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "", "	"),
				String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));
	}

	@Test
	public void T13invalidEmailChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		String wrongEmailStyle = "{\"reason\": \"Wrong mail style\"}";

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco", "", ""), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@", "", "   "), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail", "  ", "	"), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));
	}

	@Test
	public void T14newInvalidEmailChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		String wrongEmailStyle = "{\"reason\": \"Wrong mail style\"}";

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "", "xhhuwi"),
				String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "", "fhgythf@"),
				String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "", "fhfyg@hotmail"),
				String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));
	}

	@Test
	public void T15emailChangeUserNotFound() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		String userNotFound = "{\"reason\": \"User not found\"}";

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("pao@hotmail.com", "123456", "pac@hotmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(userNotFound));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("pee@gmail.com", "123456", "pepe@hotmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(userNotFound));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("pa@hotmail.com", "123456", "fhfyg@hotmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(userNotFound));
	}

	@Test
	public void T16sameEmailErrorChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		String sameEmail = "{\"reason\": \"Same email\"}";

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "", "paco@hotmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(sameEmail));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("pepe@gmail.com", "", "pepe@gmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(sameEmail));

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("carmen@yahoo.com", "", "carmen@yahoo.com"),
				String.class);
		assertThat(response.getBody(), equalTo(sameEmail));
	}

	@Test
	public void T17emailRequiredPasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String emptyEmail = "{\"reason\": \"User email is required\"}";

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("", "", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("	", "chsh", ""), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("", "dfhe", "dhdgx"), String.class);
		assertThat(response.getBody(), equalTo(emptyEmail));
	}

	@Test
	public void T18inValidRequiredPasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String wrongEmailStyle = "{\"reason\": \"Wrong mail style\"}";

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("shdgr", "", ""), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("shdgr@", "", ""), String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("shdgr@hotmail", "", ""),
				String.class);
		assertThat(response.getBody(), equalTo(wrongEmailStyle));
	}

	@Test
	public void T19passwordRequiredPasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String passwordRequired = "{\"reason\": \"User password is required\"}";

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "", ""),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "", "dkdddd"),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "", "dkejd"),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));
	}

	@Test
	public void T20newPasswordRequiredPasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String passwordRequired = "{\"reason\": \"User password is required\"}";

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "djfhr", ""),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "djvhrhc", ""),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "dkejd", ""),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));
	}

	@Test
	public void T21samePasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String passwordRequired = "{\"reason\": \"Password Incorrect\"}";

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "djfhr", "djfhr"),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));

		response = template.postForEntity(userURI,
				new PeticionChangePasswordREST("paco@hotmail.com", "djvhrhc", "djvhrhc"), String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "dkejd", "dkejd"),
				String.class);
		assertThat(response.getBody(), equalTo(passwordRequired));
	}

	@Test
	public void T22notFoundAgentPasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String userNotFound = "{\"reason\": \"User not found\"}";

		response = template.postForEntity(userURI,
				new PeticionChangePasswordREST("pac@hotmail.com", "djfhr", "djfhrtt"), String.class);
		assertThat(response.getBody(), equalTo(userNotFound));

		response = template.postForEntity(userURI,
				new PeticionChangePasswordREST("martin@hotmail.com", "djvhrhc", "tt"), String.class);
		assertThat(response.getBody(), equalTo(userNotFound));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("juan@hotmail.com", "dkejd", "tt"),
				String.class);
		assertThat(response.getBody(), equalTo(userNotFound));
	}

	@Test
	public void T23notSamePasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String passwordIncorrect = "{\"reason\": \"Password Incorrect\"}";

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("paco@hotmail.com", "djfhr", "djfhr"),
				String.class);
		assertThat(response.getBody(), equalTo(passwordIncorrect));

		response = template.postForEntity(userURI,
				new PeticionChangePasswordREST("pepe@gmail.com", "djvhrhc", "djvhrhc"), String.class);
		assertThat(response.getBody(), equalTo(passwordIncorrect));

		response = template.postForEntity(userURI, new PeticionChangePasswordREST("carmen@yahoo.com", "dkejd", "dkejd"),
				String.class);
		assertThat(response.getBody(), equalTo(passwordIncorrect));
	}

	@Test
	public void T24testHtmlController() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/";

		response = template.getForEntity(userURI, String.class);
		assertThat(response.getBody().replace(" ", "").replace("\n", "").replace("\t", ""),
				equalTo(new String("<!DOCTYPEHTML><html><head><metacharset=\"UTF-8\"/><title>Login</title></head><body>"
						+ "<h1>Login</h1><formmethod=\"POST\"action=\"login\"><table><tr><td><labelfor=\"email\">"
						+ "<strong>Usuario:</strong></label></td><td><inputtype=\"text\"id=\"email\"name=\"email\"/>"
						+ "</td></tr><tr><td><labelfor=\"password\"><strong>Contraseña:</strong></label></td><td>"
						+ "<inputtype=\"password\"id=\"password\"name=\"password\"/></td></tr><tr>" + 
						"<td><label for=\"kind\"><strong>Tipo de Agente:</strong> </label></td>" + 
						"<td><input type=\"text\" id=\"kind\" name=\"kind\" /></td>" + 
						"</tr><tr><td><buttontype="
						+ "\"submit\"id=\"login\">Entrar</button></td></tr></table></form></body></html>").replace(" ",
								"")));
	}
	
	@Test
	public void emailChangeCorrect() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		
		String correctChange = "{\"agent\":\"pac@hotmail.com\",\"message\":\"email actualizado correctamente\"}";
		response = template.postForEntity(userURI, new PeticionChangeEmailREST("paco@hotmail.com", "123456", "pac@hotmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(correctChange));

		correctChange = "{\"agent\":\"pepe@hotmail.com\",\"message\":\"email actualizado correctamente\"}";
		response = template.postForEntity(userURI, new PeticionChangeEmailREST("pepe@gmail.com", "123456", "pepe@hotmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(correctChange));

		correctChange = "{\"agent\":\"fhfyg@hotmail.com\",\"message\":\"email actualizado correctamente\"}";
		response = template.postForEntity(userURI, new PeticionChangeEmailREST("carmen@yahoo.com", "123456", "fhfyg@hotmail.com"),
				String.class);
		assertThat(response.getBody(), equalTo(correctChange));
	}
	
	@Test
	public void correctPasswordChange() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String correctPassword = "{\"agent\":\"isabel@gmail.com\",\"message\":\"contraseña actualizada correctamente\"}";

		response = template.postForEntity(userURI,
				new PeticionChangePasswordREST("isabel@gmail.com", "123456", "djfhr"), String.class);
		assertThat(response.getBody(), equalTo(correctPassword));
	}
	
	@Test
	public void correctPasswordChangeXML() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changePassword";
		String correctChange = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<ChangeInfoResponse><message>contraseÃ±a actualizada correctamente</message>"
				+ "<agent>isabel@gmail.com</agent></ChangeInfoResponse>";

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new AcceptInterceptor());

		template.setInterceptors(interceptors);

		response = template.postForEntity(userURI,
				new PeticionChangePasswordREST("isabel@gmail.com", "djfhr", "123456"), String.class);
		assertThat(response.getBody(), equalTo(correctChange));
	}
	
	@Test
	public void emailChangeCorrectXML() {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		String userURI = base.toString() + "/changeEmail";
		String correctChange = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<ChangeInfoResponse><message>email actualizado correctamente</message>"
				+ "<agent>carmen@yahoo.com</agent></ChangeInfoResponse>";

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new AcceptInterceptor());

		template.setInterceptors(interceptors);

		response = template.postForEntity(userURI, new PeticionChangeEmailREST("fhfyg@hotmail.com", "123456", "carmen@yahoo.com"),
				String.class);
		assertThat(response.getBody(), equalTo(correctChange));
	}

	// Cabecera HTTP para pedir respuesta en XML
	public class AcceptInterceptor implements ClientHttpRequestInterceptor {
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			HttpHeaders headers = request.getHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
			return execution.execute(request, body);
		}
	}
}