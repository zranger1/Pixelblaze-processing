package pixelblaze.library;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

/**
 * PBWebsocket - manages websocket communication with a single Pixelblaze
 * for internal use by pixelblaze-processing library.
 * @author ZRanger1
 */
public class PBWebsocket extends WebSocketClient {
	PixelblazePacket pkt;
	

	public PBWebsocket(URI serverUri, Draft draft) {
		super(serverUri, draft);
		pkt = new PixelblazePacket();
	}

	public PBWebsocket(URI serverURI) {
		super(serverURI);
		pkt = new PixelblazePacket();		
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Opened connection");
	}

	@Override
	public void onMessage(String message) {
		pkt.parse(message);
	}
	
	@Override
	public void onMessage(ByteBuffer message) {
		pkt.parse(message);		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codes are documented in class org.java_websocket.framing.CloseFrame
		System.out.println(
				"Connection closed by " + (remote ? "server" : "client. ") + " Code: " + code + " Reason: " + reason);
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}
}
