#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "task.h"


/*MUTEX & CONDITIONS*/
#ifdef _WIN32
    // Windows (x64 and x86)
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#elif __unix__ // all unices, not all compilers
    // Unix
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __linux__
    // linux
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER_NP;
#elif __APPLE__
    // Mac OS
    pthread_mutex_t task_mutex = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
#endif

task_t * tasks = NULL;
task_t * last_task = NULL;

int nbTasks = 0;

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
       fprintf(stderr, "(Server:task.c:addTask) : add_request: out of memory\n");
       exit(1);
    }
    task->socket = socket;
    task->command = (char*)calloc(strlen(command)+1, sizeof(char));
    if (!task->command) {
       fprintf(stderr, "(Server:task.c:addTask) :  out of memory\n");
       exit(1);
    }
    strncpy(task->command, command, sizeof(char) * strlen(command));
    task->next = NULL;

    printf("(Server:task.c:addTask) :  socket : %d \n", task->socket);
    printf("(Server:task.c:addTask) :  command : %s\n", task->command);

    if(pthread_mutex_lock(p_mutex) < 0){
        perror("(Server:task.c:addTask) : on addTask, cannot lock the p_mutex\n");
    }

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
    if(pthread_mutex_unlock(p_mutex) < 0){
        perror("(Server:task.c:addTask) : on addTask, cannot unlock the p_mutex\n");
    }
    /* signal the condition variable - there's a new request to handle */
    if(pthread_cond_signal(p_cond_var) != 0) perror("(Server:task.c:addTask) : signal new task");
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

    if(pthread_mutex_lock(p_mutex) < 0) {
        perror("(Server:task.c:getTask) :  on getTask, cannot lock the p_mutex\n");
    }

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

    printTasksState(p_mutex);
    /* unlock mutex */
    if(pthread_mutex_unlock(p_mutex) < 0) {
        perror("(Server:task.c:getTask) : on getTask, cannot lock the p_mutex\n");
    }

    /* return the request to the caller. */
    return task;
}


void printTasksState(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) < 0) {
        perror("(Server:task.c:printTasksState) : on printTasksState, cannot lock the p_mutex\n");
    }

    printf("(Server:task.c:printTasksState) : Etat de la liste des %d taches : \n", nbTasks);
    if(tasks==NULL) {
        printf("(Server:task.c:printTasksState) : Aucune tache.\n");
    } else {
        int i = 1;
        task_t * task = tasks;
        while(task != NULL) {
            printf("(Server:task.c:printTasksState) : task %d : [socket:%d ; command:%s].\n", i, task->socket, task->command);
            task = task->next;
            i++;
        }
    }
    if(pthread_mutex_unlock(p_mutex) < 0) {
        perror("(Server:task.c:printTasksState) : Error : on printTasksState, cannot unlock the p_mutex\n");
    }
}