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

pthread_cond_t  cond_got_task   = PTHREAD_COND_INITIALIZER;

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
        printf ("(Server:serveur.c:handle_request) : Splitting string \"%s\" into tokens:\n",task->command);
        pch = strtok (task->command,"/");

        /*Dispatche*/
        if(pch==NULL)
        {
            printf("(Server:serveur.c:handle_request) : ERROR : received something NULL : %s.\n", task->command);
            perror("(Server:serveur.c:handle_request) : ERROR : close socket.\n"); //Maybe need to flush client datastructure ?
            close(task->socket);
        }
        //C -> S : CONNEXION/user/
        else if(strcmp(pch,"CONNEXION")==0)
        {
            //ADD THE CLIENT
            printf("(Server:serveur.c:handle_request) : found CONNEXION\n");
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            printf("(Server:serveur.c:handle_request) : pch is %s\n",username);
            printf("(Server:serveur.c:handle_request) : add new client %s\n", username);
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
            username = (char*)calloc(strlen(pch), sizeof(char));
            strncpy(username, pch, strlen(pch));
            printf("(Server:serveur.c:handle_request) : handle Task SORT\n");

            if(clients == NULL) {
                printf("(Server:serveur.c:handle_request) : Aucun client. Should never happened\n");
            }

            else {
                if(pthread_mutex_lock(&client_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
                client_t *client = clients;
                while(client != NULL){
                    if(strcmp(client->name, username) == 0) {
                        client->isConnected = 1;
                        break;
                    }
                    client = client->next;
                }
                if(pthread_mutex_unlock(&client_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
            }
            if(pthread_mutex_lock(&client_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex on locking variable nbClientsConnecte ");
            nbClientsConnecte--;
            if(pthread_mutex_unlock(&client_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex on unlocking variable nbClientsConnecte ");
            

            printf("(Server:serveur.c:handle_request) : pch is %s\n",username);
            printf("(Server:serveur.c:handle_request) : add new client %s\n", username);
            

            deconnexion(username, task->socket);
        }
        //C -> S : SOLUTION/user/coups/    ( a gerer plus tard : //C -> S : SOLUTION/user/deplacements/  )
        else if(strcmp(pch,"SOLUTION")==0)
        {
            if(pthread_mutex_lock(&task_mutex) != 0) perror("error mutex");

            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));


            // si la phase est toujours a 0, c'est que le serveur a recu la premiere (et unique) solution
            if(phase == 0){
                phase = 1;

                // On va chercher dans la liste des clients le joueur qui correspond a celui qui a proposé
                // une solution pour le garder en mémoire en tant que joueur actif (activePlayer)
                char *activePlayerStr = (char*)calloc(strlen(pch)+1, sizeof(char));
                strncpy(activePlayerStr, username, strlen(username));
                
                if(pthread_mutex_lock(&client_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
                client_t *client = clients;
                while(client != NULL){
                    if(strcmp(client->name, activePlayerStr) == 0) {
                        // normalement activePlayer étant défini comme un pointeur vers un client déjà existant
                        // on a ni besoin de l'initialiser avec des malloc, ni besoin de remplir ses champs
                        activePlayer = client;
                        break;
                    }
                    client = client->next;
                }
                if(pthread_mutex_unlock(&client_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
                pch = strtok(NULL, "/");
                currentSolution = atoi(pch);
                activePlayer->score = currentSolution; 

                fprintf(stderr, "(Server:serveur.c:handle_request) : Solution trouvée par %s\n", activePlayer->name);
                tuAsTrouve(task->socket);
                ilATrouve(activePlayer->name, currentSolution, task->socket);
                // Il faut appeler finReflexion() seulement si personne n'a proposé de solution
                // pendant le laps de temps restant, qu'il faudra implementer dans timer
                // finReflexion();
            }
            // sinon c'est qu'on a deja changé de phase donc le protocole d'envoi de solution a changé :
            // au lieu de SOLUTION/user/coups on envoie ENCHERE/user/coups
            else {
                char *msg = (char*)calloc(50, sizeof(char));
                sprintf(msg, "(Server:serveur.c:handle_request) : Trop tard: une solution a déjà été trouvée...\n");
                fprintf(stderr, "%s", msg);
                exit(1);
            }
            if(pthread_mutex_unlock(&task_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
        }
        //C -> S : ENCHERE/user/coups/
        else if(strcmp(pch,"ENCHERE")==0)
        {
            if(pthread_mutex_lock(&task_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");

            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            
            // si la phase est a 1, c'est que le serveur a recu une echere
            if(phase == 1){
                phase = 2;

                fprintf(stderr, "(Server:serveur.c:handle_request) : Enchère reçues de la part de %s\n", username);
                
                // Il faut tester la validité de l'enchère envoyée par le client
                pch = strtok(NULL, "/");
                int betSolution = atoi(pch);

                if(activePlayer != NULL){
                    // si l'enchère propose une solution moins bonne que celle de l'activePlayer
                    // alors le serveur renvoie ECHEC/activePlayer
                    if(betSolution >= activePlayer->score) {
                        echec(activePlayer->name, task->socket);
                    }
                    // sinon, l'enchère est meilleure que la solution courante:
                    // il faut donc mettre a jour la solution courante, et enre
                    else {
                        validation(task->socket);
                    }
                }

            }
            // sinon c'est qu'on a deja changé de phase donc le protocole d'envoi de solution a changé :
            // au lieu de SOLUTION/user/coups on envoie ENCHERE/user/coups
            else {
                char *msg = (char*)calloc(50, sizeof(char));
                sprintf(msg, "la phase d'enchère est terminée...\n");
                fprintf(stderr, "(Server:serveur.c:handle_request) : %s", msg);
                exit(1);
            }

            if(pthread_mutex_unlock(&task_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
        }
        // erreur dans le protocole, à redéfinir mais correspond à l'envoi de la solution
        // proposée par le joueur actif lors de la phase de résolution
        else if(strcmp(pch,"ENVOISOLUTION")==0) {
            if(pthread_mutex_lock(&task_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");

            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));

            
            // si la phase est a 2 alors on est dans la phase de résolution
            if(phase == 2) {
                
                pch = strtok (NULL, "/");
                char *deplacements = (char*)calloc(strlen(pch)+1, sizeof(char));
                strncpy(deplacements, pch, strlen(pch));
                fprintf(stderr, "(Server:serveur.c:handle_request) : Solution proposée par le joueur actif \"%s\" : %s\n", username, deplacements);

                solutionActive(username, deplacements, task->socket);

                if(isValideSolution(deplacements) == 0) {
                    // Solution acceptée
                    bonneSolution(task->socket);
                }
                else {
                    // Il faut mettre àjour l'activePlayer courant en le remplaçant avec
                    // le joueur ayant le meilleur score après lui
                    mauvaiseSolution(username, task->socket);
                }
            }
            else {
                char *msg = (char*)calloc(50, sizeof(char));
                sprintf(msg, "La phase n'a pas été mise à jour...\n");
                fprintf(stderr, "(Server:serveur.c:handle_request) : %s", msg);
                exit(1);
            }

            if(pthread_mutex_unlock(&task_mutex) != 0) perror("(Server:serveur.c:handle_request) : error mutex");
        }
        else {
            fprintf(stderr, "(Server:serveur.c:handle_request) : ERROR : received bad protocol : %s.\n", task->command);
        }
    }
    printf("(Server:serveur.c:handle_request) : FIN handle_request.\n");
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

    puts("(Server:serveur.c:handle_tasks_loop) : handle_task_loop began");
    task_t * taskWeDo;
    int thread_id = *((int*)data);

    /* lock the mutex, to access the tasks list exclusively. */
    if(pthread_mutex_lock(&task_mutex) != 0) perror("(Server:serveur.c:handle_tasks_loop) : error mutex");

    while (1) {
        if (nbTasks > 0) { /* a request is pending */
            taskWeDo = getTask(&task_mutex);
            //printf("(handle_tasks_loop) Thread %d got task %s.\n", thread_id, taskWeDo->command);
            if (taskWeDo) { /* got a request - handle it and free it */
                printf("(Server:serveur.c:handle_tasks_loop) : Thread %d handles task %s.\n", thread_id, taskWeDo->command);
                handle_request(taskWeDo, thread_id);
                free(taskWeDo);
            }
        }
        else {
            printf("(Server:serveur.c:handle_tasks_loop) : Thread %d is waiting some task.\n", thread_id);
            if(pthread_cond_wait(&cond_got_task, &task_mutex) != 0) perror("(Server:serveur.c:handle_tasks_loop) : err condition wait ");
        }
    }
    //Unreachable code bellow
    if(pthread_mutex_unlock(&task_mutex) != 0) perror("(Server:serveur.c:handle_tasks_loop) : error mutex");
    puts("(Server:serveur.c:handle_tasks_loop) :  ended");
}





/***************
*     MAIN     *
****************/

// serveur.c

int main(int argc, char* argv[]) {
    //INITIALIZE SERVER
    printf("(Server:serveur.c:main) : Initialize server...\n");
    printf("(Server:serveur.c:main) : Initialize threads...\n");
    int        i;                               /* loop counter          */
    int        thr_id[NB_MAX_THREADS];          /* thread IDs            */
    pthread_t  p_threads[NB_MAX_THREADS];       /* thread's structures   */
    for (i=0; i<NB_MAX_THREADS; i++) {
        thr_id[i] = i;
        pthread_create(&p_threads[i], NULL, handle_tasks_loop, (void*)&thr_id[i]);
        printf("(Server:serveur.c:main) : Thread %d created and ready\n", i);
    }
    printf("(Server:serveur.c:main) : Initialize server socket...\n");
    int port = 2061;
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
    
    printf("(Server:serveur.c:main) : setting port : %d\n", port);
    socket_server = socket(AF_INET, SOCK_STREAM, 0);
   
    if (socket_server < 0) {
        perror("(Server:serveur.c:main) : ERROR opening server socket\n");
        exit(1);
    } else {
        puts("(Server:serveur.c:main) : The server socket is now open\n");
    }


    /* A enlver ultérieurement, sert à éviter le bind already in use */
    if (setsockopt(socket_server, SOL_SOCKET, SO_REUSEADDR, &(int){ 1 }, sizeof(int)) < 0)
        perror("(Server:serveur.c:main) : setsockopt(SO_REUSEADDR) failed");
    /* ************************************************************** */
    bzero((char *) &server_address, sizeof(server_address));
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(port);
    if (bind(socket_server, (struct sockaddr *) &server_address, sizeof(server_address)) < 0) {
       perror("(Server:serveur.c:main) : ERROR on binding\n");
       exit(1);
    }


    printf("(Server:serveur.c:main) : Start listenning with %d simultaneously clients max\n",NB_MAX_CLIENTS);
    
    

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
                if(socket == socket_server) {
                    client_size = sizeof(client_address);
                    socket_client = accept(socket_server, (struct sockaddr *)&client_address, &client_size);
                    FD_SET(socket_client, &readfds); //Add socket file descriptor to the set
                    printf("(Server:serveur.c:main) : New socket connection on %d\n", socket_client);
                }
                else {
                    n = read(socket,buffer,255);
                    if(n == 0) {
                        printf("(Server:serveur.c:main) : Main received empty message from %d.\n", socket);
                        FD_CLR(socket, &readfds);
                        FD_CLR(socket, &testfds);
                        if(pthread_mutex_lock(&client_mutex) != 0) perror("(Server:serveur.c:main) : error mutex");
                        client_t *client = clients;
                        while(client != NULL){
                            if(client->socket == socket) {
                                client->isConnected = 1;
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
    enigma = calloc(5*2*2 + 10 + 10 + 2 + 1, sizeof(char));
    
    if(firstLaunch == 0){
        // Generation aleatoire des positions des robots
        srand(time(NULL));
        
        do { 
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
        } while((x_r == x_b && y_r == y_b) || (x_r == x_j && y_r == y_j) || (x_r == x_v && y_r == y_v)
            || (x_b == x_j && y_b == y_j) || (x_b == x_v && y_b == y_v) || (x_j == x_v && y_j == y_v));

        // Cible

        x_cible = rand() % size_x;
        y_cible = rand() % size_y;
        while(grid[x_cible][y_cible] == 0
            || grid[x_cible][y_cible] == 1
            || grid[x_cible][y_cible] == 2
            || grid[x_cible][y_cible] == 4
            || grid[x_cible][y_cible] == 8){
            x_cible = rand() % size_x;
            y_cible = rand() % size_y;
        }

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
    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : Setting the bilan of the current session:\n");

    if(clients == NULL) {
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : ERREUR: la liste des clients est nulle\n");
        exit(EXIT_FAILURE);
    }

    client_t* first_client = clients;

    int nbTourLength = getIntLength(nbTour);
    int sizeAll = nbTourLength;
    while(clients != NULL){
        int scoreLength = getIntLength(clients->score);
        sizeAll += (strlen(clients->name) + scoreLength + 3);
        clients = clients->next;
    }

    clients = first_client;
    bilan = (char *) malloc(sizeAll+1);
    sprintf(bilan, "%d", nbTour);    
    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : %s", bilan);
    fprintf(stderr, "SizeAll : %d\n", sizeAll);
    fprintf(stderr, "toto1\n");
    while(clients != NULL){
        int scoreLength = getIntLength(clients->score);
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : scoreLength : %d\tclientNameLength : %zu\n", scoreLength, strlen(clients->name));
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : name : %s\n", clients->name);
        char *user = (char *)calloc(sizeof(char), strlen(clients->name)+scoreLength+4);
        sprintf(user, "(%s,%d)", clients->name, clients->score);
        fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : userBuffer : %s\n", user);
        
        sprintf(bilan,"%s%s", bilan, user);
        clients = clients->next;
    }

    clients = first_client;

    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : Bilan : %s\n", bilan);
    fprintf(stderr, "(Server:serveur.c:setBilanCurrentSession) : Bilan current session set!\n");

    return 0;
}