#My Retail API POC project

###Run the app
mvn clean spring-boot:run

###Testing
mvn clean verify

##Pricing REST APIs

###Request
`GET /price/products/{productId}`

    curl --location --request GET 'http://localhost:8080/price/products/13860428' \
    --header 'Accept: application/json' \
    --header 'Authorization: Basic foo'

###Response
    {
    "id": 13860428,
    "name": "The Big Lebowski (Blu-ray)",
    "current_price": {
    "value": 13.21,
    "currency_code": "USD"
    }
    }

###Request
`PUT /price/products/{productId}`
    
    curl --location --request PUT 'http://localhost:8080/price/products/12954218' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Basic foo' \
    --data-raw '{
    "id": 12954218,
    "name": "Kraft Macaroni &#38; Cheese Dinner Original - 7.25oz",
    "current_price": {
    "value": 10.34,
    "currency_code": "USD"
    }
    }'

###Response
    {
    "id": 12954218,
    "name": "Kraft Macaroni &#38; Cheese Dinner Original - 7.25oz",
    "current_price": {
    "value": 10.34,
    "currency_code": "USD"
    }
    }