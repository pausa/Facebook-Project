package fb;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * api a basso livello, si occupa di comunicare direttamente con facebook
 * @author pausa
 *
 */

public class BasicApi implements Api
{
	private String user = null;				//id utente
	private String post_form_id = null;		//post_form_id
	private String channel = null;			//canale assegnato all'utente
	private int seq = -1;					//# di sequenza del messaggio
	private URL basePage = null;			//url della pagina base
	private CookieManager cm = null;
	private String cookie = null;
	
	/**
	 * costruttore base, inizializza solo l'oggetto
	 * 
	 * @throws MalformedURLException nel caso in cui l'url sia errato
	 */
	public BasicApi () throws MalformedURLException
	{
		
		user = new String ("");
		post_form_id = new String ("");
		channel = new String ("");
		basePage = new URL (Urls.FB_BASE); 
		cm = new CookieManager();
		
		cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
		java.net.CookieHandler.setDefault(cm);
	}
	
	/**
	 * costruisce l'oggetto effettuando il login e recuperando i dati
	 * 
	 * @param uname: stringa rappresentante il nome utente
	 * @param password: stringa rappresentante la password
	 * @throws MalformedURLException nel caso in cui l'url sia errato
	 */
	public BasicApi (String uname, String password) 
								throws MalformedURLException
	{
		this();
		this.login(uname, password);
		this.getInfos();
		
	}
	
	@Override
	public void login (String uname, String password)
	{
		try
		{
			BufferedReader in = null;
			OutputStreamWriter out = null;
			URL login = null, home = null;
			URLConnection loginConn = null, homeConn = null;
			String testline = null;
			
			//costruisco l'URL per il login
			login = new URL (basePage, Urls.FB_LOGIN_PAGE);
			home = new URL (basePage, Urls.FB_HOME_PAGE);
//			
//			//creo una connessione per ricevere i cookies
//			homeConn = home.openConnection();
//			
//			setUserAgent (homeConn);
//			
//			
//			while ((testline = in.readLine()) != null);
//			
//			in.close();
//			
//			printCookies();
			
			//creo una connessione per inviare i dati
			loginConn = login.openConnection();
			
			setUserAgent(loginConn);
			
			loginConn.setDoOutput(true);
			
			//ottengo lo stream per mandare i dati
			out = new OutputStreamWriter (loginConn.getOutputStream());
			
			//scrivo i dati nello stream
			out.write("email=" + uname + "&pass=" + password);
			
			out.flush();
			
			loginConn.getContent();
			
			printCookies();
			
			//creo uno stream per la risposta
			in = new BufferedReader( new InputStreamReader(
											loginConn.getInputStream()));
			
			//recupero la risposta di facebook
			while ((testline = in.readLine()) != null)
				System.out.println (testline);
			
			in.close();
			
			loginConn = home.openConnection();
			
			loginConn.getContent();
			
			in = new BufferedReader( new InputStreamReader(
											loginConn.getInputStream()));

			//recupero la risposta di facebook
			while ((testline = in.readLine()) != null)
				System.out.println (testline);
			
			printCookies();
						
		}
		catch (MalformedURLException e)
		{
			System.out.println ("error: " + e.getCause());
		}
		catch (UnknownServiceException e)
		{
			System.out.println ("la pagina non supporta il POST");
		}
		catch (IOException e)
		{
			System.out.println ("errore imprevisto");
			e.printStackTrace();
		}
	}
	
	@Override
	public void getInfos ()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void getBuddyList ()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receive ()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send (String to, String text)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * imposta l'user agent per la connessione
	 * @param u
	 */
	private void setUserAgent (URLConnection u)
	{
		u.setRequestProperty("User-Agent", Urls.USER_AGENT);	
	}
	
	private void printCookies ()
	{
		CookieStore cs = cm.getCookieStore();
		
		for (HttpCookie c : cs.getCookies())
			System.out.println (c.toString());
		
		System.out.println("-------------------------------------------");
	}

}
