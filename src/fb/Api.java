package fb;

/**
 * Rappresenta lo scheletro delle api, tutti metodi modificano variabili
 * interne
 * @author pausa
 *
 */
public interface Api
{
	/**
	 * Effettua la procedura di login
	 * @param uname : stringa che indica l'user name
	 * @param password : stringa che indica la password
	 */
	void login (String uname, String password);
	
	/**
	 * recupera id utente e post_form_id
	 */
	void getInfos ();
	
	/**
	 * ottiene la buddyList
	 */
	void getBuddyList ();
	
	/**
	 * Manda un messaggio
	 * @param to : stringa contenente l'id del destinatario
	 * @param text : testo del messaggio
	 */
	void send (String to, String text);
	
	/**
	 * riceve un messaggio dal server
	 */
	void receive ();
}
