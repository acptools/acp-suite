//----------------------------------------------------------------------
// Includes required to build the sketch (including ext. dependencies)
#include <Blink.h>
//----------------------------------------------------------------------

void onBlink() {
	led.revert();
}

void buttonClicked() {
	if(button.getState() == HIGH) {
		if(blinkTimer.isEnabled()) {
			ACP_TRACE(F("Pause"));
			blinkTimer.disable();
		} else {
			ACP_TRACE(F("Play"));
			blinkTimer.enable();
		}
	}
}

void onStart() {
	Serial.begin(9600);
	tracer.setOutput(Serial);
}


