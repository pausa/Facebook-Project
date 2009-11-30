package fb;

/**
 * contiene gli url necessari per l'interrogazione a facebook, ogni url
 * viene costruito a partire da FB_BASE
 * @author pausa
 *
 */

public abstract class Urls
{
	public final static String FB_BASE = new String 
										("http://www.facebook.com");
	
	public final static String FB_LOGIN_PAGE = new String ("/login.php");
	
	public final static String FB_HOME_PAGE = new String ("/home.php");
	
	public final static String FB_BUDDY_UPDATE = new String 
											("/ajax/presence/update.php");
	
	public final static String FB_SEND = new String 
												("/ajax/chat/send.php");
	
	public final static String FB_CHAT_WIN = new String 
												("/presence/popout.php");
	
	public final static String USER_AGENT = new String
								("Mozilla/5.0 (X11; U; Linux i686; en-US; " +
										"rv:1.9.1.3) Gecko/20090913 " +
										"Firefox/3.5.2");
	
	public final static String FB_POST_FORM = new String ("<input type=" +
									 	"\"hidden\" id=\"post_form_id\" " +
										   "name=\"post_form_id\" value=");
	
	public final static String FB_CHANNEL = new String ("channelManager.iframeLoad");
}
