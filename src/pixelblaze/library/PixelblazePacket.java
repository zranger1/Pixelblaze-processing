/**
 * 
 */
package pixelblaze.library;

import java.nio.ByteBuffer;
import java.util.Map;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author ZRanger1
 * Data returned from Pixelblaze websocket interface
 */
public class PixelblazePacket {
	
  // CONSTANTS
  private static final Charset UTF8 = Charset.forName("UTF-8");	
	
  // packet types
  private static final byte PKT_NONE = 0;
  private static final byte PKT_SAVE_PROGRAM_SOURCE = 1;
  private static final byte PKT_CODE_DATA = 3;
  private static final byte PKT_THUMBNAIL = 4;
  private static final byte PKT_PREVIEW_FRAME = 5;
  private static final byte PKT_SOURCE_DATA = 6;
  private static final byte PKT_PROGRAM_LIST = 7;
  private static final byte PKT_PIXEL_MAP = 8;
  private static final byte PKT_OUTPUT_EXPANDER_CONFIG = 9;

  // upgrade status codes
  private static final byte UPGRADE_UNKNOWN = 0;
  private static final byte UPGRADE_CHECKING = 1;
  private static final byte UPGRADE_UPDATING = 2;
  private static final byte UPGRADE_ERROR  = 3;
  private static final byte UPGRADE_UP_TO_DATE = 4;
  private static final byte UPGRADE_AVAILABLE = 5;
  private static final byte UPGRADE_COMPLETE = 6;
                 
  // program list packet subtypes
  private static final byte PROGLIST_START = 1;
  private static final byte PROGLIST_CONT  = 2;
  private static final byte PROGLIST_END   = 4;
  

  /**
   * Convert byte buffer to UTF-8 String.
   *
   * @param bytes Source byte buffer.
   * @return String expression of the bytes.
   */
  private static String msgToText(ByteBuffer bytes) {
     return new String(bytes.array(), UTF8);
  }	   
  
  void parse(String message) {
	System.out.println("received: " + message);	  
  }
  
  void parse(ByteBuffer message) {
	byte type, subtype;
	type = message.get();
	if (type == PKT_PROGRAM_LIST ) {
		System.out.println("Pattern List packet");		
	}
	subtype = message.get();
	if ((PROGLIST_START & subtype) != 0) { System.out.println("  start"); }
	if ((PROGLIST_CONT & subtype) != 0) { System.out.println("  continuation"); }		
	if ((PROGLIST_END & subtype) != 0) { System.out.println("  last packet"); }
	  
	String msg = msgToText(message);
	System.out.println("translated: " + msg);			  
  } 
}
