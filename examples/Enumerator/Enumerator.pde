import pixelblaze.library.*;
import java.util.*;

Pixelblaze pb;
PixelblazeEnumerator pbe;
float bri;
LinkedList pbList;


void setup() {
  size(400,400);
  smooth();
  
//  pb = new Pixelblaze(this,"ws://192.168.1.15:81");
  pbe = new PixelblazeEnumerator();
  pbe.setDeviceTimeout(6000);
  bri = 1.0;
  
//  pb.getHardwareConfig();
  
}

void draw() {
 
  pbList = pbe.getPixelblazeList();
  println(pbList);
  delay(2000); 
}
