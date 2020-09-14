# webclient-soap

Different kind of SOAP calls using WebClient.

## Performance test

https://docs.locust.io/en/stable/

    pip install --user locust

    $ locust -f locust/javax.py --headless -u 1000 -r 100 --run-time 15m
    $ locust -f locust/jackson.py --headless -u 1000 -r 100 --run-time 15m
