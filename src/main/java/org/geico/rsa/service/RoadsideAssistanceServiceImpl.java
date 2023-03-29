package org.geico.rsa.service;

import ch.hsr.geohash.GeoHash;
import lombok.extern.slf4j.Slf4j;
import org.geico.rsa.entity.Assistant;
import org.geico.rsa.entity.Customer;
import org.geico.rsa.entity.Geolocation;
import org.geico.rsa.util.RSACommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoadsideAssistanceServiceImpl implements RoadsideAssistanceService{
    @Autowired
    private DataLoad dataLoad = new DataLoad();
    private  Map<String, Customer> customers = new HashMap<>();
    @Override
    public void updateAssistantLocation(Assistant assistant, Geolocation assistantLocation) {
        assistant.setLocation(assistantLocation);
    }

    @Override
    public SortedSet<Assistant> findNearestAssistants(Geolocation geolocation, int limit) {
        ArrayList<Assistant> assistants = dataLoad.getAssistants();
        SortedSet<Assistant> nearestAssistants = new TreeSet<Assistant>((a1, a2) -> {
            double dist1 = RSACommonUtils.calculateDistance(geolocation.getLatitude(), geolocation.getLongitude(),a1.getLocation().getLatitude(), a1.getLocation().getLongitude());
            double dist2 = RSACommonUtils.calculateDistance(geolocation.getLatitude(), geolocation.getLongitude(),a2.getLocation().getLatitude(), a2.getLocation().getLongitude());
            return Double.compare(dist1, dist2);
        });
        if (assistants.isEmpty()) {
            return null;
        }
        log.info("Total Assistants loaded: " + assistants.size());
        try {

            /*********** Get Nearest Assistants based on GeoHash *************/
            // *** Database - we create the Reverse Index on GeoHash
            // O(1) --> Operation, Assuming the data stores in Reverse Index i.e. GeoHash -> Assistant
            /******  Step 1: Get Assistant services with in Same GeoHash Grid   ********/
            nearestAssistants.addAll(dataLoad.getNearestAssistantByGeoHash(geolocation.getGeoHash().toBase32()));
            ArrayList<Assistant> assistantsFromSameHashGrid = dataLoad.getNearestAssistantByGeoHash(geolocation.getGeoHash().toBase32());  //All neighbors might not be having the Assistants loaded,
            if(assistantsFromSameHashGrid!= null && !assistantsFromSameHashGrid.isEmpty()) {
                nearestAssistants.addAll(assistantsFromSameHashGrid);
            }

            /******  Step 2: Get also the Assistant services from neighbor GeoHash Grids to handle edge cases with border sharing  ********/
            GeoHash[] neighbors = geolocation.getGeoHash().getAdjacent();
            log.info("No of neighbors to the Customer GeoHash Grid:" + neighbors.length);
            for (GeoHash neighbor : neighbors) {
                ArrayList<Assistant> assistantsFromNeighborGrid = dataLoad.getNearestAssistantByGeoHash(neighbor.toBase32());  //All neighbors might not be having the Assistants loaded,
                if(assistantsFromNeighborGrid!= null && !assistantsFromNeighborGrid.isEmpty()) {
                    nearestAssistants.addAll(assistantsFromNeighborGrid);
                }
            }
        }catch (Exception e){
            //handle exception
            log.error("Error while fetching the nearest assistant providers:"+ e.getMessage());
        }
        return nearestAssistants.stream().filter(Assistant::isAvailable).limit(limit).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public Optional<Assistant> reserveAssistant(Customer customer, Geolocation customerLocation) {
        SortedSet<Assistant> nearByAssistants = findNearestAssistants(customerLocation, 1); //get the nearest
        if(nearByAssistants.isEmpty()){
            return Optional.empty();
        }
        log.info("Trying to locate the nearest assistant service available.....");
        //Get the nearest Assistance service
        Assistant assistant=  nearByAssistants.first();
        if(!assistant.isAvailable()){
            return Optional.empty();
        }
        log.info("Reserving the Assistant:"+assistant.getId()+" with the Customer ID:"+customer.getId());
        //reserve the assistance
        assistant.setAvailable(false);
        dataLoad.saveReservedAssistants(customer.getId(), assistant);  //track the Customer with reserved Assistant
        customers.put(customer.getId(), customer);
        return Optional.of(assistant);
    }
    @Override
    public void releaseAssistant(Customer customer, Assistant assistant) {
        Map<String, Assistant> reservedAssistants = dataLoad.getReservedAssistants();
        if(!reservedAssistants.containsKey(customer.getId())){
           throw new IllegalStateException("Customer has not reserved any Assistance Service");
        }
        if(!reservedAssistants.get(customer.getId()).getId().equals(assistant.getId())){
            throw new IllegalStateException("Given Assistant does not match with reserved Assistant for the customer");
        }
        log.info("Releasing the Assistant:"+assistant.getId()+" assigned to Customer ID:"+customer.getId());
        assistant.setAvailable(true);
        reservedAssistants.remove(customer.getId());
        customers.remove(customer.getId());
    }

}
