import pixelblaze.library.*;

Pixelblaze pb;
float bri;


void setup() {
  size(400,400);
  smooth();
  
  pb = new Pixelblaze(this,"ws://192.168.1.15:81");
  bri = 1.0;
  
  pb.getHardwareConfig();
  
}

void draw() {
  pb.setBrightness(bri);
  bri = (bri > 0.8) ? 0.125 : 1.0;
  delay(500); 
}
