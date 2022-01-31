# ProgettoDSBD_AllegraLentini_EventsApp

Il progetto può essere eseguito su Minikube, i comandi sono i seguenti:

•minikube ip

•Fare il bind del nome dell’applicazione “eventsapp.dev.loc” sull’indirizzo di minikube precedentemente ottenuto, tale bind va inserito su /etc/hosts

•eval $(minikube docker-env)

•minikube addons enable ingress

•kubectl apply -f K8s

Le immagini sono caricate sul docker hub e tramite i file di deploy vengono scaricate.

Prova:

1) Inserimento utente
curl eventsapp.dev.loc/usersapi/insertUser -X POST -H "Content-Type: application/json" -d '{"userName": "ciao", "email":"ciao@gmail.com","password":"ciao"}'

2) Inserimento evento
curl eventsapp.dev.loc/eventsapi/insertEvent -X POST -H "Content-Type: application/json" -d 
'{ "codice" : "CCC","nomeEvento" : "Concerto2022", "descrizione" : "CataniaCampoSportivo", "postiMax" : "70", "postiDisp" : "60", "prezzoBiglietto" :"20"}'

3) Inserimento prenotazione
curl eventsapp.dev.loc/reservsapi/createReserv/10/1 -X POST -H "Content-Type:application/json" -d '{"userName": "ciao","password":"ciao"}'

4) Get prenotazioni
curl eventsapp.dev.loc/reservsapi/getReserv
