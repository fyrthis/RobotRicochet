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

int size_x = 0, size_y = 0;
int **grid;
char *gridStr;

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

int sendGrid(int socket);
int readGridFromFile(char *filename);
char * getCharFromCase(int i, int j);

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
    int rc =0;
    client_t * client = NULL;
    rc = pthread_mutex_lock(p_mutex);
    if((client=findClient(socket, name))!=NULL) {
    /*Client déjà connecté*/
        if(client->isConnected==0) {
            printf("(addClient)Client %s deja connecte !\n", name);
            rc = pthread_mutex_unlock(p_mutex);
    /*Précédemment connecté, reprise de partie*/
        } else {
            client->isConnected=0;
            printf("(addClient)Client %s se reconnecte !\n", name);
            rc = pthread_mutex_unlock(p_mutex);
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
    rc = pthread_mutex_unlock(p_mutex);
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
    int rc = pthread_mutex_lock(p_mutex);
    
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
    rc = pthread_mutex_unlock(p_mutex);
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
    int rc = 0;
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

    rc = pthread_mutex_lock(p_mutex);

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
    rc = pthread_mutex_unlock(p_mutex);

    /* signal the condition variable - there's a new request to handle */
    rc = pthread_cond_signal(p_cond_var);
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
    int rc;
    task_t * task;

    rc = pthread_mutex_lock(p_mutex);

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
    rc = pthread_mutex_unlock(p_mutex);

    /* return the request to the caller. */
    return task;
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
            printf("(handle_request) : found CONNEXION\n");
            pch = strtok (NULL, "/");
            username = (char*)calloc(strlen(pch)+1, sizeof(char));
            strncpy(username, pch, strlen(pch));
            printf("(handle_request) :pch is %s\n",username);
            printf("(handle_request) : add new client %s\n", username);
            addClient(task->socket, username, &client_mutex);

            //S -> C : SESSION/plateau/
            char *msg = (char*)malloc((12+strlen(username))*sizeof(char));
            strcpy(msg, "BIENVENUE/");
            strcat(msg, username);
            strcat(msg,"/\n");
            printf("%s", msg);
            write(task->socket,msg,strlen(msg)*sizeof(char));
            sendGrid(task->socket);
        }
        //C -> S : SORT/user/
        else if(strcmp(pch,"SORT")==0)
        {
            //ICI SE FAIT LA SUPPRESSION D'UN CLIENT
            pch = strtok (NULL, "/");
            username = (char*)malloc(strlen(pch));
            strncpy(username, pch, strlen(pch));
            printf(" handle Task SORT\n");
        }
        //C -> S : SOLUTION/user/coups/
        else if(strcmp(pch,"SOLUTION")==0)
        {
            pch = strtok (NULL, "/");
            username = (char*)malloc(strlen(pch));
            strncpy(username, pch, strlen(pch));
        }
        //C -> S : ENCHERE/user/coups/
        else if(strcmp(pch,"ENCHERE")==0)
        {
            pch = strtok (NULL, "/");
            username = (char*)malloc(strlen(pch));
            strncpy(username, pch, strlen(pch));
        }
        //C -> S : SOLUTION/user/deplacements/
        else if(strcmp(pch,"SOLUTION")==0)
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
    int rc;
    task_t * taskWeDo;
    int thread_id = *((int*)data);

    /* lock the mutex, to access the tasks list exclusively. */
    rc = pthread_mutex_lock(&task_mutex);

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
            rc = pthread_cond_wait(&cond_got_task, &task_mutex);
        }
    }
    //Unreachable code bellow
    rc = pthread_mutex_lock(&task_mutex);
}



void printTasksState(pthread_mutex_t* p_mutex) {
    int rc = pthread_mutex_lock(p_mutex);
    
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
    rc = pthread_mutex_unlock(p_mutex);
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
    int client_size;// = sizeof(client_address);
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
        int nread;
        int n;
        testfds = readfds;
        printf("(Main)server waiting\n");

        while( (select_result = select(FD_SETSIZE, &testfds, (fd_set *)0, (fd_set *)0, (struct timeval *) 0)) < 1) {
            perror("");
            fprintf(stderr, "error select : Fenêtre fermee burtalement cote client ?\nselect val : %d (%d)\n", select_result, errno);
            int rc = pthread_mutex_lock(&client_mutex);
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
            rc = pthread_mutex_unlock(&client_mutex);
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
                        FD_CLR(socket, &readfds);
                        FD_CLR(socket, &testfds);
                        close(socket);
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
    printf("%s", msg);
    int n = write(socket,msg,strlen(msg)*sizeof(char));

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

    int infoSize = 0;
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
        printf("size_x: %s\n", pch);
        pch = strtok(NULL, " \n");
        size_y = atoi(pch);
        printf("size_y: %s\n", pch);
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
    char * sizeInfo = malloc(6);
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

    fprintf(stderr, "%s", gridStr);

    /* may check feof here to make a difference between eof and io failure -- network
       timeout for instance */

    fclose(file);

    return 0;

}

char* getCharFromCase(int i, int j){
    char* chaine = calloc(sizeof(char), (2*8 + 20 + 1));
    switch(grid[i][j]){
        case 0:
            fprintf(stderr, "%s", chaine);
            break;
        case 1:
            sprintf(chaine, "(%d,%d,H)", i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 2:
            sprintf(chaine, "(%d,%d,D)", i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 3:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)", i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 4:
            sprintf(chaine, "(%d,%d,B)", i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 5:
            sprintf(chaine, "(%d,%d,H)(%d,%d,B)", i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 6:
            sprintf(chaine, "(%d,%d,D)(%d,%d,B)", i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 7:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,B)", i, j, i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 8:
            sprintf(chaine, "(%d,%d,G)", i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 9:
            sprintf(chaine, "(%d,%d,H)(%d,%d,G)", i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 10:
            sprintf(chaine, "(%d,%d,D)(%d,%d,G)", i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 11:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,G)", i, j, i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 12:
            sprintf(chaine, "(%d,%d,B)(%d,%d,G)", i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 13:
            sprintf(chaine, "(%d,%d,H)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 14:
            sprintf(chaine, "(%d,%d,D)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        case 15:
            sprintf(chaine, "(%d,%d,H)(%d,%d,D)(%d,%d,B)(%d,%d,G)", i, j, i, j, i, j, i, j);
            fprintf(stderr, "%s", chaine);
            break;
        default:;
    }
    return chaine;
}

