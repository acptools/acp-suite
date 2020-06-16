package net.acptools.suite.generator.platforms;

public class ArduinoNanoPlatform extends ArduinoPlatform {

    @Override
    public int getNumberOfAnalogInputPins() {
        return 8;
    }

    @Override
    public int getNumberOfDigitalPins() {
        return 14;
    }

    @Override
    public int getNumberOfInterrupts() {
        return 2;
    }

    @Override
    public String getBoardCliName() {
        return "arduino:avr:nano";
    }
}
