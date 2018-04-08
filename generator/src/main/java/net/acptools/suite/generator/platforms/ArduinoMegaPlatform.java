package net.acptools.suite.generator.platforms;

public class ArduinoMegaPlatform extends ArduinoPlatform {

    @Override
    public int getNumberOfAnalogInputPins() {
        return 16;
    }

    @Override
    public int getNumberOfDigitalPins() {
        return 54;
    }

    @Override
    public int getNumberOfInterrupts() {
        return 6;
    }

    @Override
    public int getNumberOfHardwareSerials() {
        return 4;
    }

    @Override
    public String getBoardCliName() {
        return "arduino:avr:mega:cpu=atmega2560";
    }
}
