FROM resin/raspberry-pi-python:3.6
ENV INITSYSTEM on

WORKDIR /app

ADD ../out /app

EXPOSE 80

CMD ["python", "-m", "http.server", "80"]
