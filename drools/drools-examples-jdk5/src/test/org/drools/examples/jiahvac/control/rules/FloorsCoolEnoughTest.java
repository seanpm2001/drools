package org.drools.examples.jiahvac.control.rules;

import org.drools.examples.jiahvac.control.rules.FloorsCoolEnough;
import org.drools.examples.jiahvac.model.HeatPump;

public class FloorsCoolEnoughTest extends HVACRuleTestCase
{
    private FloorsCoolEnough rule = new FloorsCoolEnough();

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

    public void testIsPumpServicingFloorFalse() {
        mocks.replay();

        boolean result = rule.isPumpServicingFloor(mockPump_B.object, mockThermometer_1.object);

        mocks.verify();
        assertFalse(result);
    }

    public void testIsPumpServicingFloorTrue() {
        mocks.replay();

        boolean result = rule.isPumpServicingFloor(mockPump_A.object, mockThermometer_1.object);

        mocks.verify();
        assertTrue(result);
    }

    public void testIsAllFloorsCoolEnoughNoFloorCoolEnough() {
        setupThermometerReading(mockThermometer_1, 80.0);
        setupControlIsCoolEnough(mockTempuratureControl, 80.0, false);
        mocks.replay();

        boolean result = rule.isAllFloorsCoolEnough(mockPump_A.object,
                                                    mockThermometer_1.object,
                                                    mockTempuratureControl.object);
        mocks.verify();
        assertFalse(result);
    }

    public void testIsAllFloorsCoolEnoughSomeFloorsCoolEnough() {
        setupThermometerReading(mockThermometer_1, 80.0);
        setupThermometerReading(mockThermometer_2, 90.0);
        setupControlIsCoolEnough(mockTempuratureControl, 80.0, true);
        setupControlIsCoolEnough(mockTempuratureControl, 90.0, false);
        mocks.replay();

        boolean result = rule.isAllFloorsCoolEnough(mockPump_A.object,
                                                    mockThermometer_1.object,
                                                    mockTempuratureControl.object);
        mocks.verify();
        assertFalse(result);
    }

    public void testIsAllFloorsCoolEnoughAllFloorsCoolEnough() {
        setupThermometerReading(mockThermometer_1, 80.0);
        setupThermometerReading(mockThermometer_2, 90.0);
        setupControlIsCoolEnough(mockTempuratureControl, 80.0, true);
        setupControlIsCoolEnough(mockTempuratureControl, 90.0, true);
        mocks.replay();

        boolean result = rule.isAllFloorsCoolEnough(mockPump_A.object,
                                                    mockThermometer_1.object,
                                                    mockTempuratureControl.object);
        mocks.verify();
        assertTrue(result);
    }

    public void testConsequence() {
        mockPump_A.object.setState(HeatPump.State.OFF);
        mocks.replay();

        rule.consequence(mockPump_A.object);

        mocks.verify();
    }
}