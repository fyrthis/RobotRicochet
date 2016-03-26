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
    char color = ' ';
    char direction = ' ';

    while(index < strlen(deplacements)){
        color = deplacements[index];
        direction = deplacements[index+1];
        fprintf(stderr, "color : %c, direction : %c", color, direction);
        if(moveRobot(color, direction) != 0) {
            perror("error in isValideSolution ");
        }

        index+=2;
    }

    switch(lettreCible){
        case 'R':
            if(x_r != x_cible || y_r != y_cible)
                ok = -1;
            break;
        case 'B':
            if(x_b != x_cible || y_b != y_cible)
                ok = -1;
            break;
        case 'J':
            if(x_j != x_cible || y_j != y_cible)
                ok = -1;
            break;
        case 'V':
            if(x_v != x_cible || y_v != y_cible)
                ok = -1;
            break;
        default:;
    }

    return ok;
}


int moveRobot(char color, char direction) {
    int tmp_x = -1;
    int tmp_y = -1;
    switch(color){
        case 'R':
            switch(direction) {
                case 'H':
                    tmp_x = x_r;
                    for(tmp_y = y_r; tmp_y >= 0; tmp_y--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_r = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    tmp_x = x_r;
                    for(tmp_y = y_r; tmp_y < size_y; tmp_y++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_r = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    tmp_y = y_r;
                    for(tmp_x = x_r; tmp_x >= 0; tmp_x--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_r = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    tmp_y = y_r;
                    for(tmp_x = x_r; tmp_x < size_x; tmp_x++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_r = tmp_x;
                            break;
                        }
                    }
                    break;
                default:;
            }
            break;
        case 'B':
            switch(direction) {
                case 'H':
                    tmp_x = x_b;
                    for(tmp_y = y_b; tmp_y >= 0; tmp_y--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_b = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    tmp_x = x_b;
                    for(tmp_y = y_b; tmp_y < size_y; tmp_y++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_b = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    tmp_y = y_b;
                    for(tmp_x = x_b; tmp_x >= 0; tmp_x--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_b = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    tmp_y = y_b;
                    for(tmp_x = x_b; tmp_x < size_x; tmp_x++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_b = tmp_x;
                            break;
                        }
                    }
                    break;
                default:;
            }
            break;
        case 'J':
            switch(direction) {
                case 'H':
                    tmp_x = x_j;
                    for(tmp_y = y_j; tmp_y >= 0; tmp_y--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_j = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    tmp_x = x_j;
                    for(tmp_y = y_j; tmp_y < size_y; tmp_y++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_j = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    tmp_y = y_j;
                    for(tmp_x = x_j; tmp_x >= 0; tmp_x--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_j = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    tmp_y = y_j;
                    for(tmp_x = x_j; tmp_x < size_x; tmp_x++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_j = tmp_x;
                            break;
                        }
                    }
                    break;
                default:;
            }
            break;
        case 'V':
            switch(direction) {
                case 'H':
                    tmp_x = x_v;
                    for(tmp_y = y_v; tmp_y >= 0; tmp_y--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_v = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    tmp_x = x_v;
                    for(tmp_y = y_v; tmp_y < size_y; tmp_y++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            y_v = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    tmp_y = y_v;
                    for(tmp_x = x_v; tmp_x >= 0; tmp_x--) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_v = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    tmp_y = y_v;
                    for(tmp_x = x_v; tmp_x < size_x; tmp_x++) {
                        if(grid[tmp_x][tmp_y] != 0) {
                            x_v = tmp_x;
                            break;
                        }
                    }
                    break;
                default:;
            }
            break;
        default:;
    }
    return 0;
}