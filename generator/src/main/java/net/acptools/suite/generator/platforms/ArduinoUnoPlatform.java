package net.acptools.suite.generator.platforms;

public class ArduinoUnoPlatform extends ArduinoPlatform {

    @Override
    public int getNumberOfAnalogInputPins() {
        return 6;
    }

    @Override
    public int getNumberOfDigitalPins() {
        return 14;
    }

    @Override
    public int getNumberOfInterrupts() {
        return 2;
    }
}
