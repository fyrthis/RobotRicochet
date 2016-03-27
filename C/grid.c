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
    int nbCoups = 0;

    fprintf(stderr, "(Server:grid.c:isValideSolution) : le serveur a reçu comme deplacement : %s, et la chaine de caractere fait %d\n", deplacements, strlen(deplacements));

    // deplacements de la forme: RDRHVDVHVDRB
    int index = 0;
    char color = ' ';
    char direction = ' ';

    while(index < strlen(deplacements)){
        color = deplacements[index];
        direction = deplacements[index+1];
        fprintf(stderr, "color : %c, direction : %c/t -\t r[%d,%d] - b[%d,%d] - j[%d,%d] - v[%d,%d]\n", color, direction, x_r,y_r,x_b,y_b,x_j,y_j,x_v,y_v);
        if(moveRobot(color, direction) != 0) {
            perror("error in calling moveRobot function - wrong move");
            return -2;
        }
        nbCoups++;
        index+=2;
    }

    switch(lettreCible){
        case 'R':
            if(x_r != x_cible || y_r != y_cible)
                return -1;
            break;
        case 'B':
            if(x_b != x_cible || y_b != y_cible)
                return -1;
            break;
        case 'J':
            if(x_j != x_cible || y_j != y_cible)
                return -1;
            break;
        case 'V':
            if(x_v != x_cible || y_v != y_cible)
                return -1;
            break;
        default:;
    }
    fprintf(stderr, "Le nombre de coups de la solution : %d\n", nbCoups);
    return nbCoups;
}


int moveRobot(char color, char direction) {
    int tmp_x = -1;
    int tmp_y = -1;
    switch(color){
        case 'R':
            switch(direction) {
                case 'H':
                    // 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
                    if(grid[x_r][y_r] == 1 || grid[x_r][y_r] ==  3 || grid[x_r][y_r] == 5 || grid[x_r][y_r] == 7
                        || grid[x_r][y_r] == 9 || grid[x_r][y_r] == 11 || grid[x_r][y_r] == 13 || grid[x_r][y_r] == 15)
                        return -1;
                    tmp_x = x_r;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_r-1; tmp_y >= 0; tmp_y--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_r = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_r][y_r] == 4 || grid[x_r][y_r] ==  5 || grid[x_r][y_r] == 6 || grid[x_r][y_r] == 7
                        || grid[x_r][y_r] == 12 || grid[x_r][y_r] == 13 || grid[x_r][y_r] == 14 || grid[x_r][y_r] == 15)
                        return -1;
                    tmp_x = x_r;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_r+1; tmp_y < size_y; tmp_y++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_r = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_r][y_r] == 8 || grid[x_r][y_r] ==  9 || grid[x_r][y_r] == 10 || grid[x_r][y_r] == 11
                        || grid[x_r][y_r] == 12 || grid[x_r][y_r] == 13 || grid[x_r][y_r] == 14 || grid[x_r][y_r] == 15)
                        return -1;
                    tmp_y = y_r;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_r-1; tmp_x >= 0; tmp_x--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_r = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_r][y_r] == 2 || grid[x_r][y_r] ==  3 || grid[x_r][y_r] == 6 || grid[x_r][y_r] == 7
                        || grid[x_r][y_r] == 10 || grid[x_r][y_r] == 11 || grid[x_r][y_r] == 14 || grid[x_r][y_r] == 15)
                        return -1;
                    tmp_y = y_r;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_r+1; tmp_x < size_x; tmp_x++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
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
                    // 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
                    if(grid[x_b][y_b] == 1 || grid[x_b][y_b] ==  3 || grid[x_b][y_b] == 5 || grid[x_b][y_b] == 7
                        || grid[x_b][y_b] == 9 || grid[x_b][y_b] == 11 || grid[x_b][y_b] == 13 || grid[x_b][y_b] == 15)
                        return -1;
                    tmp_x = x_b;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_b-1; tmp_y >= 0; tmp_y--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_b = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_b][y_b] == 4 || grid[x_b][y_b] ==  5 || grid[x_b][y_b] == 6 || grid[x_b][y_b] == 7
                        || grid[x_b][y_b] == 12 || grid[x_b][y_b] == 13 || grid[x_b][y_b] == 14 || grid[x_b][y_b] == 15)
                        return -1;
                    tmp_x = x_b;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_b+1; tmp_y < size_y; tmp_y++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_b = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_b][y_b] == 8 || grid[x_b][y_b] ==  9 || grid[x_b][y_b] == 10 || grid[x_b][y_b] == 11
                        || grid[x_b][y_b] == 12 || grid[x_b][y_b] == 13 || grid[x_b][y_b] == 14 || grid[x_b][y_b] == 15)
                        return -1;
                    tmp_y = y_b;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_b-1; tmp_x >= 0; tmp_x--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_b = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_b][y_b] == 2 || grid[x_b][y_b] ==  3 || grid[x_b][y_b] == 6 || grid[x_b][y_b] == 7
                        || grid[x_b][y_b] == 10 || grid[x_b][y_b] == 11 || grid[x_b][y_b] == 14 || grid[x_b][y_b] == 15)
                        return -1;
                    tmp_y = y_b;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_b+1; tmp_x < size_x; tmp_x++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
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
                    // 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
                    if(grid[x_j][y_j] == 1 || grid[x_j][y_j] ==  3 || grid[x_j][y_j] == 5 || grid[x_j][y_j] == 7
                        || grid[x_j][y_j] == 9 || grid[x_j][y_j] == 11 || grid[x_j][y_j] == 13 || grid[x_j][y_j] == 15)
                        return -1;
                    tmp_x = x_j;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_j-1; tmp_y >= 0; tmp_y--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_j = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_j][y_j] == 4 || grid[x_j][y_j] ==  5 || grid[x_j][y_j] == 6 || grid[x_j][y_j] == 7
                        || grid[x_j][y_j] == 12 || grid[x_j][y_j] == 13 || grid[x_j][y_j] == 14 || grid[x_j][y_j] == 15)
                        return -1;
                    tmp_x = x_j;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_j+1; tmp_y < size_y; tmp_y++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_j = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_j][y_j] == 8 || grid[x_j][y_j] ==  9 || grid[x_j][y_j] == 10 || grid[x_j][y_j] == 11
                        || grid[x_j][y_j] == 12 || grid[x_j][y_j] == 13 || grid[x_j][y_j] == 14 || grid[x_j][y_j] == 15)
                        return -1;
                    tmp_y = y_j;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_j-1; tmp_x >= 0; tmp_x--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_j = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_j][y_j] == 2 || grid[x_j][y_j] ==  3 || grid[x_j][y_j] == 6 || grid[x_j][y_j] == 7
                        || grid[x_j][y_j] == 10 || grid[x_j][y_j] == 11 || grid[x_j][y_j] == 14 || grid[x_j][y_j] == 15)
                        return -1;
                    tmp_y = y_j;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_j+1; tmp_x < size_x; tmp_x++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
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
                    // 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
                    if(grid[x_v][y_v] == 1 || grid[x_v][y_v] ==  3 || grid[x_v][y_v] == 5 || grid[x_v][y_v] == 7
                        || grid[x_v][y_v] == 9 || grid[x_v][y_v] == 11 || grid[x_v][y_v] == 13 || grid[x_v][y_v] == 15)
                        return -1;
                    tmp_x = x_v;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_v-1; tmp_y >= 0; tmp_y--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
                            y_v = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_v][y_v] == 4 || grid[x_v][y_v] ==  5 || grid[x_v][y_v] == 6 || grid[x_v][y_v] == 7
                        || grid[x_v][y_v] == 12 || grid[x_v][y_v] == 13 || grid[x_v][y_v] == 14 || grid[x_v][y_v] == 15)
                        return -1;
                    tmp_x = x_v;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_v+1; tmp_y < size_y; tmp_y++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 10) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
                            y_v = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_v][y_v] == 8 || grid[x_v][y_v] ==  9 || grid[x_v][y_v] == 10 || grid[x_v][y_v] == 11
                        || grid[x_v][y_v] == 12 || grid[x_v][y_v] == 13 || grid[x_v][y_v] == 14 || grid[x_v][y_v] == 15)
                        return -1;
                    tmp_y = y_v;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_v-1; tmp_x >= 0; tmp_x--) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
                            x_v = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_v][y_v] == 2 || grid[x_v][y_v] ==  3 || grid[x_v][y_v] == 6 || grid[x_v][y_v] == 7
                        || grid[x_v][y_v] == 10 || grid[x_v][y_v] == 11 || grid[x_v][y_v] == 14 || grid[x_v][y_v] == 15)
                        return -1;
                    tmp_y = y_v;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_v+1; tmp_x < size_x; tmp_x++) {
                        if((grid[tmp_x][tmp_y] != 0 && grid[tmp_x][tmp_y] != 5) || (tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
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