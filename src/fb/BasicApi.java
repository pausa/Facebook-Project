package fb;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

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
			
			HttpURLConnection.setFollowRedirects(false);
			
			BufferedReader in = null;
			OutputStreamWriter out = null;
			URL login = null, home = null;
			HttpURLConnection loginConn = null, homeConn = null;
			String testline = null;
			
			
			//costruisco l'URL per il login
			login = new URL (basePage, Urls.FB_LOGIN_PAGE);
			home = new URL (basePage, Urls.FB_HOME_PAGE);
			
			loginConn = (HttpURLConnection)login.openConnection();
			initCookieManager();		
			setUserAgent(loginConn);
			

			System.out.println (loginConn.getResponseCode());
			loginConn.getContent();
			loginConn.disconnect();
			printCookies(loginConn);

			loginConn = (HttpURLConnection)login.openConnection();
			setUserAgent(loginConn);
			loginConn.setUseCaches(false);
			loginConn.setRequestMethod("POST");

			loginConn.setDoOutput(true);
			
			out = new OutputStreamWriter(loginConn.getOutputStream());
			
			out.write("email=" + uname + "&pass=" + password);
			out.flush();
			out.close();
			loginConn.disconnect();
						
			System.out.println (loginConn.getResponseCode());
			System.out.println (loginConn.getRequestMethod());
			
			loginConn.getContent();
			
//			in = new BufferedReader(new InputStreamReader(loginConn.getInputStream()));
//			
//			while ((testline = in.readLine()) != null)
//				System.out.println (">>" + testline);
//			
//			System.out.println (loginConn.getResponseCode());	
			
			
			printCookies(loginConn);
			
			
						
		}
//		catch (URISyntaxException e)
//		{
//			System.out.println ("error: " + e.getCause());
//			
//		}
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
	
	private void initCookieManager ()
	{
		cm = new CookieManager();
		((CookieManager)cm).setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		java.net.CookieHandler.setDefault(cm);		
	}
	/**
	 * imposta l'user agent per la connessione
	 * @param u
	 */
	private void setUserAgent (URLConnection u)
	{
		u.setRequestProperty("User-Agent", Urls.USER_AGENT);	
	}
	
	private void printCookies (HttpURLConnection conn)
	{
		CookieStore cs = ((CookieManager)java.net.CookieManager
										.getDefault()).getCookieStore();
		
		int i = 0;
		System.out.println ("----------Cookies--------------");
		for (i = 0; i < 99; i++)
		{
			String s = conn.getHeaderFieldKey(i);
			
			if (s != null && s.equals("Set-Cookie"))
				System.out.println ("Set-Cookie: " + conn.getHeaderField(i));
				
			
		}
		
		System.out.println("-------------------------------");
		
		for (HttpCookie c : cs.getCookies())
			System.out.println (c.toString());
		
		System.out.println("-------------------------------------------");
	}
	
}
