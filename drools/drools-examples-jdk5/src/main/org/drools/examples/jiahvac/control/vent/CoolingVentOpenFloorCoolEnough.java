package org.drools.examples.jiahvac.control.vent;

import org.drools.examples.jiahvac.model.HeatPump;
import org.drools.examples.jiahvac.model.Vent;
import org.drools.examples.jiahvac.model.TempuratureControl;
import org.drools.examples.jiahvac.model.Thermometer;
import org.drools.semantics.annotation.DroolsRule;
import org.drools.semantics.annotation.DroolsParameter;
import org.drools.semantics.annotation.DroolsCondition;
import org.drools.semantics.annotation.DroolsConsequence;

@DroolsRule
public class CoolingVentOpenFloorCoolEnough
{
    @DroolsCondition
    public boolean condition(@DroolsParameter("vent") Vent vent,
                             @DroolsParameter("thermometer") Thermometer thermometer,
                             @DroolsParameter("pump") HeatPump pump,
                             @DroolsParameter("control") TempuratureControl control) {
        return isPumpCooling(pump) 
                && isVentOpen(vent)
                && isCoolEnough(thermometer, control)
                && isSameFloor(vent, thermometer, pump);
    }
    
    //@Drools.Condition
    public boolean isPumpCooling(@DroolsParameter("pump") HeatPump pump) {
        return pump.getState() == HeatPump.State.COOLING;
     }
    
    //@Drools.Condition
    public boolean isVentOpen(@DroolsParameter("vent") Vent vent) {
        return vent.getState() == Vent.State.OPEN;
     }
    
    //@Drools.Condition
    public boolean isCoolEnough(@DroolsParameter("thermometer") Thermometer thermometer,
                                @DroolsParameter("control") TempuratureControl control) {
        return control.isCoolEnough(thermometer.getReading());
    }
    
    //@Drools.Condition
    public boolean isSameFloor(@DroolsParameter("vent") Vent vent,
                               @DroolsParameter("thermometer") Thermometer thermometer,
                               @DroolsParameter("pump") HeatPump pump) {
        return vent.getFloor() == thermometer.getFloor()
                && vent.getFloor().getHeatPump() == pump;
    }
    
    @DroolsConsequence
    public void consequence(@DroolsParameter("vent") Vent vent) {
        vent.setState(Vent.State.CLOSED);
        System.out.println("CoolingVentOpenFloorCoolEnough: " + vent
                           + ", " + vent.getFloor().getThermometer());
    }
}