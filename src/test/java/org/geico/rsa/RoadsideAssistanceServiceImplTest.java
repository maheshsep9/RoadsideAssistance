package org.geico.rsa;

import org.geico.rsa.entity.Assistant;
import org.geico.rsa.entity.Customer;
import org.geico.rsa.entity.Geolocation;
import org.geico.rsa.service.DataLoad;
import org.geico.rsa.service.RoadsideAssistanceServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@Service
public class RoadsideAssistanceServiceImplTest {
    @Autowired
    private RoadsideAssistanceServiceImpl service = new RoadsideAssistanceServiceImpl();
    @Autowired
    DataLoad dataLoad = new DataLoad();
    private ArrayList<Assistant> assistants = new ArrayList<>();
    Customer customer;

    @Before
    public void setUp()  {
        customer = new Customer("TestCustomer1");

        //Local test data
        assistants.add(new Assistant("TestAssistant1", new Geolocation(38.2591, -77.2829), true));
        assistants.add(new Assistant("TestAssistant10", new Geolocation(40.3340, -77.3029), true));
    }

    @Test
    public void updateAssistantLocation() {
        System.out.println("Running Test");
        Assistant assistant = new Assistant("TestAssistant10", new Geolocation(39.3440, -77.4029), true);
        Geolocation newLocation = new Geolocation(39.3540, -78.3029);
        service.updateAssistantLocation(assistant, newLocation);
        assertEquals(newLocation, assistant.getLocation());
    }

    @Test
    public void findNearestAssistants_Limit5() {
        assistants = dataLoad.getAssistants();          //Can be replaced with Mock DataLoader - Mockito.when(dataLoad.getAssistants()).thenReturn(assistants);
        SortedSet<Assistant> nearestAssistants = service.findNearestAssistants(new Geolocation(39.2591,-77.2828), 5);
        assertEquals(5, nearestAssistants.size());
        assertEquals("Assistant1", nearestAssistants.first().getId());
    }

    @Test
    public void findNearestAssistantsWhenEmpty() {
        assistants = dataLoad.getAssistants();     //Can be replaced with Mock DataLoader - Mockito.when(dataLoad.getAssistants()).thenReturn(assistants);
        SortedSet<Assistant> nearestAssistants = service.findNearestAssistants(new Geolocation(20.2591,-77.2828), 1);
        assertEquals(0, nearestAssistants.size());
    }

    @Test
    public void reserveAssistant_Success() {
        Map<String, Assistant> reservedAssistants = new HashMap<>();
        assistants.get(0).setAvailable(false);
        reservedAssistants.put("1", assistants.get(0));   //customer id 1
        Optional<Assistant> reservedAssistant = service.reserveAssistant(customer, new Geolocation(39.2591,-77.2828));
        assertTrue(reservedAssistant.isPresent());
    }

    @Test
    public void reserveAssistantWhenNotAvailable() {
        Map<String, Assistant> reservedAssistants = new HashMap<>();
        assistants.get(0).setAvailable(false);
        assistants.get(1).setAvailable(false);
        reservedAssistants.put("1", assistants.get(0)); //customer id 1
        reservedAssistants.put("2", assistants.get(1)); //customer id 2
        Optional<Assistant> reservedAssistant = service.reserveAssistant(customer, new Geolocation(39.2591,-77.2828));
        assertFalse(reservedAssistants.get("1").isAvailable());
    }

    @Test
    public void releaseAssistant() {
        assistants = dataLoad.getAssistants();
        Optional<Assistant> reservedAssistant = service.reserveAssistant(customer, new Geolocation(39.2591,-77.2828));
        service.releaseAssistant(customer, reservedAssistant.get());
        assertTrue(reservedAssistant.get().isAvailable());
    }

    @Test
    public void releaseAssistantHasNoReservation() {
        dataLoad.saveReservedAssistants(customer.getId(), assistants.get(0));  //save the reserved Assistant
        Assistant newAssistant = new Assistant("Assistant11", new Geolocation(39.3440, -77.4029), true);

        // Call the releaseAssistant method and expect an IllegalStateException to be thrown
        Optional<Assistant> reservedAssistant = service.reserveAssistant(customer, new Geolocation(39.2591,-77.2828));
        Exception thrownException = assertThrows(RuntimeException.class, () -> service.releaseAssistant(customer, newAssistant));
        assertEquals("Given Assistant does not match with reserved Assistant for the customer", thrownException.getMessage());
    }

    @Test
    public void releaseAssistantCustomerHasNoReservation() {
        Assistant newAssistant = new Assistant("Assistant11", new Geolocation(39.3440, -77.4029), true);

        // Call the releaseAssistant method and expect an IllegalStateException to be thrown
        Exception thrownException = assertThrows(RuntimeException.class, () -> service.releaseAssistant(customer, newAssistant));
        assertEquals("Customer has not reserved any Assistance Service", thrownException.getMessage());
    }
}