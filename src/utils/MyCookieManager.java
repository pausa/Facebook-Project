package utils;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Classe sviluppata per modificare il comportamento del Cookie Manager di
 * default rispetto ad alcuni cookies.
 * @author pausa
 *
 */
public class MyCookieManager extends CookieManager
{

	public MyCookieManager ()
	{
		super();	
	}
	
	public MyCookieManager(CookieStore store,
            CookiePolicy cookiePolicy)
	{
		super (store, cookiePolicy);
	}
	
	/**
	 * modifico il metodo *put* in modo che rimuova il parametro *httponly*
	 * da ogni cookie.
	 */
	@Override
	public void put(URI uri, Map<String, List<String>> responseHeaders)
    													throws IOException
    {
		//inizializzo un nuovo header usare con il CookieManager originale
		Map <String, List<String>> newHeader = 
									new HashMap <String, List<String>>();
		
		List <String> temp = null, newList = new LinkedList <String>();
		
		//ottengo il valore associato alla chiave
		temp = responseHeaders.get("Set-Cookie");
		
		//se la chiave è presente
		if (temp != null)
		{
			/*aggiungo alla nuova lista tutti i cookie trovati, togliendo
			loro il parametro *httponly* */
			for (String s : temp)
				newList.add(s.replace("; httponly", ""));
			
			//inserisco in nuovi valori nel nuovo header
			newHeader.put("Set-Cookie", newList);
			
			//reinizializzo la lista di cookies
			newList = new LinkedList <String>();
		}
		
		
		temp = responseHeaders.get("Set-Cookie2");
		
		if (temp != null)
		{
			for (String s : temp)
				newList.add(s.replace("; httponly", ""));
			
			newHeader.put("Set-Cookie2", newList);
		}
		
		//chiamo il metodo *put* originale con il nuovo insieme di cookies
		super.put(uri, newHeader);
    }
}
