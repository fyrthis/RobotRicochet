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
        printf ("(handle_request)Splitting string \"%s\" into tokens:\n",task->command);
        pch = strtok (task->command,"/");

        /*Dispatche*/
        if(pch==NULL)
        {
            printf("(handle_request)ERROR : received something NULL : %s.\n", task->command);
            perror("(handle_request)ERROR : close socket.\n"); //Maybe need to flush client datastructure ?
            close(task->socket);
        }
        //C -> S : CONNEXION/user/
        else if(strcmp(pch,"CONNEXION")==0)
        {
            //ADD THE CLIENT
            printf("(handle_request) : found CONNEXION\n");
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            printf("(handle_request) :pch is %s\n",username);
            printf("(handle_request) : add new client %s\n", username);
            addClient(task->socket, username, &client_mutex);

            bienvenue(username, task->socket);
            connexion(username, task->socket);
            
            //S -> C : SESSION/plateau/
            sendGrid(gridStr, task->socket);

            //S -> C : TOUR/enigme/       bilan to do
            setEnigma();
            setBilanCurrentSession();
            sendEnigmaBilan(enigma, bilan, task->socket);
        }
        //C -> S : SORT/user/
        else if(strcmp(pch,"SORT")==0)
        {
            //ICI SE FAIT LA SUPPRESSION D'UN CLIENT
            pch = strtok (NULL, "/");
            username = (char*)malloc(strlen(pch));
            strncpy(username, pch, strlen(pch));
            printf(" handle Task SORT\n");

            if(clients == NULL) {
                printf("(HandleRequest) Aucun client. Should never happened\n");
            }

            else {
                if(pthread_mutex_lock(&client_mutex) != 0) perror("error mutex");
                client_t *client = clients;
                while(client != NULL){
                    if(strcmp(client->name, username) == 0) {
                        client->isConnected = 1;
                        break;
                    }
                    client = client->next;
                }
                if(pthread_mutex_lock(&client_mutex) != 0) perror("error mutex");
            }
            if(pthread_mutex_lock(&client_mutex) != 0) perror("error mutex");
            nbClientsConnecte--;
            if(pthread_mutex_lock(&client_mutex) != 0) perror("error mutex");
            

            printf("(handle_request) :pch is %s\n",username);
            printf("(handle_request) : add new client %s\n", username);
            

            deconnexion(username, task->socket);
        }
        //C -> S : SOLUTION/user/coups/    ( a gerer plus tard : //C -> S : SOLUTION/user/deplacements/  )
        else if(strcmp(pch,"SOLUTION")==0)
        {
            if(pthread_mutex_lock(&task_mutex) != 0) perror("error mutex");

            pch = strtok (NULL, "/");
            username = (char*)malloc(strlen(pch));
            strncpy(username, pch, strlen(pch));


            // si la phase est toujours a 0, c'est que le serveur a recu la premiere (et unique) solution
            if(phase == 0){
                phase = 1;
                activePlayer = (char*)malloc(strlen(pch));
                strncpy(activePlayer, username, strlen(username));
                pch = strtok(NULL, "/");
                currentSolution = atoi(pch);

                fprintf(stderr, "Solution trouvée par %s\n", activePlayer);
                tuAsTrouve(task->socket);
                ilATrouve(activePlayer, currentSolution, task->socket);
            }
            // sinon c'est qu'on a deja changé de phase donc le protocole d'envoi de solution a changé de
            // SOLUTION/user/coups en ENCHERE/user/coups
            else {
                char *msg = (char*)malloc(50*sizeof(char));
                sprintf(msg, "Trop tard: une solution a déjà été trouvée...\n");
                fprintf(stderr, "%s", msg);
                exit(1);
            }
            if(pthread_mutex_unlock(&task_mutex) != 0) perror("error mutex");
        }
        //C -> S : ENCHERE/user/coups/
        else if(strcmp(pch,"ENCHERE")==0)
        {
            pch = strtok (NULL, "/");
            username = (char*)malloc(strlen(pch));
            strncpy(username, pch, strlen(pch));
        }
        else {
            fprintf(stderr, "(handle_request)ERROR : received bad protocol : %s.\n", task->command);
        }
    }
    printf("FIN handle_request.\n");
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

    puts("handle_task_loop began");
    task_t * taskWeDo;
    int thread_id = *((int*)data);

    /* lock the mutex, to access the tasks list exclusively. */
    if(pthread_mutex_lock(&task_mutex) != 0) perror("error mutex");

    while (1) {
        if (nbTasks > 0) { /* a request is pending */
            taskWeDo = getTask(&task_mutex);
            //printf("(handle_tasks_loop) Thread %d got task %s.\n", thread_id, taskWeDo->command);
            if (taskWeDo) { /* got a request - handle it and free it */
                printf("(handle_tasks_loop) Thread %d handles task %s.\n", thread_id, taskWeDo->command);
                handle_request(taskWeDo, thread_id);
                free(taskWeDo);
            }
        }
        else {
            printf("(handle_tasks_loop) Thread %d is waiting some task.\n", thread_id);
            if(pthread_cond_wait(&cond_got_task, &task_mutex) != 0) perror("error mutex");
        }
    }
    //Unreachable code bellow
    if(pthread_mutex_lock(&task_mutex) != 0) perror("error mutex");
    puts("handle_task_loop ended");
}





/***************
*     MAIN     *
****************/

// serveur.c

int main(int argc, char* argv[]) {
    //INITIALIZE SERVER
    printf("(Main)Initialize server...\n");
    printf("(Main)Initialize threads...\n");
    int        i;                               /* loop counter          */
    int        thr_id[NB_MAX_THREADS];          /* thread IDs            */
    pthread_t  p_threads[NB_MAX_THREADS];       /* thread's structures   */
    for (i=0; i<NB_MAX_THREADS; i++) {
        thr_id[i] = i;
        pthread_create(&p_threads[i], NULL, handle_tasks_loop, (void*)&thr_id[i]);
        printf("(Main)Thread %d created and ready\n", i);
    }
    printf("(Main)Initialize server socket...\n");
    int port = 2016;
    int socket_server;
    int socket_client;
    struct sockaddr_in server_address;
    struct sockaddr_in client_address = { 0 };
    socklen_t client_size;// = sizeof(client_address);
    if(argc>1) {
        port = atoi(argv[1]);
    }
    if(argc>2) {
        readGridFromFile(argv[2]);
    } else {
        readGridFromFile("./res/BasicGrid.txt");
    }
    
    printf("\nsetting port : %d\n", port);
    socket_server = socket(AF_INET, SOCK_STREAM, 0);
   
    if (socket_server < 0) {
        perror("(Main)ERROR opening server socket\n");
        exit(1);
    } else {
        puts("(Main)The server socket is now open\n");
    }


    /* A enlver ultérieurement, sert à éviter le bind already in use */
    if (setsockopt(socket_server, SOL_SOCKET, SO_REUSEADDR, &(int){ 1 }, sizeof(int)) < 0)
        perror("setsockopt(SO_REUSEADDR) failed");
    /* ************************************************************** */
    bzero((char *) &server_address, sizeof(server_address));
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(port);
    if (bind(socket_server, (struct sockaddr *) &server_address, sizeof(server_address)) < 0) {
       perror("ERROR on binding\n");
       exit(1);
    }


    printf("(Main)Start listenning with %d simultaneously clients max\n",NB_MAX_CLIENTS);
    
    

    listen(socket_server,NB_MAX_CLIENTS);
    fd_set readfds, testfds;
    FD_ZERO(&readfds);
    FD_SET(socket_server, &readfds);
    int select_result;
    while(1) {
        char buffer[256];
        bzero(buffer,256);
        int socket;
        int n;
        testfds = readfds;
        printf("(Main)server waiting\n");

        while( (select_result = select(FD_SETSIZE, &testfds, (fd_set *)0, (fd_set *)0, (struct timeval *) 0)) < 1) {
            perror("");
            fprintf(stderr, "error select : Fenêtre fermee brutalement cote client ?\nselect val : %d (%d)\n", select_result, errno);
            if(pthread_mutex_lock(&client_mutex) != 0) perror("error mutex");
            printf("Etat de la liste des clients : \n");
            if(clients==NULL) {
                printf("Aucun client. Erreur dans le select non geree...\n");
                exit(1);
            } else {
                int i = 1;
                client_t * client = clients;
                while(client != NULL) {
                    //printf("client %d : [name:%s ; socket:%d]\n", i, client->name, client->socket);
                    if(fcntl(client->socket, F_GETFL) == -1 && errno == EBADF) {
                        printf("error on socket %d\n", client->socket); //The client with the corrupted file descriptor socket.
                        printClientsState(&client_mutex);
                        client->isConnected = 1;
                        nbClientsConnecte--;
                        //We must kick it from the fd_set structure (named readfds ? or testfds ? both ?)
                        FD_CLR(client->socket, &readfds);
                        FD_CLR(client->socket, &testfds);
                        printClientsState(&client_mutex);
                    }
                    client = client->next;
                    i++;
                }
                continue;
            }
            if(pthread_mutex_unlock(&client_mutex) != 0) perror("error mutex");
        }
        
        for(socket = 0; socket < FD_SETSIZE; socket++) {
            if(FD_ISSET(socket,&testfds)) { //Si une activité est détecté sur un socket
                if(socket == socket_server) {
                    client_size = sizeof(client_address);
                    socket_client = accept(socket_server, (struct sockaddr *)&client_address, &client_size);
                    FD_SET(socket_client, &readfds); //Add socket file descriptor to the set
                    printf("(Main)New socket connection on %d\n", socket_client);
                }
                else {
                    n = read(socket,buffer,255);
                    if(n == 0) {
                        printf("Main received empty message from %d.\n", socket);
                        printf("debug : 0.0\n");
                        FD_CLR(socket, &readfds);
                        printf("debug : 0.1\n");
                        FD_CLR(socket, &testfds);
                        //TODO : chercher le client responsable responsable, et isConnected = false;
                        printf("debug : 1\n");
                        if(pthread_mutex_lock(&client_mutex) != 0) perror("error mutex");
                        client_t *client = clients;
                        printf("debug : 2\n");
                        while(client != NULL){
                            printf("debug : 3\n");
                            if(client->socket == socket) {
                                printf("debug : 4\n");
                                client->isConnected = 1;
                                break;
                            }
                            printf("debug : 5\n");
                            client = client->next;
                        }
                        printf("debug : 6");
                        if(client==NULL) printf("error : client null\n");
                        if(pthread_mutex_unlock(&client_mutex) != 0) perror("error mutex");
                        printf("debug : 6\n");
                        sprintf(buffer, "SORT/%s/", client->name);
                        printf("debug : 7\n");
                        addTask(socket, buffer, &task_mutex, &cond_got_task);
                        printf("debug : 8\n");
                        break;
                    }
                    printf("(Main)Server received %d bytes from %d.\n", n, socket);
                    printf("(Main)Server received %s from %d.\n", buffer, socket);
                    addTask(socket, buffer, &task_mutex, &cond_got_task);
                }
            } else {
                //puts("no data received since 5 seconds.\n");
            }
        }
    }

    
    return 0;
}

/*********************************************
*                                            *
*  Crée une nouvelle enigme en initialisant  *
*  les coordoonées du robot aléatoirement    *
*                                            *
*********************************************/
//serveur.c
int setEnigma(){
    // nbRobots*nbCoordonnées*tailleCoordonnée + nbLettres + nbVirgule + parenthèses + lettreCible
    enigma = malloc(sizeof(char)*5*2*2 + 10 + 10 + 2 + 1);
    
    if(firstLaunch == 0){
        // Generation aleatoire des positions des robots
        srand(time(NULL));
        
        // Rouge
        x_r = rand() % size_x;
        y_r = rand() % size_y;
        // Bleu
        x_b = rand() % size_x;
        y_b = rand() % size_y;
        // Jaune
        x_j = rand() % size_x;
        y_j = rand() % size_y;
        // Vert
        x_v = rand() % size_x;
        y_v = rand() % size_y;

        // Cible
        x_cible = rand() % size_x;
        y_cible = rand() % size_y;

        // LettreCible
        int cible = rand() % 4;
        switch(cible){
            case 0:
                lettreCible = 'r';
                break;
            case 1:
                lettreCible = 'b';
                break;
            case 2:
                lettreCible = 'j';
                break;
            case 3:
                lettreCible = 'v';
                break;
            default:;
        }
        firstLaunch = -1;
    }

    sprintf(enigma, "(%dr,%dr,%db,%db,%dj,%dj,%dv,%dv,%dc,%dc,%c)", x_r, y_r, x_b, y_b, x_j, y_j, x_v, y_v, x_cible, y_cible, lettreCible);
    return 0;
}

/*********************************************
*                                            *
*    Set le bilan de la session courante     *
*                                            *
*********************************************/
//serveur.c
int setBilanCurrentSession(){
    fprintf(stderr, "Setting the bilan of the current session:\n");

    if(clients == NULL) {
        fprintf(stderr, "ERREUR: la liste des clients est nulle\n");
        exit(EXIT_FAILURE);
    }

    client_t* first_client = clients;

    int nbTourLength = 1;
    if(nbTour >= 10)
        nbTourLength = floor(log10(abs(nbTour))) + 1;
        
    int sizeAll = nbTourLength;
    while(clients != NULL){
        int scoreLength = 1;
        if(clients->score >= 10)
            scoreLength = floor(log10(abs(clients->score))) + 1;
        sizeAll += (strlen(clients->name) + scoreLength + 3);
        clients = clients->next;
    }

    clients = first_client;
    bilan = (char *) malloc(sizeAll);
    sprintf(bilan, "%d", nbTour);    
    fprintf(stderr, "%s", bilan);
    fprintf(stderr, "SizeAll : %d\n", sizeAll);
    fprintf(stderr, "toto1\n");
    while(clients != NULL){
        int scoreLength = 1;
        if(clients->score >= 10)
            scoreLength = floor(log10(abs(clients->score))) + 1;
        fprintf(stderr, "scoreLength : %d\tclientNameLength : %zu\n", scoreLength, strlen(clients->name));
        fprintf(stderr, "name : %s\n", clients->name);
        char *user = (char *)calloc(sizeof(char), strlen(clients->name)+scoreLength+3);
        sprintf(user, "(%s,%d)", clients->name, clients->score);
        fprintf(stderr, "userBuffer : %s\n", user);
        
        sprintf(bilan,"%s%s", bilan, user);
        clients = clients->next;
    }

    clients = first_client;

    fprintf(stderr, "Bilan : %s\n", bilan);
    fprintf(stderr, "Bilan current session set!\n");

    return 0;
}