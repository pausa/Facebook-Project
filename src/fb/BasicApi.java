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
	private CookieHandler cm = null;
	
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
		
		initCookieManager();
		
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
			OutputStreamWriter out = null;
			URL login = null;
			HttpURLConnection loginConn = null;
			
			System.out.println("--------------LOGIN:----------------");
			
			//costruisco l'URL per il login
			login = new URL (basePage, Urls.FB_LOGIN_PAGE);
			
			HttpURLConnection.setFollowRedirects(false);
			
			//apro una connessione verso la pagina
			loginConn = openConnection(login);
			
			System.out.println (loginConn.getResponseCode());
			
			//ottengo i cookies
			loginConn.getContent();
			loginConn.disconnect();
			
			//stampa a video di prova
			printCookies(loginConn);

			//apro una connessione per fare la post request
			loginConn = openConnection(login);
			loginConn.setRequestMethod("POST");

			loginConn.setDoOutput(true);
			
			//inizializzo il buffer di scrittura
			out = new OutputStreamWriter(loginConn.getOutputStream());
			
			//lo riempio con i parametri per il login
			out.write("email=" + uname + "&pass=" + password);
			
			//lo spedisco a facebook
			out.flush();
			out.close();
			
			loginConn.disconnect();
			
			//verifico la risposta
			System.out.println (loginConn.getResponseCode());
			System.out.println (loginConn.getRequestMethod());	
			
			printCookies(loginConn);
			
			System.out.println("-------------------END LOGIN---------" +
														  "-------------");
						
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
		try
		{
			System.out.println ("------------------------GET INFOS----------" +
																  "---------");
			//inizializzazione delle variabili
			URL infopage = null;
			HttpURLConnection infoConn = null;
			BufferedReader read = null;
			String line = null;
			
			int pfStart = -1, pfEnd = -1;
			CookieStore cs = ((CookieManager)cm).getCookieStore();
			
			//cerco l'user id all'interno dei cookies
			for (HttpCookie c : cs.getCookies())
			{
				if (c.getName().equals("c_user"))
				{
					user += c.getValue();
					break;
				}	
			}
			
			//stampo l'user id
			if (this.user.equals(""))
				System.out.println("Login errato");
			
			else 
				System.out.println("user id: " + user);
			
			//cerco il post form id all'interno della finestra di chat
			infopage = new URL(basePage, Urls.FB_CHAT_WIN);
			
			infoConn = openConnection(infopage);
			
			read = new BufferedReader(new InputStreamReader
											  (infoConn.getInputStream()));
			
			//cerco la linea corretta nel codice
			while ((line = read.readLine()) != null)
			{
				if (line.startsWith(Urls.FB_POST_FORM))
				{
					//estraggo la stringa
					pfStart = line.indexOf("value=");
					pfStart = line.indexOf("\"", pfStart);
					pfStart ++;
					
					pfEnd = line.indexOf("\"", pfStart);
					
					post_form_id = line.substring(pfStart, pfEnd);
					
				}
				else if (line.startsWith(Urls.FB_CHANNEL))
				{
					pfStart = line.lastIndexOf("channel");
					pfStart += 7;
					pfEnd = pfStart + 2;
					channel = line.substring(pfStart, pfEnd);	
				}
				
			}
			
			//stampo
			if (post_form_id == null)
				System.out.println("impossibile recuperare post_form_id");
			else
				System.out.println("post form: " + post_form_id);
			
			if (channel == null)
				System.out.println("impossibile recuperare channel");
			else
				System.out.println ("channel: " + channel);
			
			System.out.println ("----------------END GET INFOS--------------");
			
		}
		catch (MalformedURLException e)
		{
			System.out.println ("error: " + e.getCause());
		}
		catch (IOException e)
		{
			System.out.println ("errore imprevisto");
			e.printStackTrace();
		}
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
	 * inizializza il CookieManager, non dovrebbe essere chiamato solo se 
	 * si vogliono azzerare i cookies
	 */
	private void initCookieManager ()
	{
		cm = new utils.MyCookieManager();
		java.net.CookieHandler.setDefault(cm);		
	}

	/**
	 * apre una nuova connessione verso l'url puntato da *u* dandogli 
	 * l'user-agent definito nel file Urls.java
	 * @param u
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection openConnection (URL u) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection)u.openConnection();
		setUserAgent(conn);
		return conn;	
	}
	
	/**
	 * imposta l'user agent per la connessione
	 * @param u
	 */
	private void setUserAgent (URLConnection u)
	{
		u.setRequestProperty("User-Agent", Urls.USER_AGENT);	
	}
	
	/**
	 * stampa a video i cookies restituiti dalla connessione
	 * @param conn
	 */
	private void printCookies (HttpURLConnection conn)
	{
		CookieStore cs = ((CookieManager)cm).getCookieStore();
		
		int i = 0;
		System.out.println ("----------Cookies--------------");
		for (i = 0; i < 99; i++)
		{
			String s = conn.getHeaderFieldKey(i);
			
			if (s != null && s.equals("Set-Cookie"))
			{
				System.out.println ("Set-Cookie: " + conn.getHeaderField(i));
			}
				
			
		}
		
		System.out.println("-------------------------------");
		
		for (HttpCookie c : cs.getCookies())
			System.out.println (c.toString());
		
		System.out.println("-------------------------------------------");
	}
	
}
