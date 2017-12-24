FROM resin/raspberry-pi-python:3.6

WORKDIR /app

ADD . /app

EXPOSE 8000

CMD ["python", "-m", "http.server", "8000"]
