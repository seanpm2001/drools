package org.drools.examples.jiahvac.control.rules;

import org.drools.examples.jiahvac.model.HeatPump;
import org.drools.examples.jiahvac.model.Vent;

public class CoolingVentOpenFloorCoolEnoughTest extends HVACRuleTestCase
{
    private CoolingVentOpenFloorCoolEnough rule = new CoolingVentOpenFloorCoolEnough();

    /*
     * Really, this method cannot fail. This test serves only as documentation of intent.
     */
    public void testIsPumpCooling() {
        for (HeatPump.State state : HeatPump.State.values()) {
            mocks.reset();
            setupPumpState(mockPump_A, state );
            mocks.replay();

            boolean result = rule.isPumpCooling(mockPump_A.object);

            mocks.verify();
            assertTrue((state == HeatPump.State.COOLING) ? result : !result );
        }
    }

    /*
     * Really, this method cannot fail. This test serves only as documentation of intent.
     */
    public void testIsVentOpen() {
        for (Vent.State state : Vent.State.values()) {
            mocks.reset();
            setupVentState(mockVent_1, state );
            mocks.replay();

            boolean result = rule.isVentOpen(mockVent_1.object);

            mocks.verify();
            assertTrue((state == Vent.State.OPEN) ? result : !result );
        }
    }

    public void testIsSameFloorAllDifferentFloors() {
        mocks.replay();

        boolean result = rule.isSameFloor(mockVent_1.object, mockThermometer_2.object, mockPump_B.object);

        mocks.verify();
        assertFalse(result);
    }

    public void testIsSameFloorPumpDifferentFloor() {
        mocks.replay();

        boolean result = rule.isSameFloor(mockVent_1.object, mockThermometer_1.object, mockPump_B.object);

        mocks.verify();
        assertFalse(result);
    }

    public void testIsSameFloorThermometerDifferentFloor() {
        mocks.replay();

        boolean result = rule.isSameFloor(mockVent_1.object, mockThermometer_2.object, mockPump_A.object);

        mocks.verify();
        assertFalse(result);
    }

    public void testIsSameFloorAllSameFloor() {
        mocks.replay();

        boolean result = rule.isSameFloor(mockVent_1.object, mockThermometer_1.object, mockPump_A.object);

        mocks.verify();
        assertTrue(result);
    }

    public void testIsCoolEnoughFalse() {
        setupThermometerReading(mockThermometer_1, 80.0);
        setupControlIsCoolEnough(mockTempuratureControl, 80.0, false);
        mocks.replay();

        boolean result = rule.isCoolEnough(mockThermometer_1.object, mockTempuratureControl.object);

        mocks.verify();
        assertFalse(result);

    }

    public void testIsNotCoolEnoughTrue() {
        setupThermometerReading(mockThermometer_1, 80.0);
        setupControlIsCoolEnough(mockTempuratureControl, 80.0, true);
        mocks.replay();

        boolean result = rule.isCoolEnough(mockThermometer_1.object, mockTempuratureControl.object);

        mocks.verify();
        assertTrue(result);

    }

    public void testConsequence() {
        mockVent_1.object.setState(Vent.State.CLOSED);
        mocks.replay();

        rule.consequence(mockVent_1.object);

        mocks.verify();
    }
}