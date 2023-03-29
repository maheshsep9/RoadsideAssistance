
# Roadside Assistance Service - Instructions

This document provides the instructions on how to run the test cases of the Roadside Assistance Service.
The application is built using Spring Boot and the tests are written using JUnit

## Prerequisites

IntelliJ IDEA (or any other IDE of your choice)
JDK 11 or higher
Maven

## Setup

1. Clone the project repository to your local machine using the following command:
2. git clone https://github.com/maheshsep9/RoadsideAssistance.git
3. Open the project in any preferred IDE (Implemented using IntelliJ ID)
4. Build the project using Maven.
5. Run the RoadsideAssistanceServiceImplTest class to execute the test cases.

## Test Cases

**The following test cases are included in the RoadsideAssistanceServiceImplTest class:**

**updateAssistantLocation**() - Test to update the location of an assistant.

**findNearestAssistants_Limit5**() - Test to find the 5 nearest assistants to a given location.

**findNearestAssistantsWhenEmpty**() - Test to handle the scenario when no assistant is available.

**reserveAssistant_Success**() - Test to reserve an available assistant.

**reserveAssistantWhenNotAvailable**() - Test to handle the scenario when the desired assistant is not available.

**releaseAssistant**() - Test to release a reserved assistant.

**releaseAssistantHasNoReservation**() - Test to handle the scenario when the customer has no prior reserved assistance service.

**releaseAssistantCustomerHasNoReservation**() - Test to handle the scenario when the customer has not reserved the given assistant.

Here is the test class to run the test cases - RoadsideAssistanceServiceImplTest class


Additional Notes & Assumptions
1. Project has been implemented without JPA or DB integration. Instead, used "DataLoad" service class to load with some test data.
2. Same has been used for test cases to load test data, instead Mockito can also be used to create Mock objects
