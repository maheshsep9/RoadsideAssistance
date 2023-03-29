package org.geico.rsa;

import lombok.extern.slf4j.Slf4j;
import org.geico.rsa.entity.Assistant;
import org.geico.rsa.entity.Customer;
import org.geico.rsa.entity.Geolocation;
import org.geico.rsa.service.RoadsideAssistanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;
@Slf4j
@SpringBootApplication
public class RoadSideAssistanceApplication {

    private static RoadsideAssistanceServiceImpl roadsideAssistanceService;

    @Autowired
    public RoadSideAssistanceApplication(RoadsideAssistanceServiceImpl roadsideAssistanceService) {
        this.roadsideAssistanceService = roadsideAssistanceService;
    }

    public static void main(String[] args) {
        SpringApplication.run(RoadSideAssistanceApplication.class, args);
        log.info("Hello Roadside Assistance Service!");
        log.info("Loading Customer...");
        Customer customer = new Customer("Mahesh");
        //Reserve the available Assistant
        Optional<Assistant> reservedAssistant = roadsideAssistanceService.reserveAssistant(customer, new Geolocation(39.2591,-77.2828));
        log.info("Reserved Assistant ID:"+reservedAssistant.get().getId()+" hash:"+reservedAssistant.get().getLocation().getGeoHash().toBase32());
        //Release the Assistant
        roadsideAssistanceService.releaseAssistant(customer, reservedAssistant.get());
        log.info("Released Assistant ID:"+reservedAssistant.get().getId()+" hash:"+reservedAssistant.get().getLocation().getGeoHash().toBase32());
    }
}

