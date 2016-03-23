#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include "task.h"


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

    if(pthread_mutex_lock(p_mutex) < 0){
        perror("Error : on addTask, cannot lock the p_mutex\n");
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
    if(pthread_mutex_lock(p_mutex) < 0){
        perror("Error : on addTask, cannot unlock the p_mutex\n");
    }
    /* signal the condition variable - there's a new request to handle */
    if(pthread_cond_signal(p_cond_var) < 0){
        perror("Error : on addTask, p_thread_cond_signal\n");
    }
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
        perror("Error : on getTask, cannot lock the p_mutex\n");
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

    printTasksState(&task_mutex);
    /* unlock mutex */
    if(pthread_mutex_unlock(p_mutex) < 0) {
        perror("Error : on getTask, cannot lock the p_mutex\n");
    }

    /* return the request to the caller. */
    return task;
}


void printTasksState(pthread_mutex_t* p_mutex) {
    if(pthread_mutex_lock(p_mutex) < 0) {
        perror("Error : on printTasksState, cannot lock the p_mutex\n");
    }

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
    if(pthread_mutex_lock(p_mutex) < 0) {
        perror("Error : on printTasksState, cannot unlock the p_mutex\n");
    }
}