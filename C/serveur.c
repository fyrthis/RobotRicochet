#include <stdio.h>       /* standard I/O routines                     */
#define __USE_GNU 
#include <pthread.h>     /* pthread functions and data structures     */

#include <stdlib.h>      /* rand() and srand() functions              */

#include <netdb.h>
#include <netinet/in.h>
#include <string.h>

#include <sys/time.h>
#include <sys/ioctl.h>

#include <fcntl.h>
#include <errno.h>

#include <time.h>
#include <math.h>

#include <ctype.h>
#include <unistd.h>

#include "serveur.h"

pthread_cond_t  cond_got_task = PTHREAD_COND_INITIALIZER;


int nbSecondsTimer;
int etat_reso=0;
int isShutingDown=0;

pthread_t ptimer;
pthread_t  p_threads[NB_MAX_THREADS];       /* thread's structures   */



// client_t * clients = NULL;  --> ça pourrait très bien être le serveur qui intialise tout .. !!

/******************************************
*                                         *
*  Traite la tâche passée en paramètre :  *
*  1) Parse la commande de la tâche       *
*  2) Appelle la fonction associée        *
*                                         *
*******************************************/
// serveur.c
void handle_request(task_t * task, int thread_id) {
    if (task) {
        /* parse la commande */
        char * pch = (char*)calloc(strlen(task->command)+1, sizeof(char)); // TOKEN
        char* username = NULL;
        //printf ("(Server:serveur.c:handle_request) : Splitting string \"%s\" into tokens:\n",task->command);
        pch = strtok (task->command,"/");

        if(pch==NULL) //Should never happen.
        {
            printf("(Server:serveur.c:handle_request) : ERROR : received something NULL : %s.\n", task->command);
            perror("(Server:serveur.c:handle_request) : ERROR : close socket.\n");
            close(task->socket);
        }

        
        else if(strcmp(pch,"CONNEXION")==0) //C -> S : CONNEXION/user/
        {
            //ADD THE CLIENT AND WELCOME
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            addClient(task->socket, username, &client_mutex);
            send_bienvenue(username, task->socket);
            send_connexion(username, task->socket);
            
            //S -> C : SESSION/plateau/
            sendGrid(gridStr, task->socket);
        }


        else if(strcmp(pch,"SORT")==0) //C -> S : SORT/user/
        {
            //ICI SE FAIT LA SUPPRESSION D'UN CLIENT
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch), sizeof(char));
            strncpy(username, pch, strlen(pch));
            //printf("(Server:serveur.c:handle_request) : handle Task SORT\n");

            if(clients == NULL) { puts("(Server:serveur.c:handle_request) : Aucun client. Should never happened\n"); return; }

            disconnectClient(username, &client_mutex);
            send_deconnexion(username, task->socket);
        }


        else if(strcmp(pch,"SOLUTION")==0) //C -> S : SOLUTION/user/...
        {
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            if(phase == REFLEXION) //C -> S : SOLUTION/user/coups/
            {
                phase = ENCHERE;
                pch = strtok(NULL, "/");
                int solution = atoi(pch);
                tuAsTrouve(task->socket);
                ilATrouve(username, solution, task->socket);
                addEnchere(task->socket, username, solution, &enchere_mutex);
                fprintf(stderr, "(Server:serveur.c:handle_request) : Solution trouvée par %s\n", username);
            }
            else if(phase == RESOLUTION) //C -> S : SOLUTION/user/deplacements/
            { 
                
            }
            else {
                puts("ERROR !\n");
            }
        }


        
        else if(strcmp(pch,"ENCHERE")==0) //C -> S : ENCHERE/user/coups/
        {
            if(pthread_mutex_lock(&enchere_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");

            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            
            // TEST DE L'ENCHERE
            if(phase == ENCHERE){                
                pch = strtok(NULL, "/");
                int betSolution = atoi(pch);
                if(betSolution >= encheres->nbCoups) {
                    fprintf(stderr, "(Server:serveur.c:handle_request) : Enchère reçue de la part de %s refusee.\n", username);
                    send_echec(username, task->socket);
                }
                else {
                    fprintf(stderr, "(Server:serveur.c:handle_request) : Enchère reçue de la part de %s acceptee.\n", username);
                    send_validation(task->socket);
                    addEnchere(task->socket, username, betSolution, &enchere_mutex);
                }

            }
            else { //Si on n'est pas dans la phase d'enchere : un client hack pourrait provoquer ça. Ou bien une tâche qui arrive trop tard.
                fprintf(stderr, "(Server:serveur.c:handle_request) : ERROR, reçue enchère phase resolution\n");
            }
            if(pthread_mutex_unlock(&enchere_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
        }
        // erreur dans le protocole, à redéfinir mais correspond à l'envoi de la solution
        // proposée par le joueur actif lors de la phase de résolution
        else if(strcmp(pch,"ENVOISOLUTION")==0) {
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));

            
            // si la phase est a 2 alors on est dans la phase de résolution
            if(phase == RESOLUTION) {
                pch = strtok (NULL, "/");
                char *deplacements = (char*)calloc(strlen(pch)+1, sizeof(char));
                strncpy(deplacements, pch, strlen(pch));
                fprintf(stderr, "(Server:serveur.c:handle_request) : Solution proposée par le joueur actif \"%s\" : %s\n", username, deplacements);

                solutionActive(username, deplacements, task->socket);

                // isValideSolution() renvoie le nombre de deplacements necessaire pour la solution
                enchere_t * enchere = getEnchere(&enchere_mutex); //Get la meilleure enchère
                //fprintf(stderr, "solution courante : %d\n", enchere->nbCoups);
                if(isValideSolution(deplacements) == enchere->nbCoups) {
                    // Solution acceptée
                    send_bonneSolution();
                    etat_reso = 1;
                    puts("Solution correcte ! \n");
                }
                else {
                    if(encheres!=NULL) { //Il reste un joueur qui peut proposer une solution
                        send_mauvaiseSolution(encheres->name);
                    }
                    etat_reso = 2;
                    puts("Solution incorrecte ! \n");
                }
                free(enchere);
            }
            else {
                char *msg = (char*)calloc(50, sizeof(char));
                sprintf(msg, "La phase n'a pas été mise à jour...\n");
                fprintf(stderr, "(Server:serveur.c:handle_request) : %s", msg);
                exit(1);
            }
        }
        
        else if (strcmp(pch,"MESSAGE")==0) //C -> S : MESSAGE/user/message
        {

            pch = strtok(NULL, "/");
            char *user = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(user, pch, strlen(pch));

            pch = strtok(NULL, "/");
            char *message = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(message, pch, strlen(pch));

            //fprintf(stderr, "(Server:serveur.c:handle_request) : Message envoyé par %s : %s\n", user, message);

            envoyerMessageAuxAutres(user, message, task->socket);
        }

        else {
            fprintf(stderr, "(Server:serveur.c:handle_request) : ERROR : received bad protocol : %s.\n", task->command);
        }
    }
}


/******************************************
*                                         *
*  Surveille lorsqu'une tâche est         *
*  disponible, et distribue les tâches    *
*  parmis les threads disponibles.        *
*                                         *
*******************************************/
// serveur.c
void * handle_tasks_loop(void* data) {

    //puts("(Server:serveur.c:handle_tasks_loop) : handle_task_loop began");
    task_t * taskWeDo;
    int thread_id = *((int*)data);
    printf("(Server:serveur.c:handle_task_loop) : Thread %d created and ready\n", thread_id);
    /* lock the mutex, to access the tasks list exclusively. */
    if(pthread_mutex_lock(&task_mutex) != 0) perror("(Server:serveur.c:handle_tasks_loop) : error mutex\n");

    while (1) {
        if (nbTasks > 0) { /* a request is pending */
            taskWeDo = getTask(&task_mutex);
            //printf("(handle_tasks_loop) Thread %d got task %s.\n", thread_id, taskWeDo->command);
            if (taskWeDo) { /* got a request - handle it and free it */
                //printf("(Server:serveur.c:handle_tasks_loop) : Thread %d handles task %s.\n", thread_id, taskWeDo->command);
                handle_request(taskWeDo, thread_id);
                free(taskWeDo);
            }
        }
        else {
            //printf("(Server:serveur.c:handle_tasks_loop) : Thread %d is waiting some task.\n", thread_id);
            if(pthread_cond_wait(&cond_got_task, &task_mutex) != 0) perror("(Server:serveur.c:handle_tasks_loop) : err condition wait \n");
            if(isShutingDown==1) {
                break;
            }
        }
    }
    //Unreachable code bellow
    if(pthread_mutex_unlock(&task_mutex) != 0) perror("(Server:serveur.c:handle_tasks_loop) : error mutex\n");
    //puts("(Server:serveur.c:handle_tasks_loop) :  ended\n");
    return NULL;
}


/***************
*     MAIN     *
****************/

// serveur.c

int main(int argc, char* argv[]) {
    
    //INITIALIZE SERVER
    //To catch Ctrl+C (SIGINT)
    signal(SIGINT, shutdown_server);

    printf("(Server:serveur.c:main) : Initialize server...\n");
    printf("(Server:serveur.c:main) : Initialize map seed...\n");
    //Seed rand
    //srand(time(NULL));
    srand(1);
    printf("(Server:serveur.c:main) : Initialize threads...\n");
    int        i;                               /* loop counter          */
    int        thr_id[NB_MAX_THREADS];          /* thread IDs            */
    
    for (i=0; i<NB_MAX_THREADS; i++) {
        thr_id[i] = i;
        pthread_create(&p_threads[i], NULL, handle_tasks_loop, (void*)&thr_id[i]);
    }
    
    
    int port;
    
    
    if(argc>2) {
        port = atoi(argv[1]);
        readGridFromFile(argv[2]);
    }else {
        puts("please use : ./serveur n_port map_file.txt.\n");
        exit(1);
    }
    printf("(Server:serveur.c:main) : Initialize server socket on %d...\n", port);

    int socket_server;
    int socket_client;
    struct sockaddr_in server_address;
    struct sockaddr_in client_address = { 0 };
    socklen_t client_size;
    socket_server = socket(AF_INET, SOCK_STREAM, 0);
   
    if (socket_server < 0) {
        perror("(Server:serveur.c:main) : ERROR opening server socket\n");
        exit(1);
    }

    bzero((char *) &server_address, sizeof(server_address));
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(port);
    if (bind(socket_server, (struct sockaddr *) &server_address, sizeof(server_address)) < 0) {
       perror("(Server:serveur.c:main) : ERROR on binding\n");
       exit(1);
    }

    printf("(Server:serveur.c:main) : Initialize game loop with %d rounds each...", nbTours);
    pthread_create(&ptimer, NULL, session_loop, (void*)&nbTours);


    printf("(Server:serveur.c:main) : Start listenning with %d simultaneously clients max\n",NB_MAX_CLIENTS); 


    listen(socket_server,NB_MAX_CLIENTS);
    fd_set readfds, testfds;
    FD_ZERO(&readfds);
    FD_SET(socket_server, &readfds);
    FD_SET(STDIN_FILENO, &readfds); //add stdin
    int select_result;
    while(1) {
        char buffer[256];
        bzero(buffer,256);
        int socket;
        int n;
        testfds = readfds;
        printf("(Server:serveur.c:main) : server waiting\n");

        while( (select_result = select(FD_SETSIZE, &testfds, (fd_set *)0, (fd_set *)0, (struct timeval *) 0)) < 1) {
            fprintf(stderr, "(Server:serveur.c:main) : error select : Fenêtre fermee brutalement cote client ?\nselect val : %d (%d)\n", select_result, errno);
            if(pthread_mutex_lock(&client_mutex) != 0) perror("(Server:serveur.c:main) : error mutex");
            printf("(Server:serveur.c:main) : Etat de la liste des clients : \n");
            if(clients==NULL) {
                printf("(Server:serveur.c:main) : Aucun client. Erreur dans le select non geree...\n");
                exit(1);
            } else {
                int i = 1;
                client_t * client = clients;
                while(client != NULL) {
                    //printf("client %d : [name:%s ; socket:%d]\n", i, client->name, client->socket);
                    if(fcntl(client->socket, F_GETFL) == -1 && errno == EBADF) {
                        printf("(Server:serveur.c:main) : error on socket %d\n", client->socket); //The client with the corrupted file descriptor socket.
                        printClientsState(&client_mutex);
                        client->isConnected = 1;
                        nbClientsConnecte--;
                        //We must kick it from the fd_set structure (named readfds ? or testfds ? both ?)
                        FD_CLR(client->socket, &readfds);
                        FD_CLR(client->socket, &testfds);
                        FD_SET(STDIN_FILENO, &readfds); //On rajoute l'entrée clavier quand même !
                        printClientsState(&client_mutex);
                    }
                    client = client->next;
                    i++;
                }
                continue;
            }
            if(pthread_mutex_unlock(&client_mutex) != 0) perror("(Server:serveur.c:main) : error mutex");
        }
        
        for(socket = 0; socket < FD_SETSIZE; socket++) {
            if(FD_ISSET(socket,&testfds)) { //Si une activité est détecté sur un socket
                if(socket == socket_server)
                 { //Activité socket serveur : quelqu'un essaie de se connecter.
                    client_size = sizeof(client_address);
                    socket_client = accept(socket_server, (struct sockaddr *)&client_address, &client_size);
                    FD_SET(socket_client, &readfds); //Add socket file descriptor to the set
                    printf("(Server:serveur.c:main) : New socket connection on %d\n", socket_client);
                }
                else if(FD_ISSET(STDIN_FILENO, &readfds))
                { //Activité stdin : l'administrateur parle !
                    fgets(buffer, 255, stdin);
                    printf("L'admin dit : %s\n", buffer);
                    if(strncmp(buffer, "exit",4)==0) shutdown_server(0);
                }
                else
                { //Activité sur un socket client : un client nous parle !
                    n = read(socket,buffer,255);
                    if(n == 0) {
                        printf("(Server:serveur.c:main) : Main received empty message from %d.\n", socket);
                        FD_CLR(socket, &readfds);
                        FD_CLR(socket, &testfds);
                        FD_SET(STDIN_FILENO, &readfds); //Onrajoute l'entrée clavier quand même !
                        if(pthread_mutex_lock(&client_mutex) != 0) perror("(Server:serveur.c:main) : error mutex");
                        client_t *client = clients;
                        while(client != NULL){
                            if(client->socket == socket) {
                                client->isConnected = 1;
                                nbClientsConnecte--;
                                break;
                            }
                            client = client->next;
                        }
                        if(client==NULL) printf("(Server:serveur.c:main) : error : client null\n");
                        if(pthread_mutex_unlock(&client_mutex) != 0) perror("(Server:serveur.c:main) : error mutex");
                        sprintf(buffer, "SORT/%s/", client->name);
                        addTask(socket, buffer, &task_mutex, &cond_got_task);

                        break;
                    }
                    printf("(Server:serveur.c:main) : Server received %d bytes from %d.\n", n, socket);
                    printf("(Server:serveur.c:main) : Server received %s from %d.\n", buffer, socket);
                    addTask(socket, buffer, &task_mutex, &cond_got_task);
                }
            } else {

            }
        }
    }

    
    return 0;
}






void * session_loop(void* nbToursSession) {
    int cptTours = 0;
    int i;
    int timer;
    while(1) {
        if(pthread_mutex_lock(&client_mutex) < 0) {
            perror("(Server:client.c:addClient) : on addClient, cannot lock the first p_mutex\n");
        }

        //Si pas assez de client, on attend le feu vert, sinon on continue
        while(nbClientsConnecte<2) {
            if(pthread_cond_wait(&cond_at_least_2_players, &client_mutex) != 0) {
                perror("(Server:serveur.c:handle_tasks_loop) : err condition wait \n");
            }
        }

        if(pthread_mutex_unlock(&client_mutex) < 0) {
            perror("(Server:client.c:addClient) : on addClient, cannot unlock the p_mutex when an already connected client asks for a new connection\n");
        }

        if(isShutingDown==1) break;

        cptTours = 0;
        i=0;
        timer=0;
        
        while(nbClientsConnecte>=2 && cptTours<*((int*)nbToursSession)) {
            if(isShutingDown==1) return NULL;
            rmEncheres(&enchere_mutex); //On vide les enchères du tour précédent.
            etat_reso=0; //Personne n'a encore trouvé de solution

            i=0;
            timer=10;
            while(i < timer) {
                if(isShutingDown==1) return NULL;
                sleep(1);
                i++;
                printf("Waiting players... : %d sec.\n", i);
            }

            if( phase==REFLEXION && nbClientsConnecte>=2) {
                printf("DEBUT REFLEXION\n");
                //S -> C : TOUR/enigme/
                setEnigma();
                setBilanCurrentSession();
                sendEnigmaBilan(enigma, bilan);
                i=0;
                timer=5*60;
                while(i < timer && phase==REFLEXION && nbClientsConnecte>=2) {
                    if(isShutingDown==1) return NULL;
                    sleep(1);
                    i++;
                    printf("REFLEXION : %d sec.\n", i);
                }
                if(i==timer) { //Délai écoulé
                    printf("REFLEXION : DELAI ECOULE\n");
                    phase=ENCHERE;
                    char *msg = "FINREFLEXION/\n";
                    sendMessageAll(msg, &client_mutex);
                } else { //Quelqu'un a proposé une solution
                    //DO NOTHING : Géré dans Handle_request
                    printf("REFLEXION : SOLTUION PROPOSEE \n");
                }
            }
            if( phase==ENCHERE && nbClientsConnecte>=2) {
                printf("DEBUT ENCHERE\n");
                i=0;
                timer=30;
                while(i < timer && nbClientsConnecte>=2) {
                    if(isShutingDown==1) return NULL;
                    if(encheres==NULL) { //Personne n'a proposée de solution lors de la réfexion
                        break;
                    }
                    sleep(1);
                    i++;
                    printf("ENCHERE : %d sec.\n", i);
                }
                phase=RESOLUTION;
            }
            if( phase==RESOLUTION && nbClientsConnecte>=2) {
                printf("DEBUT RESOLUTION\n");
                i=0;
                timer=60;
                while(i < timer && nbClientsConnecte>=2) { //Et tant qu'unjoueur a une solution a proposer
                    if(isShutingDown==1) return NULL;
                    if(etat_reso==1) { //Solution trouvée
                         //Base
                        break; //Nouveau tour, énigme !
                    } else if(etat_reso==2 && encheres!=NULL) { //Solution erronée, relance du compte à rebours si il reste quelqu'un
                        i = 0;
                        etat_reso=0;
                    }

                    if(encheres == NULL) { //Plus personne n'a d'enchère à proposer
                        send_finReso();
                        break;
                    }
                    sleep(1);
                    i++;
                    if(i==timer) { //l'utilisateur a mis trop de temps à répondre !
                        free(getEnchere(&enchere_mutex)); //Enlève l'enchère du joueur.
                        if(encheres!=NULL) { //Si ilreste quelqu'un avec une solution possible
                            send_tropLong(encheres->name);
                        }
                        i = 0;
                    }
                    printf("RESOLUTION : %d sec.\n", i);
                }
                phase=REFLEXION;
            }
            cptTours++;
        }
    }
    return NULL;
}


void shutdown_server(int sig) {

    signal(sig, SIG_IGN);
    puts("server is shuting down...\n");
    isShutingDown=1; //To terminate threads
    pthread_cond_broadcast(&cond_got_task);
    pthread_cond_broadcast(&cond_at_least_2_players);
    exit(0);
}