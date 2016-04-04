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
int isShutingDown=0;

pthread_t ptimer;
pthread_t  p_threads[NB_MAX_THREADS];       /* thread's structures   */

int ticTac;
int timer;



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
        char * pch = (char*)calloc(strlen(task->command)+1, sizeof(char));
        char* username = NULL;
        //printf ("(Server:serveur.c:handle_request) : Splitting string \"%s\" into tokens:\n",task->command);
        pch = strtok (task->command,"/");

        if(pch==NULL) //Should never happen ? Maybe too rude to close the socket, so we return.
        {
            printf("(Server:serveur.c:handle_request) : ERROR : received something NULL : %s.\n", task->command);
            return;
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
            if(bilan != NULL)
                send_vainqueur();
            resetNbCoups();
        }


        else if(strcmp(pch,"SORT")==0) //C -> S : SORT/user/
        {
            //ICI SE FAIT LA SUPPRESSION D'UN CLIENT
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            //printf("(Server:serveur.c:handle_request) : handle Task SORT\n");

            if(clients == NULL) { puts("(Server:serveur.c:handle_request) : Aucun client. Should never happened\n"); return; }

            disconnectClient(username, &client_mutex);
            send_deconnexion(username, task->socket);
        }


        else if(strcmp(pch,"SOLUTION")==0) //C -> S : SOLUTION/user/...
        {
            //0. Récupération des paramètres
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            pch = strtok(NULL, "/");
            int solution = atoi(pch);
            //1 Vérification des paramètres (int est bien int, non vide, etc...)
            //TODO
            //2. Verification tricherie
            pthread_mutex_lock(&client_mutex);
            client_t * client = findClient(username);
            pthread_mutex_unlock(&client_mutex);
            if((client->socket != task->socket) //Un joueur essaie d'enchérir pour un autre (Usurpation du nom)
             ||(phase != REFLEXION))                                   //Mauvaise phase
            {
                puts("Quelqu'un a essayé de tricher !\n");
                return;
            }

            tuAsTrouve(task->socket);
            ilATrouve(username, solution, task->socket);
            addEnchere(task->socket, username, solution, &enchere_mutex);
            fprintf(stderr, "Solution trouvée par %s\n", username);
            phase = ENCHERE;
        }


         /***************
         *   ENCHERE    *
        ****************/
        else if(strcmp(pch,"ENCHERE")==0) //C -> S : ENCHERE/user/coups/
        {
            pthread_mutex_lock(&enchere_mutex);
            //0. Récupération des paramètres
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            pch = strtok(NULL, "/");
            int betSolution = atoi(pch);
            //1 Vérification des paramètres (int est bien int, non vide, etc...)
            //TODO
            //2. Verification tricherie
            pthread_mutex_lock(&client_mutex);
            client_t * client = findClient(username);
            pthread_mutex_unlock(&client_mutex);
            if((client->socket != task->socket) //Un joueur essaie d'enchérir pour un autre (Usurpation du nom)
             ||(phase != ENCHERE))                                   //Mauvaise phase
            {
                puts("Quelqu'un a essayé de tricher !\n");
                return;
            }
            //3. Verification validité de l'enchère (différente de celle des autres joueurs, inf. à celle qu'il a proposé auparavant)
            if(checkEnchere(client->socket, username, betSolution, &enchere_mutex)==0){
                fprintf(stderr, "Enchère reçue de la part de %s acceptee.\n", username);
                send_validation(task->socket);
                send_nouvelleEnchere(username, betSolution);
                //addEnchere(task->socket, username, betSolution, &enchere_mutex);
            }
            else {
                fprintf(stderr, "Enchère reçue de la part de %s refusee.\n", username);
                send_echec(username, task->socket);
            }
            pthread_mutex_unlock(&enchere_mutex);
        }
        
        /***************
        *ENVOISOLUTION *
        ****************/
        else if(strcmp(pch,"ENVOISOLUTION")==0) {
            //0. Récupération des paramètres
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            pch = strtok (NULL, "/");
            char *deplacements = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(deplacements, pch, strlen(pch));
            pch = strtok (NULL, "/");
            animationTime = (atoi(pch)/1000)+1;
            //1 Vérification des paramètres (int est bien int, non vide, etc...)
            //TODO
            //2. Verification tricherie
            pthread_mutex_lock(&client_mutex);
            client_t * client = findClient(username);
            pthread_mutex_unlock(&client_mutex);

            //pthread_mutex_lock(&enchere_mutex);
            if((strcmp(username, encheres->name) != 0)                  //Pas à ce joueur de jouer, ou nom n'existe pas
             ||(client->socket != task->socket)           //Usurpation du nom 
             ||(phase != RESOLUTION))                                   //Mauvaise phase
            {
                puts("Quelqu'un a essayé de tricher !\n");
                return;
            }
            //2. Présentation de la solution aux clients
            solutionActive(username, deplacements);
            //3. Verification validité de la solution
            //pthread_mutex_lock(&etat_reso_mutex);
            printf("Le joueur %s propose la solution %s.", username, deplacements);
            enchere_t * enchere = getEnchere(&enchere_mutex);
            if(isValideSolution(deplacements, enchere->nbCoups) == 0) { //correcte
                send_bonneSolution();
                pthread_mutex_lock(&ticTac_mutex);
                ticTac=timer+1;
                pthread_mutex_unlock(&ticTac_mutex);
                puts("La solution est acceptée !\n");
                updateBilan(enchere);
                setBilanCurrentSession();
            } else if(encheres!=NULL) { //Erronée et quelqu'un d'autre peut proposer une solution
                send_mauvaiseSolution(enchere->next->name);
                pthread_mutex_lock(&ticTac_mutex);
                ticTac = 0;
                pthread_mutex_unlock(&ticTac_mutex);
                puts("La solution est refusée !\n");
            } else {
                send_finReso();
                pthread_mutex_lock(&ticTac_mutex);
                ticTac=timer+1;
                pthread_mutex_unlock(&ticTac_mutex);
                puts("La solution est refusée ! Plus aucune enchère.\n");
            }
            //pthread_mutex_unlock(&etat_reso_mutex);
            free(enchere);
            free(username);
            free(deplacements);
            //pthread_mutex_unlock(&enchere_mutex);
            
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
    pthread_mutex_lock(&task_mutex);

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
            pthread_cond_wait(&cond_got_task, &task_mutex);
            if(isShutingDown==1) {
                break;
            }
        }
    }
    //Unreachable code bellow
    pthread_mutex_unlock(&task_mutex);
    //puts("(Server:serveur.c:handle_tasks_loop) :  ended\n");
    return NULL;
}




void * session_loop(void* nbToursSession) {
    cptTours = 1;
    int cptSessions = 1;

    while(1) {
        /******************
        *  DEBUT SESSION  *
        ******************/
        if(isShutingDown==1) return NULL; //Serveur veut s'arrêter.
        /********************
        *  ATTENTE JOEUURS  *
        ********************/
        pthread_mutex_lock(&client_mutex);
        //Si pas assez de client, on attend le feu vert, sinon on continue
        while(nbClientsConnecte<2) {
            puts("We need at least two players in order to start a game.\n");
            pthread_cond_wait(&cond_at_least_2_players, &client_mutex);
        }
        pthread_mutex_unlock(&client_mutex);

        /***************************
        *  INITIALISATION SESSION  *
        ***************************/
        cptTours = 1;
        pthread_mutex_lock(&ticTac_mutex);
        ticTac=0;
        pthread_mutex_unlock(&ticTac_mutex);
        timer=0;
        while(nbClientsConnecte>=2 && cptTours<=*((int*)nbToursSession))
        {
            printf("Session %d : Tour %d/%d\n", cptSessions, cptTours, *((int*)nbToursSession));

            /********************
            *  ATTENTE JOUEURS  *
            ********************/
            pthread_mutex_lock(&ticTac_mutex);
            ticTac=0;
            pthread_mutex_unlock(&ticTac_mutex);
            timer=10+animationTime;
            while(ticTac < timer) {
                if(isShutingDown==1) return NULL; //Serveur veut s'arrêter.
                sleep(1);
                pthread_mutex_lock(&ticTac_mutex);
                ticTac++;
                pthread_mutex_unlock(&ticTac_mutex);
                printf("Waiting players... : %d sec.\n", ticTac);
            }

            /************************
            *  INITIALISATION TOUR  *
            ************************/
            rmEncheres(&enchere_mutex); //On vide les enchères du tour précédent.
            phase=REFLEXION;
            //S -> C : TOUR/enigme/
            setEnigma();
            resetNbCoups();
            setBilanCurrentSession();
            sendEnigmaBilan(enigma, bilan);



            /********************
            *  PHASE REFLEXION  *
            ********************/
            if(phase==REFLEXION && nbClientsConnecte>=2)
            {
                puts("DEBUT REFLEXION\n");
                pthread_mutex_lock(&ticTac_mutex);
                ticTac=0;
                pthread_mutex_unlock(&ticTac_mutex);
                timer=5*60; //5minutes
                while(ticTac < timer && phase==REFLEXION && nbClientsConnecte>=2) {
                    if(isShutingDown==1) return NULL; //Serveur veut s'arrêter.
                    sleep(1);
                    pthread_mutex_lock(&ticTac_mutex);
                    ticTac++;
                    pthread_mutex_unlock(&ticTac_mutex);
                    printf("REFLEXION : %d sec.\n", ticTac);
                }
                if(ticTac==timer) { //Délai écoulé
                    puts("REFLEXION : DELAI ECOULE\n");
                    send_finReflexion();
                }
                phase=ENCHERE;
                puts("FIN REFLEXION\n");
            }
            /******************
            *  PHASE ENCHERE  *
            ******************/
            if(phase==ENCHERE && nbClientsConnecte>=2)
            {
                puts("DEBUT ENCHERE\n");
                pthread_mutex_lock(&ticTac_mutex);
                ticTac=0;
                pthread_mutex_unlock(&ticTac_mutex);
                timer=30; //30 seconds
                while(ticTac < timer && nbClientsConnecte>=2) {
                    if(isShutingDown==1) return NULL; //Serveur veut s'arrêter.
                    sleep(1);
                    pthread_mutex_lock(&ticTac_mutex);
                    ticTac++;
                    pthread_mutex_unlock(&ticTac_mutex);
                    printf("ENCHERE : %d sec.\n", ticTac);
                }
                phase=RESOLUTION;
                pthread_mutex_lock(&enchere_mutex);
                send_finEnchere(encheres->name, encheres->nbCoups);
                pthread_mutex_unlock(&enchere_mutex);
                puts("FIN ENCHERE\n");
            }
            /*********************
            *  PHASE RESOLUTION  *
            *********************/
            if(phase==RESOLUTION && nbClientsConnecte>=2 && encheres != NULL)
            {
                printf("DEBUT RESOLUTION\n");
                pthread_mutex_lock(&ticTac_mutex);
                ticTac=0;
                pthread_mutex_unlock(&ticTac_mutex);
                timer=60;//1 minute
                while(ticTac < timer && nbClientsConnecte>=2) {
                    if(isShutingDown==1) return NULL; //Serveur veut s'arrêter.

                    pthread_mutex_lock(&ticTac_mutex);
                    ticTac++;
                    pthread_mutex_unlock(&ticTac_mutex);

                    
                    if(ticTac==timer) { //l'utilisateur a mis trop de temps à répondre !
                        pthread_mutex_lock(&enchere_mutex);
                        free(getEnchere(&enchere_mutex)); //Enlève l'enchère du joueur.
                        if(encheres!=NULL) { //Si ilreste quelqu'un avec une solution possible
                            send_tropLong(encheres->name);
                        }else {
                            send_finReso();
                            pthread_mutex_unlock(&enchere_mutex);
                            break;
                        }
                        pthread_mutex_unlock(&enchere_mutex);
                        pthread_mutex_lock(&ticTac_mutex);
                        ticTac = 0;
                        pthread_mutex_unlock(&ticTac_mutex);
                    }
                    
                    sleep(1);

                    printf("RESOLUTION : %d sec.\n", ticTac);
                }
            }
            /****************
            *  FIN DU TOUR  *
            ****************/
            cptTours++;
        }
        /**********************
        *  FIN D'UNE SESSION  *
        **********************/
        send_vainqueur();
        cptSessions++;
    }
    //Unreachable code bellow
    return NULL;
}


void shutdown_server(int sig) {

    signal(sig, SIG_IGN);
    puts("server is shuting down...\n");
    isShutingDown=1; //To terminate threads, we signal condition in order to unlock them.
    pthread_cond_broadcast(&cond_got_task);
    pthread_cond_broadcast(&cond_at_least_2_players);

    //free tasks
    task_t * task = getTask(&task_mutex);
    while(task!=NULL) {
        free(task);
        task = getTask(&task_mutex);
    }
    printTasksState(&task_mutex);
    //free encheres
    //free clients
    exit(0);
}



/******************************************************************************************
*                                                                                         *
*    MAIN            MAIN         MAINMAIN         MAINMAINMAIN  MAINMAIN          MAIN   *
*    MAINMAIN    MAINMAIN        MAIN  MAIN            MAIN      MAINMAIN          MAIN   *
*    MAINMAINMAINMAINMAIN       MAIN    MAIN           MAIN      MAIN  MAIN        MAIN   *
*    MAIN    MAIN    MAIN      MAIN      MAIN          MAIN      MAIN    MAIN      MAIN   *
*    MAIN            MAIN     MAINMAINMAINMAIN         MAIN      MAIN      MAIN    MAIN   *
*    MAIN            MAIN    MAIN          MAIN        MAIN      MAIN        MAIN  MAIN   *
*    MAIN            MAIN   MAIN            MAIN       MAIN      MAIN          MAINMAIN   *
*    MAIN            MAIN  MAIN              MAIN  MAINMAINMAIN  MAIN          MAINMAIN   *
*                                                                                         *
*******************************************************************************************/

int main(int argc, char* argv[]) {
    
    //INITIALIZE SERVER
    //To catch Ctrl+C (SIGINT)
    signal(SIGINT, shutdown_server);

    printf("(Server:serveur.c:main) : Initialize server...\n");
    printf("(Server:serveur.c:main) : Initialize map seed...\n");
    //Seed rand
    srand(time(NULL));
    //srand(1);
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
        int file_descr;
        int n;
        testfds = readfds;
        printf("(Server:serveur.c:main) : server waiting\n");

        while( (select_result = select(FD_SETSIZE, &testfds, (fd_set *)0, (fd_set *)0, (struct timeval *) 0)) < 1) {
            fprintf(stderr, "(Server:serveur.c:main) : error select : Fenêtre fermee brutalement cote client ?\nselect val : %d (%d)\n", select_result, errno);
            pthread_mutex_lock(&client_mutex);
            printf("(Server:serveur.c:main) : Etat de la liste des clients : \n");
            if(clients==NULL) {
                printf("(Server:serveur.c:main) : Aucun client. Erreur dans le select non geree...\n");
                exit(1);
            } else {
                int i = 1;
                client_t * client = clients;
                while(client != NULL) {
                    if(fcntl(client->socket, F_GETFL) == -1 && errno == EBADF) {
                        printf("(Server:serveur.c:main) : error on socket %d\n", client->socket); //The client with the corrupted file descriptor socket.
                        printClientsState(&client_mutex);
                        client->isConnected = 1;
                        //We must kick it from the fd_set structure (named readfds ? or testfds ? both ?)
                        FD_CLR(client->socket, &readfds);
                        FD_CLR(client->socket, &testfds);
                        FD_SET(STDIN_FILENO, &readfds); //On rajoute l'entrée clavier quand même !
                        printClientsState(&client_mutex);
                    }
                    client = client->next;
                    i++;
                }
                pthread_mutex_unlock(&client_mutex);
                continue;
            }
            pthread_mutex_unlock(&client_mutex);
        }
        
        for(file_descr = 0; file_descr < FD_SETSIZE; file_descr++) {
            if(FD_ISSET(file_descr,&testfds)) { //Si une activité est détecté sur un socket
                if(file_descr == socket_server)
                 { //Activité socket serveur : quelqu'un essaie de se connecter.
                    client_size = sizeof(client_address);
                    socket_client = accept(socket_server, (struct sockaddr *)&client_address, &client_size);
                    FD_SET(socket_client, &readfds); //Add socket file descriptor to the set
                    printf("(Server:serveur.c:main) : New socket connection on %d\n", socket_client);
                }
                else if(file_descr==STDIN_FILENO)
                { //Activité stdin : l'administrateur parle !
                    printf("socket stdin is %d", file_descr);
                    fgets(buffer, 255, stdin);
                    printf("L'admin dit : %s\n", buffer);
                    if(strncmp(buffer, "exit",4)==0) shutdown_server(0);
                }
                else //file_descr est une socket client
                { //Activité sur un socket client : un client nous parle !
                    n = read(file_descr,buffer,255);
                    if(n == 0) {
                        FD_CLR(file_descr, &readfds);
                        FD_CLR(file_descr, &testfds);
                        FD_SET(STDIN_FILENO, &readfds); //Onrajoute l'entrée clavier quand même !
                        pthread_mutex_lock(&client_mutex);
                        client_t *client = clients;
                        while(client != NULL){
                            if(client->socket == file_descr) {
                                client->isConnected = 1;
                                break;
                            }
                            client = client->next;
                        }
                        if(client==NULL) printf("(Server:serveur.c:main) : error : client null\n");
                        pthread_mutex_unlock(&client_mutex);
                        puts("Received empty message !\n");
                        sprintf(buffer, "SORT/%s/", client->name);
                        addTask(file_descr, buffer, &task_mutex, &cond_got_task);
                        break;
                    }
                    printf("(Server:serveur.c:main) : Server received %d bytes from %d.\n", n, file_descr);
                    printf("(Server:serveur.c:main) : Server received %s from %d.\n", buffer, file_descr);
                    addTask(file_descr, buffer, &task_mutex, &cond_got_task);
                }
            }
        }
    }
    return 0;
}