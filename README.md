# nyscraper

1. Make sure your port 5432 is not in use or change ports mapping in the docker-compose file
1. Run docker-compose with:
    ```bash
    docker-compose up -d
    ```
1. Run the backend
    ```bash
    sbt "headlines_api/runMain nyscraper.AppIo"
    ```
1. Navigate to http://127.0.0.1:8080/playground.html and make sure that http://127.0.0.1:8080/graphql is specified as the entry point
1. Run query:
    ```graphql
    query Get {
      news {
        title
        link
      }
    }
    ```
1. Run the crawler app
    ```bash
    sbt "crawler/runMain nyscraper.CrawlerApp"
    ```
1. Rerun the previous GraphQL query
1. `docker-compose down`