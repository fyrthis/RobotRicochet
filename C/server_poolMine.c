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

/* CONSTANTS */
#define NB_MAX_THREADS 8
#define NB_MAX_CLIENTS 50

/*MUTEX & CONDITIONS*/
#ifdef _WIN32
    // Windows (x64 and x86)
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#elif __unix__ // all unices, not all compilers
    // Unix
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __linux__
    // linux
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __APPLE__
    // Mac OS
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
    pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#endif

pthread_cond_t  cond_got_task   = PTHREAD_COND_INITIALIZER;

/*STRUCTURES*/
typedef struct client {
    int socket;
    int isConnected; //0 if true
    char *name;
    int score;
    struct client * next;
}client_t;

typedef struct task {
    int socket;
    char *command;
    struct task * next;
}task_t;

/*LINKED LISTS*/
client_t * clients = NULL;
client_t * last_client = NULL;

task_t * tasks = NULL;
task_t * last_task = NULL;

int nbTasks = 0;
int nbClients = 0;
int nbClientsConnecte = 0;

int size_x = 0, size_y = 0;
int **grid;
char *gridStr;

char *enigma;
char *bilan;

int phase = 0;
int nbTour = 1;
int currentSolution = -1;
char *activePlayer;

// Variable qui permet de savoir si le client qui se connecte
// est le premier ou pas
int firstLaunch = 0;

// Coordonnées des robots    
int x_r = -1;
int y_r = -1;
int x_b = -1;
int y_b = -1;
int x_j = -1;
int y_j = -1;
int x_v = -1;
int y_v = -1;
int x_cible = -1;
int y_cible = -1;

char lettreCible;

/*FUNCTIONS*/
void addClient(int socket, char *name, pthread_mutex_t* p_mutex);
void rmClient(int socket, pthread_mutex_t* p_mutex);
client_t *findClient(int socket, char * name);
void printClientsState(pthread_mutex_t* p_mutex);

void addTask(int socket, char *command, pthread_mutex_t* p_mutex, pthread_cond_t*  p_cond_var);
task_t * getTask(pthread_mutex_t* p_mutex);
void handle_request(task_t * task, int thread_id);
void * handle_tasks_loop(void* data);
void printTasksState(pthread_mutex_t* p_mutex);

void connect1(char * name);
void disconnect1(char * name);

int strcicmp(char const *a, char const *b);
char * append_strings(const char * old, const char * new);

int sendGrid(int socket);
int readGridFromFile(char *filename);
char * getCharFromCase(int i, int j);

void sendMessageAll(char *msg, pthread_mutex_t* p_mutex);
void sendMessageAllExceptOne(char *msg, char *name, pthread_mutex_t* p_mutex);

int setEnigma();
int sendEnigma(int socket);

int setBilan();
int sendBilan(int socket);

int setBilanCurrentSession();

/***************
* CLIENTS PART *
****************/

/******************************************
*                                         *
*  Ajoute un client dans la liste des     *
*  clients. Opération protégée par un     *
*  mutex afin  de s'assurer qu'une seule  *
*  entité puisse ajouter un client à la   *
*  fois, i.e. que quelqu'un ne soit pas   *
*  présent deux fois dans la liste.       *
*                                         *
*******************************************/
/*
3 cas possibles :
1) Le client est déjà connecté (son nom, OU sa socket est déjà en cours d'utilisation), dans ce cas on ne fait rien
2) Le client n'est pas connecté, mais l'a été AU COURS DE LA SESSION COURANTE, on met simplement son tag isConnected à vrai.
3) Le client n'est pas connecté et nest pas dans le cas (2), on crée un client qu'on met dans la liste
*/

void addClient(int socket, char *name, pthread_mutex_t* p_mutex) {
    client_t * client = NULL;
    if(pthread_mutex_lock(p_mutex) != 0) perror("mutex error");
    if((client=findClient(socket, name))!=NULL) {
    /*Client déjà connecté*/
        if(client->isConnected==0) {
            printf("(addClient)Client %s deja connecte !\n", name);
            if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");
    /*Précédemment connecté, reprise de partie*/
        } else {
            client->isConnected=0;
            client->socket = socket;
            printf("(addClient)Client %s se reconnecte !\n", name);
            if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");
            nbClientsConnecte++;
        }
        return;
    }
    /*nouveau client*/
    client = (client_t*)malloc(sizeof(client_t));
    if (!client) {
	fprintf(stderr, "(addClient) out of memory.\n");
	exit(1);
    }
    client->socket = socket;
    client->name = name;
    client->isConnected = 0;
    client->score = 0;
    client->next = NULL;

    
    /* M.a.j. de la liste*/
    if (nbClients == 0) {
        clients = client;
        last_client = client;
    }
    else {
        last_client->next = client;
        last_client = client;
    }

    nbClients++;

    printf("add_request: added client with socket '%d'\n", client->socket);
    fflush(stdout);

    printClientsState(&client_mutex);
    if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");
}

/******************************************
*                                         *
*  Enlève un client dans la liste des     *
*  clients. Opération protégée par un     *
*  mutex.                                 *
*                                         *
*******************************************/

void rmClient(int socket, pthread_mutex_t* p_mutex) {
//TODO : Retirer un client de la liste.
//TODO : ce n'est pas ici qu'on envoie le message et qu'on clore la socket !!
    printClientsState(&client_mutex);
}

/******************************************
*                                         *
*  Retourne le client dans la liste des   *
*  clients. Opération protégée par un     *
*  mutex afin  de s'assurer qu'une seule  *
*  entité accède aux clients à la fois,   *
*  renvoie NULL si non trouvé.            *
*                                         *
*******************************************/
//Mutex dans addClient
client_t *findClient(int socket, char * name) {
    client_t * client = clients;
    while(client != NULL) {
        if(strcicmp(name, client->name)==0/* || socket==client->socket*/) {
            return client;
        }
        client = client->next;
    }
    return NULL;
}

void printClientsState(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) != 0) perror("mutex error");
    
    printf("Etat de la liste des %d clients : \n", nbClients);
    if(clients==NULL) {
        printf("Aucun client.\n");
    } else {
        int i = 1;
        client_t * client = clients;
        while(client != NULL) {
            printf("client %d : [name:%s ; socket:%d ; connected:", i, client->name, client->socket);
            if(client->isConnected==0) {
                puts("true].\n");
            } else {
                puts("false].\n");
            }
            client = client->next;
            i++;
        }
    }
    if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");
}



/***************
*  TASKS PART  *
****************/

/******************************************
*                                         *
*  Ajoute une tâche dans la liste des     *
*  tâches. Opération protégée par un      *
*  mutex afin  de s'assurer qu'une seule  *
*  entité puisse ajouter une tâche à la   *
*  fois.                                  *
*                                         *
*******************************************/
void addTask(int socket, char *command, pthread_mutex_t* p_mutex, pthread_cond_t*  p_cond_var) {
    task_t * task;

    /* create structure with new request */
    task = (task_t*)malloc(sizeof(task_t));
    if (!task) {
       fprintf(stderr, "add_request: out of memory\n");
       exit(1);
    }
    task->socket = socket;
    task->command = (char*)calloc(strlen(command)+1, sizeof(char));
    if (!task->command) {
       fprintf(stderr, "(addTask) out of memory\n");
       exit(1);
    }
    strncpy(task->command, command, sizeof(char) * strlen(command));
    task->next = NULL;

    printf("(addTask) socket : %d \n", task->socket);
    printf("(addTask) command : %s\n", task->command);

    if(pthread_mutex_lock(p_mutex) != 0) perror("mutex error");

    if (nbTasks == 0) {
       tasks = task;
       last_task = task;
    }
    else {
       last_task->next = task;
       last_task = task;
    }

    nbTasks++;

    printTasksState(&task_mutex);
    /* unlock mutex */
    if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");

    /* signal the condition variable - there's a new request to handle */
    if(pthread_cond_signal(p_cond_var) != 0) perror("mutex error");
}

/******************************************
*                                         *
*  Récupère une tâche dans la liste des   *
*  tâches. Opération protégée par un      *
*  mutex afin  de s'assurer qu'une seule  *
*  entité puisse récupérer une tâche à la *
*  fois.                                  *
*                                         *
*******************************************/
task_t * getTask(pthread_mutex_t* p_mutex) {
    task_t * task;

    if(pthread_mutex_lock(p_mutex) != 0) perror("mutex error");

    if (nbTasks > 0) {
        task = tasks;
        tasks = task->next;
    if (tasks == NULL) {
        last_task = NULL;
    }
        nbTasks--;
    }
    else {
        task = NULL;
    }

    printTasksState(&task_mutex);
    /* unlock mutex */
    if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");

    /* return the request to the caller. */
    return task;
}

void sendMessageAll(char *msg, pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) != 0) perror("mutex error");
    
    if(clients==NULL) {
        printf("(SendMessageAll)Aucun client. Should never happened\n");
    //} else if(clients) { TODO : Si qu'un client
    //    printf("(SendMessageAll)Un seul client. Should never happened\n");
    } else {
        client_t * client = clients;
        while(client != NULL) {
            if(client->isConnected==0) {
                write(client->socket,msg,strlen(msg)*sizeof(char));
            }
            client = client->next;
        }
    }
    if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");
}

void sendMessageAllExceptOne(char *msg, char *name, pthread_mutex_t* p_mutex) { //Except client with this name
    if(pthread_mutex_lock(p_mutex) != 0) perror("mutex error");
    fprintf(stderr, "\ttest : %s\n", msg);
    if(clients==NULL) {
        printf("(SendMessageAll)Aucun client. Should never happened\n");
    } else {
        client_t * client = clients;
        while(client != NULL) {
            if(client->isConnected==0 && strcmp(client->name, name)!=0) {
                fprintf(stderr, "Sending message to %s...\n", client->name);
                write(client->socket,msg,strlen(msg)*sizeof(char));
                fprintf(stderr, "Send message to %s !\n", client->name);
            }
            client = client->next;
        }
    }
    if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");
}

/******************************************
*                                         *
*  Traite la tâche passée en paramètre :  *
*  1) Parse la commande de la tâche       *
*  2) Appelle la fonction associée        *
*                                         *
*******************************************/
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

            //S -> C : BIENVENUE/user/
            char *msg = (char*)malloc((12+strlen(username))*sizeof(char));
            strcpy(msg, "BIENVENUE/");
            strcat(msg, username);
            strcat(msg,"/\n");
            printf("%s", msg);
            write(task->socket,msg,strlen(msg)*sizeof(char));

            //S -> C : CONNECTE/user/
            char *msg2 = (char*)malloc((11+strlen(username))*sizeof(char));
            strcpy(msg2, "CONNECTE/");
            strcat(msg2, username);
            strcat(msg2,"/\n");
            printf("%s", msg2);
            sendMessageAllExceptOne(msg2, username, &client_mutex);

            //S -> C : SESSION/plateau/
            sendGrid(task->socket);

            //S -> C : TOUR/enigme/       bilan to do
            setEnigma();
            setBilanCurrentSession();
            sendEnigma(task->socket);
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
                if(pthread_mutex_lock(&client_mutex) != 0) perror("mutex error");
                client_t *client = clients;
                while(client != NULL){
                    if(strcmp(client->name, username) == 0) {
                        client->isConnected = 1;
                        break;
                    }
                    client = client->next;
                }
                if(pthread_mutex_lock(&client_mutex) != 0) perror("mutex error");
            }
            if(pthread_mutex_lock(&client_mutex) != 0) perror("mutex error");
            nbClientsConnecte--;
            if(pthread_mutex_lock(&client_mutex) != 0) perror("mutex error");
            

            printf("(handle_request) :pch is %s\n",username);
            printf("(handle_request) : add new client %s\n", username);
            

            //S -> C : DECONNEXION/user/
            char *msg = (char*)malloc((14+strlen(username))*sizeof(char));
            strcpy(msg, "DECONNEXION/");
            strcat(msg, username);
            strcat(msg,"/\n");
            printf("%s", msg);
            sendMessageAllExceptOne(msg, username, &client_mutex);
            close(task->socket);
            printf(" handle Task SORT\n");
        }
        //C -> S : SOLUTION/user/coups/    ( a gerer plus tard : //C -> S : SOLUTION/user/deplacements/  )
        else if(strcmp(pch,"SOLUTION")==0)
        {
            if(pthread_mutex_lock(&task_mutex) != 0) perror("mutex error");

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

                fprintf(stderr, "SOLUTION TROUVEE PAR %s\n", activePlayer);
                char *msgActivePlayer = (char*)malloc(12*sizeof(char));
                strcpy(msgActivePlayer, "TUASTROUVE/");
                fprintf(stderr, "%s\n", msgActivePlayer);
                write(task->socket,msgActivePlayer,strlen(msgActivePlayer)*sizeof(char));

                // On indique aux autres players qu'un joueur a proposé une solution
                int currentSolutionLength = 1;
                if(nbTour >= 10)
                    currentSolutionLength = floor(log10(abs(currentSolutionLength))) + 1;
      
                char *msgOtherPlayers = (char*)malloc((13+strlen(activePlayer)+currentSolutionLength)*sizeof(char));
                sprintf(msgOtherPlayers, "ILATROUVE/%s/%d/", activePlayer, currentSolution);
               
                fprintf(stderr, "%s\n", msgOtherPlayers);
                
				sendMessageAllExceptOne(msgOtherPlayers, activePlayer, &client_mutex);

            }
            // sinon c'est qu'on a deja changé de phase donc le protocole d'envoi de solution a changé de
            // SOLUTION/user/coups en ENCHERE/user/coups
            else {
                char *msg = (char*)malloc(50*sizeof(char));
                sprintf(msg, "Trop tard: une solution a déjà été trouvée...\n");
                fprintf(stderr, "%s", msg);
                exit(1);
            }
            if(pthread_mutex_unlock(&task_mutex) != 0) perror("mutex error");
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
void * handle_tasks_loop(void* data) {
    task_t * taskWeDo;
    int thread_id = *((int*)data);

    /* lock the mutex, to access the tasks list exclusively. */
    if(pthread_mutex_lock(&task_mutex) != 0) perror("mutex error");

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
            if(pthread_cond_wait(&cond_got_task, &task_mutex) != 0) perror("mutex error");
        }
    }
    //Unreachable code bellow
    if(pthread_mutex_lock(&task_mutex) != 0) perror("mutex error");
}



void printTasksState(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) != 0) perror("mutex error");
    
    printf("Etat de la liste des %d taches : \n", nbTasks);
    if(tasks==NULL) {
        printf("Aucune tache.\n");
    } else {
        int i = 1;
        task_t * task = tasks;
        while(task != NULL) {
            printf("task %d : [socket:%d ; command:%s].\n", i, task->socket, task->command);
            task = task->next;
            i++;
        }
    }
    if(pthread_mutex_unlock(p_mutex) != 0) perror("mutex error");
}



/***************
*     MAIN     *
****************/



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
        readGridFromFile("../res/BasicGrid.txt");
    }
    
    printf("\nsetting port : %d\n", port);
    socket_server = socket(AF_INET, SOCK_STREAM, 0);
   
    if (socket_server < 0) {
        perror("(Main)ERROR opening server socket\n");
        exit(1);
    } else {
        puts("(Main)The server socket is now open\n");
    }

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
            if(pthread_mutex_lock(&client_mutex) != 0) perror("mutex error");
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
            if(pthread_mutex_unlock(&client_mutex) != 0) perror("mutex error");
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
                        printf("debug : 0.0");
                        FD_CLR(socket, &readfds);
                        printf("debug : 0.1");
                        FD_CLR(socket, &testfds);
                        //TODO : chercher le client responsable responsable, et isConnected = false;
                        printf("debug : 1");
                        if(pthread_mutex_lock(&client_mutex) != 0) perror("mutex error");
                        client_t *client = clients;
                        printf("debug : 2");
                        while(client != NULL){
                            printf("debug : 3");
                            if(client->socket == socket) {
                                printf("debug : 4");
                                client->isConnected = 1;
                                break;
                            }
                            printf("debug : 5");
                            client = client->next;
                        }
                        printf("debug : 6");
                        if(client==NULL) printf("should be Unreachable\n");
                        if(pthread_mutex_unlock(&client_mutex) != 0) perror("mutex error");
                        printf("should be Unreachable 1\n");
                        sprintf(buffer, "SORT/%s/", client->name);
                        printf("should be Unreachable 2\n");
                        addTask(socket, buffer, &task_mutex, &cond_got_task);
                        printf("should be Unreachable 3\n");
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

void connect1(char * name) {
    printf("connect %s\n", name);
}

void disconnect1(char * name) {
    printf("disconnect %s\n", name);
}

//Fonction de comparaison de strings, insensitifs à la casse
int strcicmp(char const *a, char const *b)
{
    for (;; a++, b++) {
        int d = tolower(*a) - tolower(*b);
        if (d != 0 || !*a)
            return d;
    }
}

char * append_strings(const char * old, const char * new)
{
    // find the size of the string to allocate
    const size_t old_len = strlen(old), new_len = strlen(new);
    const size_t out_len = old_len + new_len + 1;

    // allocate a pointer to the new string
    char *out = malloc(out_len);

    // concat both strings and return
    memcpy(out, old, old_len);
    memcpy(out + old_len, new, new_len + 1);

    return out;
}

/****************
*   GRID PART   *
*****************/

/******************************************
*                                         *
*  Crée une nouvelle tâche dans la liste  *
*  pour envoyer la grille au client       *
*                                         *
*******************************************/

int sendGrid(int socket){
    fprintf(stderr, "Sending the Grid:\n");
    char *msg = (char*)calloc(sizeof(char), 4096);
    strcpy(msg, "SESSION/");
    strcat(msg, gridStr);
    strcat(msg,"/\n");
    //printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0) {
        perror("sendgrid : error writing socket\n");
    }

    fprintf(stderr, "Grid send!\n");
    return 0;
}

/******************************************
*                                         *
*  Crée la Map à partir d'un fichier txt. *
*                                         *
*******************************************/

int readGridFromFile(char *filename) {

    FILE* file = fopen(filename, "r"); /* should check the result */
    
    if(file == NULL){
        fprintf(stderr, "Error: Could not open file\n");
        perror("error : ");
        exit(-1);
    }

    char line[128];

    // Ignore comments line
    while (fgets(line, sizeof(line), file)) {
        if(strncmp(line, "##", 2) != 0)
            break;
    }

    // Get the line size info
    char * pch = strtok(line, " ");
    while(pch != NULL){
        size_x = atoi(pch);
        //printf("size_x: %s\n", pch);
        pch = strtok(NULL, " \n");
        size_y = atoi(pch);
        //printf("size_y: %s\n", pch);
        pch = strtok(NULL, " \n");
    }

    grid = malloc(size_x * sizeof(int *));

    int x_tmp;
    for(x_tmp = 0; x_tmp < size_x; x_tmp++){
        grid[x_tmp] = malloc(size_y * sizeof(int));
        if(grid[x_tmp] == NULL){
            printf("\nFailure to allocate for grid[%d]\n", x_tmp);
            exit(0);
        }
    }
    gridStr = malloc(4096*sizeof(char));
    if(gridStr == NULL){
        printf("\nFailure to allocate for gridStr\n");
        exit(0);
    }

    int x = 0;
    int y = 0;

    *gridStr = '\0';
    
    // Get the grid informations
    while (fgets(line, sizeof(line), file)) {
       if(strncmp(line, "END", 3) == 0)
            break;

        char * pch = strtok(line, " ");

        while(pch != NULL){
            grid[x][y] = atoi(pch);

            char * caseToChar = calloc(sizeof(char), 27);
            caseToChar = getCharFromCase(x,y);

            strcat(gridStr, caseToChar);
            pch = strtok(NULL, " ");
            y++;
            if(y == size_x){
                y = 0;
                x++;
            }
        }
    }

    // Concat the size of the grid at the end of the grid: SESSION/plateau/size_x/size_y/
    char * sizeInfo = malloc(7);
    sprintf(sizeInfo,"/%d/%d", size_x, size_y); 
    strcat(gridStr, sizeInfo);
            
    /*
    int i = 0, j = 0;
    for(i = 0; i < size_x; i++){
        for(j = 0; j < size_y; j++){
            printf("%d ", grid[i][j]);
        }
        printf("\n");
    }*/  

    //fprintf(stderr, "%s", gridStr);

    /* may check feof here to make a difference between eof and io failure -- network
       timeout for instance */

    fclose(file);

    return 0;

}

char* getCharFromCase(int i, int j){
    char* chaine = calloc(sizeof(char), (2*8 + 20 + 1));
    switch(grid[i][j]){
        case 0:
            //fprintf(stderr, "%s", chaine);
            break;
        case 1:
            sprintf(chaine, "(%d,%d,H)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 2:
            sprintf(chaine, "(%d,%d,D)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 3:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 4:
            sprintf(chaine, "(%d,%d,B)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 5:
            sprintf(chaine, "(%d,%d,H)(%d,%d,B)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 6:
            sprintf(chaine, "(%d,%d,D)(%d,%d,B)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 7:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,B)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 8:
            sprintf(chaine, "(%d,%d,G)", i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 9:
            sprintf(chaine, "(%d,%d,H)(%d,%d,G)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 10:
            sprintf(chaine, "(%d,%d,D)(%d,%d,G)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 11:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,G)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 12:
            sprintf(chaine, "(%d,%d,B)(%d,%d,G)", i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 13:
            sprintf(chaine, "(%d,%d,H)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 14:
            sprintf(chaine, "(%d,%d,D)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        case 15:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j, i, j);
            //fprintf(stderr, "%s", chaine);
            break;
        default:;
    }
    return chaine;
}


/******************
*   ENIGMA PART   *
*******************/

/******************************************
*                                         *
*  Envoie une nouvelle enigme/bilan au    *
*  client                                 *
*                                         *
*******************************************/

int sendEnigma(int socket){
    fprintf(stderr, "Sending the Enigma:\n");
    char *msg = (char*)calloc(sizeof(char), strlen(enigma)+strlen(bilan)+7);
    strcpy(msg, "TOUR/");
    strcat(msg, enigma);
    strcat(msg,"/");
    strcat(msg, bilan);
    strcat(msg,"/\n");

    printf("%s", msg);
    if(write(socket,msg,strlen(msg)*sizeof(char)) < 0) {
        perror("(sendEnigma l.1039): Error writing socket");
    }

    fprintf(stderr, "Enigma + bilan send!\n");
    return 0;
}

/*********************************************
*                                            *
*  Crée une nouvelle enigme en initialisant  *
*  les coordoonées du robot aléatoirement    *
*                                            *
*********************************************/

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