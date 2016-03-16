#include <stdio.h>       /* standard I/O routines                     */
#define __USE_GNU 
#include <pthread.h>     /* pthread functions and data structures     */

#include <stdlib.h>      /* rand() and srand() functions              */

#include <netdb.h>
#include <netinet/in.h>
#include <string.h>

#include <sys/time.h>
#include <sys/ioctl.h>

/* CONSTANTS */
#define NB_MAX_THREADS 8
#define NB_MAX_CLIENTS 50

/*MUTEX & CONDITIONS*/
// MAC O X
#ifdef __APPLE__
pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
pthread_cond_t  cond_got_task   = PTHREAD_COND_INITIALIZER;

// WINDOWS
#elif __MINGW32__

// LINUX
#elif __linux__
pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
pthread_mutex_t client_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
pthread_cond_t  cond_got_task   = PTHREAD_COND_INITIALIZER;
#endif


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

task_t * requests = NULL;
task_t * last_request = NULL;

int nbTask = 0;
int nbClients = 0;

int size_x = 0, size_y = 0;
int **grid;

/*FUNCTIONS*/
void addClient(int socket, char *name, pthread_mutex_t* p_mutex);
void rmClient(int socket, pthread_mutex_t* p_mutex);
client_t *findClient(int socket, char * name);
void printClientsState(pthread_mutex_t* p_mutex);

void addTask(int socket, char *command, pthread_mutex_t* p_mutex, pthread_cond_t*  p_cond_var);
task_t * getTask(pthread_mutex_t* p_mutex);
void handle_request(task_t * a_request, int thread_id);
void * handle_requests_loop(void* data);
void printTasksState(pthread_mutex_t* p_mutex);

void connect1(char * name);
void disconnect1(char * name);

int strcicmp(char const *a, char const *b);

int readGridFromFile(char *filename);

int sendGrid(int socket);


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

void addClient(int socket, char *name, pthread_mutex_t* p_mutex) {
    int rc;
    client_t * client;
    rc = pthread_mutex_lock(p_mutex);
    if((client=findClient(socket, name))!=NULL) {
        if(client->isConnected==0) { //Client déjà connecté
            printf("Client %s deja connecte !\n", name);
            rc = pthread_mutex_unlock(p_mutex);
        } else { //Précédemment connecté, reprise de partie
            client->isConnected=0;
            printf("Client %s se reconnecte !\n", name);
            rc = pthread_mutex_unlock(p_mutex);
        }
        return;
    }
    /*nouveau client*/
    client = (client_t*)malloc(sizeof(client_t));
    if (!client) { /* malloc failed */
	fprintf(stderr, "add_request: out of memory\n");
	exit(1);
    }
    client->socket = socket;
    client->name = name;
    client->isConnected = 0;
    client->next = NULL;

    

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

    /* unlock mutex */
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
//TODO : Retirer un client de la liste (des connectés) i.e. mettre flag connecté à false.

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
        if(strcicmp(name, client->name)==0 || socket==client->socket) {
            return client;
        }
        client = client->next;
    }
    return NULL;
}

void printClientsState(pthread_mutex_t* p_mutex) {
    int rc = pthread_mutex_lock(p_mutex);
    
    printf("Etat de la liste des clients : \n");
    if(clients==NULL) {
        printf("Aucun client.\n");
    } else {
        int i = 1;
        client_t * client = clients;
        while(client != NULL) {
            printf("client %d : %s sur socket %d\n", i, client->name, client->socket);
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
    int rc;
    task_t * a_request;

    /* create structure with new request */
    a_request = (task_t*)malloc(sizeof(task_t));
    if (!a_request) { /* malloc failed?? */
	   fprintf(stderr, "add_request: out of memory\n");
	   exit(1);
    }
    a_request->socket = socket;
    a_request->command = command;
    a_request->next = NULL;

    rc = pthread_mutex_lock(p_mutex);

    if (nbTask == 0) {
	   requests = a_request;
	   last_request = a_request;
    }
    else {
	   last_request->next = a_request;
	   last_request = a_request;
    }

    nbTask++;

    printf("add_request: added request with socket '%d'\n", a_request->socket);
    fflush(stdout);

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
    task_t * a_request;

    rc = pthread_mutex_lock(p_mutex);

    if (nbTask > 0) {
	   a_request = requests;
	   requests = a_request->next;
	   if (requests == NULL) {
	       last_request = NULL;
	   }
	   nbTask--;
    }
    else {
	   a_request = NULL;
    }

    /* unlock mutex */
    rc = pthread_mutex_unlock(p_mutex);

    /* return the request to the caller. */
    return a_request;
}

/******************************************
*                                         *
*  Traite la tâche passée en paramètre :  *
*  1) Parse la commande de la tâche       *
*  2) Appelle la fonction associée        *
*                                         *
*******************************************/
void handle_request(task_t * a_request, int thread_id) {
    if (a_request) {
        char *str = (char*)malloc(10*sizeof(char));
        //a_request->func_ptr("toto");
	    printf("Thread '%d' handled request '%d'\n",
	    thread_id, a_request->socket);
	    fflush(stdout);
        /* parse la commande */
        char * pch = NULL; // TOKEN
        char* username = NULL;
        printf ("Splitting string \"%s\" into tokens:\n",a_request->command);
        pch = strtok (a_request->command,"/");

        /*Dispatche*/
        if(pch==NULL)
        {
            printf("ERROR : received shit\n");
        }
        //C -> S : CONNEXION/user/
        else if(strcmp(pch,"CONNEXION")==0)
        {
            pch = strtok (NULL, "/");
            username = (char*)malloc(sizeof(pch));
            strncpy(username, pch, sizeof(username));
            addClient(a_request->socket, username, &client_mutex);
            fprintf(stderr, "handle task CONNEXION\n");

            //S -> C : SESSION/plateau/
            sendGrid(a_request->socket);
        }
        //C -> S : SORT/user/
        else if(strcmp(pch,"SORT")==0)
        {
            //ICI SE FAIT LA SUPPRESSION D'UN CLIENT
            pch = strtok (NULL, "/");
            username = (char*)malloc(sizeof(pch));
            strncpy(username, pch, sizeof(username));
            printf(" handle Task SORT\n");
        }
        //C -> S : SOLUTION/user/coups/
        else if(strcmp(pch,"SOLUTION")==0)
        {
            pch = strtok (NULL, "/");
            username = (char*)malloc(sizeof(pch));
            strncpy(username, pch, sizeof(username));
        }
        //C -> S : ENCHERE/user/coups/
        else if(strcmp(pch,"ENCHERE")==0)
        {
            pch = strtok (NULL, "/");
            username = (char*)malloc(sizeof(pch));
            strncpy(username, pch, sizeof(username));
        }
        //C -> S : SOLUTION/user/deplacements/
        else if(strcmp(pch,"SOLUTION")==0)
        {
            pch = strtok (NULL, "/");
            username = (char*)malloc(sizeof(pch));
            strncpy(username, pch, sizeof(username));
        }
        else {
            printf("ERROR : received shit\n");
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
void * handle_requests_loop(void* data) {
    int rc;
    task_t * taskWeDo;
    int thread_id = *((int*)data);

    /* lock the mutex, to access the requests list exclusively. */
    rc = pthread_mutex_lock(&task_mutex);

    while (1) {
	   if (nbTask > 0) { /* a request is pending */
	       taskWeDo = getTask(&task_mutex);
	       if (taskWeDo) { /* got a request - handle it and free it */
		      handle_request(taskWeDo, thread_id);
		      free(taskWeDo);
	       }
	   }
	   else {
	       rc = pthread_cond_wait(&cond_got_task, &task_mutex);
	   }
    }
}







/***************
*     MAIN     *
****************/



int main(int argc, char* argv[]) {
    //INITIALIZE SERVER
    printf("Initialize server...\n");
    printf("Initialize threads...\n");
    int        i;                                /* loop counter          */
    int        thr_id[NB_MAX_THREADS];                /* thread IDs            */
    pthread_t  p_threads[NB_MAX_THREADS];             /* thread's structures   */
    for (i=0; i<NB_MAX_THREADS; i++) {
	thr_id[i] = i;
	pthread_create(&p_threads[i], NULL, handle_requests_loop, (void*)&thr_id[i]);
        printf("Thread %d created and ready\n", i);
    }
    printf("Initialize server socket...\n");
    int port = 2016;
    int socket_server;
    int socket_client;
    struct sockaddr_in server_address;
    struct sockaddr_in client_address = { 0 };
    int client_size;// = sizeof(client_address);
    if(argc>1) {
        port = atoi(argv[1]);
    }
    printf("setting port : %d\n", port);
    socket_server = socket(AF_INET, SOCK_STREAM, 0);
   
    if (socket_server < 0) {
        perror("ERROR opening server socket\n");
        exit(1);
    } else {
        puts("The server socket is now open\n");
    }

    bzero((char *) &server_address, sizeof(server_address));
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(port);
    if (bind(socket_server, (struct sockaddr *) &server_address, sizeof(server_address)) < 0) {
       perror("ERROR on binding\n");
       exit(1);
    }


    printf("Start listenning with %d simultaneously clients max\n",NB_MAX_CLIENTS);
    listen(socket_server,NB_MAX_CLIENTS);
    //What is it for ?/////////////////////
    fd_set readfds, testfds;
    FD_ZERO(&readfds);
    FD_SET(socket_server, &readfds);
    int select_result;



    // INITIALIZE GRID
    // argv[2] contains the filename to be parsed
    readGridFromFile(argv[2]);

    ///////////////////////////////////
    while(1) {
        char buffer[256];
        bzero(buffer,256);
        int socket;
        int nread;
        int n;
        testfds = readfds;
        printClientsState(&client_mutex);
        printf("server waiting\n");
        select_result = select(FD_SETSIZE, &testfds, (fd_set *)0, (fd_set *)0, (struct timeval *) 0);
        if(select_result < 1) {
            perror("server5");
            exit(1);
        }
        
        for(socket = 0; socket < FD_SETSIZE; socket++) {
            if(FD_ISSET(socket,&testfds)) {
                if(socket == socket_server) {
                    client_size = sizeof(client_address);
                    socket_client = accept(socket_server, (struct sockaddr *)&client_address, &client_size);
                    FD_SET(socket_client, &readfds);
                    printf("adding client on fd %d\n", socket_client);
                }
                else {
                    n = read(socket,buffer,255);
                    addTask(socket, buffer, &task_mutex, &cond_got_task);
                    //sleep(5);
                    printf("serving client on fd %d : %s\n", socket, buffer);
                }
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
    write(socket,grid,sizeof(grid));
    fprintf(stderr, "Grid send\n");
    return 0;
}

/******************************************
*                                         *
*  Crée la Map à partir d'un fichier txt. *
*                                         *
*******************************************/

int readGridFromFile(char *filename) {
    // le fichier est passé en parametre
    // si pas de parametre, alors le fichier est BasicGrid.txt
    if(filename == NULL){
        filename = "../res/BasicGrid.txt";
        fprintf(stderr, "%s\n", filename);
    }
    FILE* file = fopen(filename, "r"); /* should check the result */
    
    if(file == NULL){
        fprintf(stderr, "Error: Could not open file\n");
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
    int x = 0;
    int y = 0;

    // Get the grid informations
    while (fgets(line, sizeof(line), file)) {
       if(strncmp(line, "END", 3) == 0)
            break;

        char * pch = strtok(line, " ");

        while(pch != NULL){
            grid[x][y] = atoi(pch);
            pch = strtok(NULL, " ");
            y++;
            if(y == size_x){
                y = 0;
                x++;
            }
        }
    }

    int i = 0, j = 0;

    for(i = 0; i < size_x; i++){
        for(j = 0; j < size_y; j++){
            printf("%d ", grid[i][j]);
        }
        printf("\n");
    }

    /* may check feof here to make a difference between eof and io failure -- network
       timeout for instance */

    fclose(file);

    return 0;

}