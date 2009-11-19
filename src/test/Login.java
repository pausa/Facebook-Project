package test;

import java.net.MalformedURLException;

import fb.*;

public class Login
{

	/**
	 * effettua il login, utilizzo login "email" "password"
	 * @param args : "email" "password"
	 */
	public static void main (String[] args)
	{
		try
		{
			Api fb = new BasicApi (args[0], args[1]);
			
		} 
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			System.out.println ("Eccezzione inaspettata");
			e.printStackTrace();
		}

	}

}
