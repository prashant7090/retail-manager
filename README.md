# Retail-Manager
A basic Spring boot Application for retail manager. Shops are stored in the Java in memory map. Near by shops can be retrive
by providing latitude and longitude of customer. Distance between customer and shop is calculated by haversine formula! The
latitude and longitude of shop is calculated using Google map API.

## Run the Project:
 1. Clone the Project
 2. Go to project's sub directory *complete*.
 3. Run command: ./gradlew clean build && java -jar build/libs/gs-actuator-service-0.1.0.jar
 
## Technology used:
 1. Spring Boot
 2. Junit 
 3. Gradle

## POST REQUEST:
  curl --request POST   
  --url http://localhost:9000/shop   
  --header 'cache-control: no-cache'   
  --header 'content-type: application/json'   
  --data '{"shopName": "ABC","shopAddress": "Vishal Nagar Pimpri-Chinchwad, Maharashtra","shopPostCode": "411027"}'
  
## GET REQUEST:
curl --request GET \
  --url 'http://localhost:9000/shop?latitude=18.556657&longitude=73.791819' \
  --header 'cache-control: no-cache'
  
  *Happy Learning!*
