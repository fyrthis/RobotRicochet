#include <stdio.h>
#include <stdlib.h>

#include <string.h>

#include "grid.h"


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
