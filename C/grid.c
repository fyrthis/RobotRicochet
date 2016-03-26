#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "grid.h"

int size_x = 0;
int size_y = 0;
int **grid = NULL;
char *gridStr = NULL;

/******************************************
*                                         *
*  Crée la Map à partir d'un fichier txt. *
*                                         *
*******************************************/



int readGridFromFile(char *filename) {
    FILE* file = fopen(filename, "r"); /* should check the result */
    
    if(file == NULL){
        perror("(Server:grid.c:readGridFromFile) : Error in readGridFromFile function ");
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
        printf("(Server:grid.c:readGridFromFile) : size_x: %s\n", pch);
        pch = strtok(NULL, " \n");
        size_y = atoi(pch);
        printf("(Server:grid.c:readGridFromFile) : size_y: %s\n", pch);
        pch = strtok(NULL, " \n");
    }

    grid = malloc(size_x * sizeof(int *));

    int x_tmp;
    for(x_tmp = 0; x_tmp < size_x; x_tmp++){
        grid[x_tmp] = malloc(size_y * sizeof(int));
        if(grid[x_tmp] == NULL){
            printf("(Server:grid.c:readGridFromFile) : Failure to allocate for grid[%d]\n", x_tmp);
            exit(0);
        }
    }
    gridStr = malloc(4096*sizeof(char));
    if(gridStr == NULL){
        printf("(Server:grid.c:readGridFromFile) : Failure to allocate for gridStr\n");
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
            caseToChar = getCharFromCase(grid[x][y], x, y);

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

    // fprintf(stderr, "%s", gridStr);

    /* may check feof here to make a difference between eof and io failure -- network
       timeout for instance */

    fclose(file);

    return 0;

}


int isValideSolution(char *deplacements) {
    int ok = 0;

    fprintf(stderr, "(Server:grid.c:isValideSolution) : le serveur a reçu comme deplacement : %s, et la chaine de caractere fait %d\n", deplacements, strlen(deplacements));

    // deplacements de la forme: RDRHVDVHVDRB
    int index = 0;
    char color = " ";
    char direction = " ";

    while(index < strlen(deplacements)){
        color = deplacements[index];
        direction = deplacements[index+1];
        moveRobot(color, direction);

        index+=2;
    }

    return ok;
}


int moveRobot(char color, char direction) {
    switch(color){
        case 'R':
            switch(direction) {
                case 'H':
                    break;
                case 'B':
                    break;
                case 'G':
                    break;
                case 'D':
                    break;
                default:;
            }
            break;
        case 'B':
            switch(direction) {
                case 'H':
                    break;
                case 'B':
                    break;
                case 'G':
                    break;
                case 'D':
                    break;
                default:;
            }
            break;
        case 'J':
            switch(direction) {
                case 'H':
                    break;
                case 'B':
                    break;
                case 'G':
                    break;
                case 'D':
                    break;
                default:;
            }
            break;
        case 'V':
            switch(direction) {
                case 'H':
                    break;
                case 'B':
                    break;
                case 'G':
                    break;
                case 'D':
                    break;
                default:;
            }
            break;
        default:;
    }
}