package org.drools.examples.jiahvac.control.rules;

import org.drools.examples.jiahvac.model.HeatPump;
import org.drools.examples.jiahvac.model.TempuratureControl;
import org.drools.examples.jiahvac.model.Thermometer;
import static org.drools.examples.jiahvac.model.HeatPump.State.*;
import org.drools.semantics.annotation.Rule;
import org.drools.semantics.annotation.Parameter;
import org.drools.semantics.annotation.Condition;
import org.drools.semantics.annotation.Consequence;

@Rule(defaultParameterAnnotation=true)
public class FloorTooColdPumpOff
{
    @Condition
    public boolean isPumpOff(HeatPump pump) {
        return pump.getState() == OFF;
     }

    @Condition
    public boolean isPumpServicingFloor(HeatPump pump, Thermometer thermometer) {
        return thermometer.getFloor().getHeatPump() == pump;
    }

    @Condition
    public boolean isTooCold(Thermometer thermometer, TempuratureControl control) {
        return control.isTooCold(thermometer.getReading());
    }

    @Consequence
    public void consequence(HeatPump pump) {
        pump.setState(HEATING);
        System.out.println("FloorTooColdPumpOff: " + pump);
    }
}