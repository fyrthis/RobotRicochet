#include <stdio.h>
#include <stdlib.h>

#include <string.h>


typedef struct task {
    int socket;
    char *command;
    struct task * next;
}task_t;

task_t * tasks = NULL;
task_t * last_task = NULL;

int nbTasks = 0;


void addTask(int socket, char *command, pthread_mutex_t* p_mutex, pthread_cond_t*  p_cond_var);
task_t * getTask(pthread_mutex_t* p_mutex);
void printTasksState(pthread_mutex_t* p_mutex);
