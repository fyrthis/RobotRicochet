#include <stdio.h>       /* standard I/O routines                     */
#include <pthread.h>     /* pthread functions and data structures     */

#include <stdlib.h>      /* rand() and srand() functions              */


int** grid;

int readGridFromFile(char *filename);