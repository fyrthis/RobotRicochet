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

    grid = calloc(size_x, sizeof(int *));

    int x_tmp;
    for(x_tmp = 0; x_tmp < size_x; x_tmp++){
        grid[x_tmp] = calloc(size_y, sizeof(int));
        if(grid[x_tmp] == NULL){
            printf("(Server:grid.c:readGridFromFile) : Failure to allocate for grid[%d]\n", x_tmp);
            exit(0);
        }
    }
    gridStr = calloc(4096, sizeof(char));
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
            x++;
            if(x == size_x){
                x = 0;
                y++;
                if(y == size_y)
                    break;
            }
        }
    }

    // Concat the size of the grid at the end of the grid: SESSION/plateau/size_x/size_y/
    int size_xLength = getIntLength(size_x);
    int size_yLength = getIntLength(size_y);
    char * sizeInfo = calloc(3 + size_xLength + size_yLength, sizeof(char));
    sprintf(sizeInfo,"/%d/%d", size_x, size_y); 
    strcat(gridStr, sizeInfo);
            
    fprintf(stderr, "BEFORE SENDING GRID TO CLIENT\n");
    int ligne = 0, colonne = 0;
    for(ligne = 0; ligne < size_y; ligne++){
        for(colonne = 0; colonne < size_x; colonne++){
            printf("%d ", grid[colonne][ligne]);
        }
        printf("\n");
    }

    // fprintf(stderr, "%s", gridStr);

    /* may check feof here to make a difference between eof and io failure -- network
       timeout for instance */

    fclose(file);

    return 0;

}


int isValideSolution(char *deplacements, int nbCoupsAnnonce) {
    int nbCoups = 0;

    fprintf(stderr, "(Server:grid.c:isValideSolution) : le serveur a reçu comme deplacement : %s, et la chaine de caractere fait %zu\n", deplacements, strlen(deplacements));

    // deplacements de la forme: RDRHVDVHVDRB
    int index = 0;
    char color = ' ';
    char direction = ' ';

    // Il faut stocker les coordonnées initiales des robots pour les replacer si la solution est mauvaise
    int initial_x_r = x_r;
    int initial_y_r = y_r;
    int initial_x_b = x_b;
    int initial_y_b = y_b;
    int initial_x_j = x_j;
    int initial_y_j = y_j;
    int initial_x_v = x_v;
    int initial_y_v = y_v;

    while(index < strlen(deplacements)){
        color = deplacements[index];
        direction = deplacements[index+1];
        fprintf(stderr, "r[%d,%d] - b[%d,%d] - j[%d,%d] - v[%d,%d] \t - \tcolor : %c, direction : %c\n",x_r,y_r,x_b,y_b,x_j,y_j,x_v,y_v, color, direction);
        if(moveRobot(color, direction) != 0) {
            perror("error in calling moveRobot function - wrong move");
            x_r = initial_x_r;
            y_r = initial_y_r;
            x_b = initial_x_b;
            y_b = initial_y_b;
            x_j = initial_x_j;
            y_j = initial_y_j;
            x_v = initial_x_v;
            y_v = initial_y_v;
            return -2;
        }
        nbCoups++;
        index+=2;
    }

    fprintf(stderr, "La cible se trouve en [%d,%d] - %c - \n", x_cible, y_cible, lettreCible);
    fprintf(stderr, "r[%d,%d] - b[%d,%d] - j[%d,%d] - v[%d,%d]\n", x_r,y_r,x_b,y_b,x_j,y_j,x_v,y_v);

    fprintf(stderr, "Le nombre de coups de la solution : %d\n", nbCoups);
    fprintf(stderr, "Le nombre de coups de proposée lors de l'enchère : %d\n", nbCoupsAnnonce);
    if((nbCoupsAnnonce-nbCoups)>=0){
        switch(lettreCible){
            case 'r':
                if(x_r == x_cible && y_r == y_cible)
                    return 0;
                break;
            case 'b':
                if(x_b == x_cible && y_b == y_cible)
                    return 0;
                break;
            case 'j':
                if(x_j == x_cible && y_j == y_cible)
                    return 0;
                break;
            case 'v':
                if(x_v == x_cible && y_v == y_cible)
                    return 0;
                break;
            default:;
        }
    }
    x_r = initial_x_r;
    y_r = initial_y_r;
    x_b = initial_x_b;
    y_b = initial_y_b;
    x_j = initial_x_j;
    y_j = initial_y_j;
    x_v = initial_x_v;
    y_v = initial_y_v;
    fprintf(stderr, "La solution n'est pas valide...\n");
    return -1;
}

/***************************************************************
*  Pour chaque robot et chaque direction:                      *
*   - on regarde la valeur de la case où se trouve le robot    *
*   avant le déplacement:                                      *
*       s'il y a un mur qui bloque tout déplacement dans la    *
*           direction donné, alors on retourne -1              *
*   - on parcourt toutes les cases de la direction donné par   *
*   rapport à l'axe qui ne change pas, et on s'arrête dès      *
*   qu'il y a un robot ou un mur sur le côté correspondant à   *
*   la direction sur cette case                                *
*   - on enregistre les nouvelles coordonnées pour le robot    *
*   correspondant.                                             *
*                                                              *
*   Il faut bien faire attention à ce que le robot ne          *
*   s'arrête pas sur les cases où il y a 2 murs parallèles,    *
*   pour lesquelles il n'y a rien qui bloque le passage du     *
*   robot.                                                     *
****************************************************************/

int moveRobot(char color, char direction) {
    int tmp_x = -1;
    int tmp_y = -1;
    switch(color){
        case 'R':
            switch(direction) {
                case 'H':
                    // 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
                    if(grid[x_r][y_r] == 1 || grid[x_r][y_r] ==  3 || grid[x_r][y_r] == 5 || grid[x_r][y_r] == 7
                        || grid[x_r][y_r] == 9 || grid[x_r][y_r] == 11 || grid[x_r][y_r] == 13 || grid[x_r][y_r] == 15){
                        return 0;   
                    }
                    tmp_x = x_r;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_r-1; tmp_y >= 0; tmp_y--) {
                        if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_r = tmp_y+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 1 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 15) {
                            y_r = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_r][y_r] == 4 || grid[x_r][y_r] ==  5 || grid[x_r][y_r] == 6 || grid[x_r][y_r] == 7
                        || grid[x_r][y_r] == 12 || grid[x_r][y_r] == 13 || grid[x_r][y_r] == 14 || grid[x_r][y_r] == 15)
                    {
                        return 0;   
                    }                    
                    tmp_x = x_r;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_r+1; tmp_y < size_y; tmp_y++) {
                        if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_r = tmp_y-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 4 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            y_r = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_r][y_r] == 8 || grid[x_r][y_r] ==  9 || grid[x_r][y_r] == 10 || grid[x_r][y_r] == 11
                        || grid[x_r][y_r] == 12 || grid[x_r][y_r] == 13 || grid[x_r][y_r] == 14 || grid[x_r][y_r] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_r;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_r-1; tmp_x >= 0; tmp_x--) {
                        if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_r = tmp_x+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 8 || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            x_r = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_r][y_r] == 2 || grid[x_r][y_r] ==  3 || grid[x_r][y_r] == 6 || grid[x_r][y_r] == 7
                        || grid[x_r][y_r] == 10 || grid[x_r][y_r] == 11 || grid[x_r][y_r] == 14 || grid[x_r][y_r] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_r;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_r+1; tmp_x < size_x; tmp_x++) {
                        if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_r = tmp_x-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 2 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
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
                    {
                        return 0;   
                    }
                    tmp_x = x_b;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_b-1; tmp_y >= 0; tmp_y--) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_b = tmp_y+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 1 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 15) {
                            y_b = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_b][y_b] == 4 || grid[x_b][y_b] ==  5 || grid[x_b][y_b] == 6 || grid[x_b][y_b] == 7
                        || grid[x_b][y_b] == 12 || grid[x_b][y_b] == 13 || grid[x_b][y_b] == 14 || grid[x_b][y_b] == 15)
                    {
                        return 0;   
                    }
                    tmp_x = x_b;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_b+1; tmp_y < size_y; tmp_y++) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_b = tmp_y-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 4 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            y_b = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_b][y_b] == 8 || grid[x_b][y_b] ==  9 || grid[x_b][y_b] == 10 || grid[x_b][y_b] == 11
                        || grid[x_b][y_b] == 12 || grid[x_b][y_b] == 13 || grid[x_b][y_b] == 14 || grid[x_b][y_b] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_b;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_b-1; tmp_x >= 0; tmp_x--) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_b = tmp_x+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 8 || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            x_b = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_b][y_b] == 2 || grid[x_b][y_b] ==  3 || grid[x_b][y_b] == 6 || grid[x_b][y_b] == 7
                        || grid[x_b][y_b] == 10 || grid[x_b][y_b] == 11 || grid[x_b][y_b] == 14 || grid[x_b][y_b] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_b;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_b+1; tmp_x < size_x; tmp_x++) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_b = tmp_x-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 2 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
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
                    {
                        return 0;   
                    }
                    tmp_x = x_j;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_j-1; tmp_y >= 0; tmp_y--) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_j = tmp_y+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 1 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 15) {
                            y_j = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_j][y_j] == 4 || grid[x_j][y_j] ==  5 || grid[x_j][y_j] == 6 || grid[x_j][y_j] == 7
                        || grid[x_j][y_j] == 12 || grid[x_j][y_j] == 13 || grid[x_j][y_j] == 14 || grid[x_j][y_j] == 15)
                    {
                        return 0;   
                    }
                    tmp_x = x_j;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_j+1; tmp_y < size_y; tmp_y++) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
                            y_j = tmp_y-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 4 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            y_j = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_j][y_j] == 8 || grid[x_j][y_j] ==  9 || grid[x_j][y_j] == 10 || grid[x_j][y_j] == 11
                        || grid[x_j][y_j] == 12 || grid[x_j][y_j] == 13 || grid[x_j][y_j] == 14 || grid[x_j][y_j] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_j;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_j-1; tmp_x >= 0; tmp_x--) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_j = tmp_x+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 8 || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            x_j = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_j][y_j] == 2 || grid[x_j][y_j] ==  3 || grid[x_j][y_j] == 6 || grid[x_j][y_j] == 7
                        || grid[x_j][y_j] == 10 || grid[x_j][y_j] == 11 || grid[x_j][y_j] == 14 || grid[x_j][y_j] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_j;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_j+1; tmp_x < size_x; tmp_x++) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
                            x_j = tmp_x-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 2 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
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
                    {
                        return 0;   
                    }
                    tmp_x = x_v;
                    // on teste toutes les cases à partir de la case au dessus du robot
                    for(tmp_y = y_v-1; tmp_y >= 0; tmp_y--) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
                            y_v = tmp_y+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 1 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 15) {
                            y_v = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'B':
                    // 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
                    if(grid[x_v][y_v] == 4 || grid[x_v][y_v] ==  5 || grid[x_v][y_v] == 6 || grid[x_v][y_v] == 7
                        || grid[x_v][y_v] == 12 || grid[x_v][y_v] == 13 || grid[x_v][y_v] == 14 || grid[x_v][y_v] == 15)
                    {
                        return 0;   
                    }
                    tmp_x = x_v;
                    // on teste toutes les cases à partir de la case en dessous du robot
                    for(tmp_y = y_v+1; tmp_y < size_y; tmp_y++) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
                            y_v = tmp_y-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 4 || grid[tmp_x][tmp_y] == 5 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            y_v = tmp_y;
                            break;
                        }
                    }
                    break;
                case 'G':
                    // 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
                    if(grid[x_v][y_v] == 8 || grid[x_v][y_v] ==  9 || grid[x_v][y_v] == 10 || grid[x_v][y_v] == 11
                        || grid[x_v][y_v] == 12 || grid[x_v][y_v] == 13 || grid[x_v][y_v] == 14 || grid[x_v][y_v] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_v;
                    // on teste toutes les cases à partir de la case à gauche du robot
                    for(tmp_x = x_v-1; tmp_x >= 0; tmp_x--) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
                            x_v = tmp_x+1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 8 || grid[tmp_x][tmp_y] == 9 || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11
                            || grid[tmp_x][tmp_y] == 12 || grid[tmp_x][tmp_y] == 13 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
                            x_v = tmp_x;
                            break;
                        }
                    }
                    break;
                case 'D':
                    // 2, 3, 6, 7, 10, 11, 14, 15 sont les valeurs des cases où il y a déjà un mur à droite
                    if(grid[x_v][y_v] == 2 || grid[x_v][y_v] ==  3 || grid[x_v][y_v] == 6 || grid[x_v][y_v] == 7
                        || grid[x_v][y_v] == 10 || grid[x_v][y_v] == 11 || grid[x_v][y_v] == 14 || grid[x_v][y_v] == 15)
                    {
                        return 0;   
                    }
                    tmp_y = y_v;
                    // on teste toutes les cases à partir de la case à droite du robot
                    for(tmp_x = x_v+1; tmp_x < size_x; tmp_x++) {
                        if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
                            x_v = tmp_x-1;
                            break;
                        }
                        else if(grid[tmp_x][tmp_y] == 2 || grid[tmp_x][tmp_y] == 3 || grid[tmp_x][tmp_y] == 6 || grid[tmp_x][tmp_y] == 7
                            || grid[tmp_x][tmp_y] == 10 || grid[tmp_x][tmp_y] == 11 || grid[tmp_x][tmp_y] == 14 || grid[tmp_x][tmp_y] == 15) {
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
    fprintf(stderr, "\tfounded : grid[%d][%d] = %d\n", tmp_x, tmp_y, grid[tmp_x][tmp_y]);
    return 0;
}